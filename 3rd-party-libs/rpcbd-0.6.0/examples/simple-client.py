from rpcbd import ThreadedTCPJsonRpcPeer, JSONRPC_V2

peer = ThreadedTCPJsonRpcPeer(JSONRPC_V2)
connection = peer.connect_tcp('127.0.0.1', 9999)
data = connection.request.echo('Hello World!')
print 'Got data: %s' % data
peer.shutdown()



