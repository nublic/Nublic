from rpcbd import ThreadedTCPJsonRpcPeer, JSONRPC_V2

peer = None
connection = None


def rpcbd_call_return(port, f, timeout=10):
    global peer
    global connection
    if not peer or not connection:
        peer = ThreadedTCPJsonRpcPeer(JSONRPC_V2, default_timeout=timeout)
        connection = peer.connect_tcp('127.0.0.1', port)
    data = f(connection.request)
    #peer.shutdown()
    return data


def rpcbd_call(port, f, timeout=10):
    global peer
    global connection
    if not peer or not connection:
        peer = ThreadedTCPJsonRpcPeer(JSONRPC_V2, default_timeout=timeout)
        connection = peer.connect_tcp('127.0.0.1', port)
    f(connection.request)
    #peer.shutdown()
