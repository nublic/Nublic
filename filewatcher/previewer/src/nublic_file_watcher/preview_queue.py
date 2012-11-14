import datetime
from Queue import PriorityQueue
from pykka import ThreadingActor
from nublic.filewatcher import FileChange
from file_info import FileInfo
from solr_processor import SolrProcessor

class CentralQueue(ThreadingActor):
    def __init__(self, processors):
        super(CentralQueue, self).__init__()
        # Create the wrapped SolrProcessor
        self.solr = ProcessorWrapper.start(SolrProcessor(), self)
        self.solr_q = PriorityQueue()
        # Create the rest of wrappers
        self.procs = dict()
        self.qs = dict()
        for proc in processors:
            self.procs[proc.get_id()] = ProcessorWrapper.start(proc, self)
            self.qs[proc.get_id()] = PriorityQueue()

    def on_receive(self, msg):
        (sender_id, element) = msg

        if sender_id == '_watcher':
            if self.solr_q.empty():
                # We have an empry Solr queue
                self.solr.tell(element)
            else:
                # Else, put in queue
                self.solr_q.put(element)
        elif sender_id == 'solr':
            # Send to the rest of processors
            for k in self.procs.keys():
                p = self.procs[k]
                q = self.qs[k]
                if q.empty():
                    p.tell(element)
                else:
                    q.put(element)
            # Now we have to get the next item
            if not self.solr_q.empty():
                next_e = self.solr_q.get()
                self.solr.tell(next_e)
        else: # Any other processor
            # Now we have to get the next item
            p = self.procs[sender_id]
            q = self.qs[sender_id]
            if not q.empty():
                next_e = q.get()
                p.tell(next_e)

class ProcessorWrapper(ThreadingActor):
    def __init__(self, processor, central_queue):
        super(ProcessorWrapper, self).__init__()
        self.processor = processor
        self.central_queue = central_queue
        self.id = processor.get_id()

    def on_receive(self, element):
        self.processor.process(element)
        self.central_queue.tell((self.id, element))
