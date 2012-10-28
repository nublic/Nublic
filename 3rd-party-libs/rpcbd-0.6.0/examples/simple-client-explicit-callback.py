from rpcbd import ThreadedTCPJsonRpcPeer, JSONRPC_V2, Handler
import threading

class ClientExample(Handler):
    assume_methods_block = True    
    def progress_callback(self, info):
        print 'Running callback in %s. Got: %s' % (threading.currentThread().getName(), info)

peer = ThreadedTCPJsonRpcPeer(JSONRPC_V2, default_handler = ClientExample)
connection = peer.connect_tcp('127.0.0.1', port = 9999)
result = connection.request.count(5, 'progress_callback')
print result
data = connection.request.echo('Hello World!')
print 'Got data: %s' % data
peer.shutdown()

