from Queue import PriorityQueue
from pykka.actor import ThreadingActor
from pykka import ActorDeadError
#from solr_processor import SolrProcessor
from preprocessor_processor import PreprocessorProcessor

import logging
log = logging.getLogger(__name__)


class CentralQueue(ThreadingActor):
    """
    Central queue that handles the queues of all processors.
    It has a preprocessor that is called before all the others.
    Every processor is called async but strictly after the preprocessor
    """
    def __init__(self, processors, preprocessor=PreprocessorProcessor):
        super(CentralQueue, self).__init__()
        self.procs = dict()
        self.qs = dict()
        self.actors = dict()
        self.preprocessor_id = None
        # Create the wrapped preprocessor
        log.info('CentralQueue starting')
        self.start_preprocessor(preprocessor, start_queue=True)
        # Create the rest of wrappers
        for proc in processors:
            self.start_processor(proc, start_queue=True)
        log.info('All Processors started')

    def start_preprocessor(self, preprocessor_class, start_queue=False):
        preprocessor = preprocessor_class()
        log.info('Preprocessor started')
        self.preprocessor_id = preprocessor.get_id()
        self.actors[self.preprocessor_id] = preprocessor
        self.preprocessor = CentralQueueWrapper.start(
            preprocessor, self.actor_ref)
        if start_queue:
            self.preprocessor_q = PriorityQueue()

    def start_processor(self, processor, start_queue=False):
        proc = processor()
        log.info('Processor %s starting', proc.get_id())
        self.actors[proc.get_id()] = processor
        self.procs[proc.get_id()] = CentralQueueWrapper.start(
            proc, self.actor_ref)
        if start_queue:
            self.qs[proc.get_id()] = PriorityQueue()

    def tell_reload(self, actor, element, key):
        try:
            actor.tell({'element': element})
        except ActorDeadError:
            log.exception("Processor %s was dead when trying "
                          "to reach it, attempt %i", key, 1)
            self.restart_processor(key)

    def tell_preprocessor(self, element):
        self.tell_reload(self.preprocessor, element, 'preprocessor')

    def restart_processor(self, processor_id):
        if processor_id == 'preprocessor':
            self.start_preprocessor(start_queue=False)
        else:
            self.start_processor(self.actors[processor_id], start_queue=False)

    def on_receive(self, msg):
        sender_id = msg['id']
        element = msg['element']
        log.debug(
            "Received message that %s from sender_id %s",
            repr(element), sender_id)
        if sender_id == '_watcher':
            if self.preprocessor_q.empty():
                # We have an empty preprocessor queue
                self.tell_preprocessor(element)
            else:
                # Else, put in queue
                self.preprocessor_q.put(element)
        elif sender_id == self.preprocessor_id:
            # Send to the rest of processors
            for k in self.procs.keys():
                p = self.procs[k]
                q = self.qs[k]
                if q.empty():
                    self.tell_reload(p, element, k)
                else:
                    q.put(element)
            # Now we have to get the next item
            if not self.preprocessor_q.empty():
                next_e = self.preprocessor_q.get()
                self.tell_preprocessor(element)
        else:  # Any other processor
            # Now we have to get the next item
            p = self.procs[sender_id]
            q = self.qs[sender_id]
            if not q.empty():
                next_e = q.get()
                # p.tell({'element': next_e})
                self.tell_reload(p, next_e, sender_id)


class CentralQueueWrapper(ThreadingActor):
    def __init__(self, processor, central_queue):
        super(CentralQueueWrapper, self).__init__()
        self.processor = processor
        self.central_queue = central_queue
        self.id = processor.get_id()

    def on_receive(self, msg):
        element = msg['element']
        error = True
        try:
            self.processor.process(element)
            error = False
        except:
            log.exception(
                "Exception detected on processor %s processing message %s",
                self.id, element)
        finally:
            self.central_queue.tell({
                                    'id': self.id, 'element': element,
                                    'error': error})

    def on_failure(self, exception_type, exception_value, traceback):
        log.exception("Exception detected on actor %s, %s, %s",
                      exception_type, exception_value, traceback)
        # self.central_queue.tell({'id': self.id, 'element': None, 'error':
        # True})
