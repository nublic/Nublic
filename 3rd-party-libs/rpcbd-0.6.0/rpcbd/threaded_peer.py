# threaded_peer.py

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


import Queue
import threading
import socket
##import SocketServer
import logging
import sys

from base import Connection, ClientServerBase, Register, NullHandler

# logging setup
nullhandler = NullHandler()
log = logging.getLogger('rpcbd.threaded_peer')
log.addHandler(nullhandler)

# Threaded TCP/IP Client/Server implementation

class TCPConnection(Connection):
    def __init__(self, connection_group, description, default_timeout, tcp_socket):
        Connection.__init__(self, connection_group, description, default_timeout)
        self._tcp_socket = tcp_socket
        self._time_to_close = False
        self._processing_thread = None
    def _process_incoming(self):
        self._tcp_socket.settimeout(1)
        while not self._time_to_close:
            try:
                data = self._tcp_socket.recv(1024)
            except socket.timeout:
                if self.server.shutdown_on_parent_exit:
                    if not self.server._parent_thread.isAlive():
                        log.warning('Parent thread exited! Closing connection.')
                        sys.stderr.write('Parent thread exited! Closing connection.\n')
                        self.close()
                continue
            if data == '':
                break
            self._process_incoming_data(data)
        self.close()  ## Fixme: Check this!!!
    def _start_peer(self):
        self._processing_thread = threading.Thread(target = self._process_incoming)
        self._processing_thread.start()
    def _transport_level_send(self, raw_data):
        amount_to_send = len(raw_data)
        total_amount_sent = 0
        while total_amount_sent < amount_to_send:
            try:
                amount_sent = self._tcp_socket.send(raw_data)
            except socket.timeout:
                amount_sent = 0
            raw_data = raw_data[amount_sent:]
            total_amount_sent += amount_sent
    def _transport_level_close(self):
        self._time_to_close = True
        if threading.currentThread() != self._processing_thread:
            self._processing_thread.join()
    def close(self):
        Connection.close(self)
        #self._tcp_socket.shutdown()
        self._tcp_socket.close()


class TCP_Listener(object):
    def __init__(self, controlling_server, port, bind, connection_group, default_timeout):
        if connection_group is None:
            connection_group = controlling_server.default_incoming_connection_group
        self.port = port
        self.bind = bind
        self.connection_group = connection_group
        self.controlling_server = controlling_server
        self.controlling_server.listeners.append(self)
        self.connections = Register()
        self.listening_thread = None
        self.listening_socket = None
        self.default_timeout = default_timeout
        self._time_to_close = False
    def _run(self):
        while not self._time_to_close:
            try:
                tcp_socket, address = self.listening_socket.accept()
            except socket.timeout:
                if self.controlling_server.shutdown_on_parent_exit:
                    if not self.controlling_server._parent_thread.isAlive():
                        log.warning('Parent thread exited! Closing tcp listener.')
                        sys.stderr.write('Parent thread exited! Closing tcp listener.\n')
                        self.close()
                continue
            description = 'Connection from %s' % str(address)
            connection = TCPConnection(self.connection_group, description, self.default_timeout, tcp_socket)
            self.connections.append(connection)
            connection._start_peer()
    def close(self):
        self._time_to_close = True
        self.listening_thread.join()
        log.info('Peer no longer listening on %s:%s closed' % (self.bind or '0.0.0.0', self.port))
    def start_listening(self):
        self.listening_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.listening_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)  # make restarting the peer easier if it dies
        self.listening_socket.bind((self.bind, self.port))
        self.listening_socket.settimeout(1)
        self.listening_socket.listen(1)
        self.listening_thread = threading.Thread(target = self._run)
        ##self.listening_thread.setDaemon(True)
        self.listening_thread.start()
        log.info('Server loop started in thread: %s' % self.listening_thread.getName())
        log.info('Server listening on %s:%s' % (self.bind or '0.0.0.0', self.port))


class ThreadedTCPJsonRpcPeer(ClientServerBase):
    def __init__(self, default_rpc_processor, default_handler = None, default_timeout = None, shutdown_on_parent_exit = True):
        ClientServerBase.__init__(self, default_rpc_processor, default_handler = default_handler)
        self.listeners = Register()
        self.outgoing_connections = Register()
        self.default_timeout = default_timeout
        self.shutdown_on_parent_exit = shutdown_on_parent_exit
        self._parent_thread = threading.currentThread()
    def listen_tcp(self, port, bind = '', connection_group = None, default_timeout = -1):
        if default_timeout == -1:
            default_timeout = self.default_timeout
        listener = TCP_Listener(self, port, bind, connection_group, default_timeout)
        listener.start_listening()
    def connect_tcp(self, ip, port, connection_group = None, default_timeout = -1):
        if default_timeout == -1:
            default_timeout = self.default_timeout
        tcp_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        tcp_socket.connect((ip, port))
        description = 'Connection to %s:%s' % (ip, port)
        if connection_group is None:
            connection_group = self.default_outgoing_connection_group
        connection = TCPConnection(connection_group, description, default_timeout, tcp_socket)
        self.outgoing_connections.append(connection)
        connection._start_peer()
        return connection
    def shutdown(self, timeout = 60):
        for listener in self.listeners:
            listener.close()
            for connection in listener.connections:
                connection.close()
        for connection in self.outgoing_connections:
            connection.close()
        log.info('All connections closed.')
        for group in self.connection_groups:
            for dispatcher in group._dispatchers:
                for worker in dispatcher.workers:
                    worker.stop()
                for worker in dispatcher.workers:
                    worker.join(timeout)
        log.info('All workers stopped.')
        log.info('Peer shutdown complete')
