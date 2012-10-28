from rpcbd import ThreadedTCPJsonRpcPeer, JSONRPC_V2
import threading
import logging

def progress_callback(info):
    print 'Running callback in %s. Got: %s' % (threading.currentThread().getName(), info)

logging.basicConfig(level = logging.WARNING)
peer = ThreadedTCPJsonRpcPeer(JSONRPC_V2, default_timeout = 5)
connection = peer.connect_tcp('127.0.0.1', port = 9999)
result = connection.request.count(5, progress_callback)
print result
data = connection.request.echo('Hello World!')
print 'Got data: %s' % data
peer.shutdown()

