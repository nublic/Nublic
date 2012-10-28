# functional_tests.py

# Copyright (c) 2010 Rasjid Wilcox
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in
# all copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
# THE SOFTWARE.

from rpcbd import Handler, ThreadedTCPJsonRpcPeer, JSONRPC_V2, hidden, exposed, willnotblock, mayblock
from time import sleep

PORT = 9999

# handlers for the test server

class TestServerHandler(Handler):
    assume_methods_block = False
    def echo(self, data):
        return data
    @mayblock
    def count(self, number, callback = None):
        num = int(number)
        if num <= 0:
            return 'Num is too low'
        for x in range(num):
            print 'Count to %s' % x
            if callback is None:
                # try the default callback
                self.connection.notification('count_cb')('Got to %s' % x)
            else:
                self.connection.notification(callback)('Counted to %x - sending via %s' % (x, callback))
            sleep(0.01)
        return 'Counted to %s' % num
    def divide(self, x, y):
        return x / y
    @hidden
    def you_cant_see_this(self):
        return "You can't see this method"
    @exposed
    def _you_can_see_this(self):
        return 'Hi there!'
    def _but_not_this(self):
        return 'I do not exist!'    

server_peer = None
    
def setup_module():
    global server_peer
    server_peer = ThreadedTCPJsonRpcPeer(JSONRPC_V2, default_handler = TestServerHandler, default_timeout = 5)
    server_peer.listen_tcp(port = PORT)
    
def teardown_module():
    server_peer.shutdown()
    
def test_echo_single_call():
    peer = ThreadedTCPJsonRpcPeer(JSONRPC_V2)
    connection = peer.connect_tcp('127.0.0.1', PORT)
    assert connection.request.echo('hello') == 'hello'
    connection.close()
    peer.shutdown()
    
def test_echo_multiple_calls():
    peer = ThreadedTCPJsonRpcPeer(JSONRPC_V2)
    connection = peer.connect_tcp('127.0.0.1', PORT)
    assert connection.request.echo('hello') == 'hello'
    assert connection.request.echo(1) == 1
    assert connection.request.echo(None) == None
    assert connection.request.echo(10.2) == 10.2
    connection.close()
    peer.shutdown()
    
class CallbackCollector(object):
    def __init__(self):
        self.results = []
    def __call__(self, result):
        self.results.append(result)
        
def test_inline_callback():
    n = 4
    peer = ThreadedTCPJsonRpcPeer(JSONRPC_V2)
    connection = peer.connect_tcp('127.0.0.1', PORT)
    inline_callback = CallbackCollector()
    assert connection.request.count(n, inline_callback) == 'Counted to %s' % n
    for index, value in enumerate(inline_callback.results):
        assert value.startswith('Counted to %s - sending via rpc.callback.' % index)
    print inline_callback.results
    
def teardown_module():
    server_peer.shutdown()
    
