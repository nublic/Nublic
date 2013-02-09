from Queue import PriorityQueue
from pykka.actor import ThreadingActor
from pykka import ActorDeadError
from solr_processor import SolrProcessor

import logging
log = logging.getLogger(__name__)


class CentralQueue(ThreadingActor):
    def __init__(self, processors):
        super(CentralQueue, self).__init__()
        self.procs = dict()
        self.qs = dict()
        self.actors = dict()
        # Create the wrapped SolrProcessor
        log.info('CentralQueue starting')
        self.start_solr(start_queue=True)
        #log.info('SolrProcessor started')
        #self.solr_q = PriorityQueue()
        # Create the rest of wrappers
        for proc in processors:
            self.start_processor(proc, start_queue=True)
            #self.procs[proc.get_id(
            #)] = CentralQueueWrapper.start(proc, self.actor_ref)
            #self.qs[proc.get_id()] = PriorityQueue()
        log.info('All Processors started')

    def start_solr(self, start_queue=False):
        solr = SolrProcessor()
        log.info('SolrProcessor started')
        self.actors[solr.get_id()] = solr
        self.solr = CentralQueueWrapper.start(solr, self.actor_ref)
        if start_queue:
            self.solr_q = PriorityQueue()

    def start_processor(self, processor, start_queue=False):
        proc = processor()
        log.info('Processor %s starting', proc.get_id())
        self.actors[proc.get_id()] = processor
        self.procs[proc.get_id()] = CentralQueueWrapper.start(proc, self.actor_ref)
        if start_queue:
            self.qs[proc.get_id()] = PriorityQueue()

    def tell_reload(self, actor, element, key):
        try:
            actor.tell({'element': element})
        except ActorDeadError:
            log.exception("Processor %s was dead when trying to reach it", key)
            self.start_processor(self.actors[key], start_queue=False)
            self.tell_reload(actor, element, key)

    def tell_solr(self, element):
        try:
            self.solr.tell({'element': element})
        except ActorDeadError:
            log.exception("Solr was dead when trying to reach it")
            self.start_solr(start_queue=False)
            self.tell_solr(element)

    def on_receive(self, msg):
        sender_id = msg['id']
        element = msg['element']
        log.debug(
            "Received message %s from sender_id %s", repr(element), sender_id)
        if sender_id == '_watcher':
            if self.solr_q.empty():
                # We have an empry Solr queue
                self.solr.tell({'element': element})
            else:
                # Else, put in queue
                self.solr_q.put(element)
        elif sender_id == 'solr':
            # Send to the rest of processors
            for k in self.procs.keys():
                p = self.procs[k]
                q = self.qs[k]
                if q.empty():
                    p.tell({'element': element})
                else:
                    q.put(element)
            # Now we have to get the next item
            if not self.solr_q.empty():
                next_e = self.solr_q.get()
                self.solr.tell({'element': next_e})
        else:  # Any other processor
            # Now we have to get the next item
            p = self.procs[sender_id]
            q = self.qs[sender_id]
            if not q.empty():
                next_e = q.get()
                p.tell({'element': next_e})


class CentralQueueWrapper(ThreadingActor):
    def __init__(self, processor, central_queue):
        super(CentralQueueWrapper, self).__init__()
        self.processor = processor
        self.central_queue = central_queue
        self.id = processor.get_id()

    def on_receive(self, msg):
        element = msg['element']
        self.processor.process(element)
        self.central_queue.tell({'id': self.id, 'element': element})
