#!/usr/bin/env python

'''Simple performance test'''

from rpcbd import Handler, ThreadedTCPJsonRpcPeer, JSONRPC_V2, hidden, exposed, willnotblock, mayblock

from time import sleep, time
import logging
import threading

# used for the test case
    
class Example(Handler):
    @willnotblock
    def count_cb(self, info):
        print info      


def sequential_calls(connection):
    start = time()
    for x in range(100):
        print connection.request.echo(x)
    finish = time()
    print 'Done in %s' % (finish - start)
    
def many_connections(peer):
    start = time()
    cns = []
    for x in range(100):
        c = peer.connect_tcp('127.0.0.1', 9999)
        cns.append(c)
        print 'Made %s connections in %s' % (x + 1, time() - start)
    print 'connections done in %s' % (time() - start)
    
    sleep(5)
    print 'Starting calls'
    start = time()
    for index, c in enumerate(cns):
        print c.request.echo(index)
        print 'Made %s calls in %s' % (index + 1, time() - start)        
    finish = time()
    print 'Done in %s' % (finish - start)
    
    print 'Closing connections'
    for c in cns:
        c.close()
    print 'All Closed'

if __name__=='__main__':
    #logging.basicConfig(level = logging.DEBUG)
    
    print 'Doing setup'

    # set up peer
    peer = ThreadedTCPJsonRpcPeer(JSONRPC_V2)
    
    d = peer.create_standard_dispatcher()
    d.add_handler_class(Example)
    grp = peer.create_connection_group('example')
    grp.add_dispatcher(d)
    peer.default_incoming_connection_group = grp
    c = peer.connect_tcp('127.0.0.1', 9999)
    
    sequential_calls(c)
    c.close()

    many_connections(peer)
    
    peer.shutdown()
 
    print 'Done!'
    
