from rpcbd import Handler, ThreadedTCPJsonRpcPeer, JSONRPC_V2, willnotblock, mayblock
from time import sleep
import logging
import signal

class Example(Handler):
    @willnotblock
    def echo(self, data):
        return data
    @mayblock
    def count(self, number, callback_name = None):
        num = int(number)
        if num <= 0:
            return 'Num is too low'
        for x in range(num):
            if callback_name:
                self.connection.notification(callback_name)('Counted to %s' % str(x + 1))
            sleep(1)
        return 'Counted to %s' % num
    @willnotblock
    def divide(self, x, y):
        return x / y

if __name__=='__main__':
    logging.basicConfig(level = logging.INFO)
    peer = ThreadedTCPJsonRpcPeer(JSONRPC_V2, default_handler = Example)
    peer.listen_tcp(port = 9999)
    try:
        while True:
            sleep(1)
    except KeyboardInterrupt:
        print '*** Got keyboard interrupt - shutting down ***'
        peer.shutdown()
    print 'Done!'

