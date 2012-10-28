#!/usr/bin/env python

'''Simple test peer'''

from rpcbd import Handler, ThreadedTCPJsonRpcPeer, JSONRPC_V2, hidden, exposed, willnotblock, mayblock
from time import sleep
import logging
import threading
import signal

# used for the test case
    
class Example(Handler):
    @mayblock
    def echo(self, data):
        ##print 'Running echo in thread %s' % threading.currentThread().getName()
        return data
    @willnotblock
    def count_cb(self, info):
        print info
    @mayblock
    def count(self, number, callback = None):
        num = int(number)
        if num <= 0:
            return 'Num is too low'
        for x in range(num):
            #print 'Count to %s' % x
            if callback is None:
                # try the default callback
                self.connection.notification('count_cb')('Got to %s of %s' % (str(x + 1), num))
            else:
                self.connection.notification(callback)('Counted to %s - sending via %s' % (str(x + 1), callback))
            sleep(1)
        return 'Counted to %s' % num
    @willnotblock
    def divide(self, x, y):
        return x / y
    @hidden
    def you_cant_see_this(self):
        return "You can't see this method"
    @exposed
    @willnotblock
    def _you_can_see_this(self):
        return 'Hi there!'
    def _but_not_this(self):
        return 'I do not exist!'    

class Foo(Handler):
    assume_methods_block = False
    def echo(self, data):
        return 'Foo said: %s' % data
        
def translatesignal(signalnum, stackframe):
    print '*** Got signal: %s' % signalnum
    raise KeyboardInterrupt()
                

if __name__=='__main__':
    logging.basicConfig(level = logging.INFO)    
    signal.signal(signal.SIGTERM, translatesignal)    
    
    print '*** Doing setup'
    # set up peer
    peer = ThreadedTCPJsonRpcPeer(JSONRPC_V2)
    d = peer.create_standard_dispatcher()
    d.add_handler_class(Example)
    d.add_handler_class(Foo, prefix='foo')
    grp = peer.create_connection_group('example')
    grp.add_dispatcher(d)
    peer.default_incoming_connection_group = grp
    peer.listen_tcp(9999)
    
    thread_count = 0

    try:
        while 1:
            tc = threading.activeCount()
            if thread_count != tc:
                thread_count = tc
                print '*** Active thread count now: %s' % thread_count
            sleep(1)
    except KeyboardInterrupt:
        print '*** Got keyboard interrupt - shutting down ***'
        peer.shutdown()
 
    print '*** Done!'
    
