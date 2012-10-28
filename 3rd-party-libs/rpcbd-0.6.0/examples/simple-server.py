from rpcbd import Handler, ThreadedTCPJsonRpcPeer, JSONRPC_V2

from time import sleep
import logging # so we can see what is happening
import signal  # so keyboard interupt goes to main thread

class Example(Handler):
    assume_methods_block = False
    def echo(self, data):
        return data

if __name__=='__main__':
    logging.basicConfig(level = logging.INFO)
    peer = ThreadedTCPJsonRpcPeer(JSONRPC_V2, default_handler = Example)
    peer.listen_tcp(9999)
    try:
        while True:
            sleep(1)
    except KeyboardInterrupt:
        print '*** Got keyboard interrupt - shutting down ***'
        peer.shutdown()
    print 'Done!'

