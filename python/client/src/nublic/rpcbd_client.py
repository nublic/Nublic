from rpcbd import ThreadedTCPJsonRpcPeer, JSONRPC_V2

def rpcbd_call_return(port, f):
    peer = ThreadedTCPJsonRpcPeer(JSONRPC_V2)
    connection = peer.connect_tcp('127.0.0.1', port)
    data = f(connection.request)
    peer.shutdown()
    return data

def rpcbd_call(port, f):
    peer = ThreadedTCPJsonRpcPeer(JSONRPC_V2)
    connection = peer.connect_tcp('127.0.0.1', port)
    f(connection.request)
    peer.shutdown()
