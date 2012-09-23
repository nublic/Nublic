'''
Created on 06/09/2011

@author: Alejandro Serrano Mena
@copyright: 2011 Nublic
'''

from datetime import datetime
from dbus_signals import (DbusSignaler)
import os
import os.path
import pyinotify
import socket
import sys
import threading
import traceback

class FakeCreationEvent:
    def __init__(self, pathname, isdir):
        self.pathname = pathname
        self.dir = isdir

class FakeMoveEvent:
    def __init__(self, pathname, src_pathname, isdir):
        self.pathname = pathname
        self.src_pathname = src_pathname
        self.dir = isdir

class SocketListener(threading.Thread):
    MAX_CONN = 100
    
    def __init__(self, handler):
        self.handler = handler
        threading.Thread.__init__(self)
    
    def run(self):
        serversocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        serversocket.bind(('localhost', 5438))
        serversocket.listen(10)
        while True:
            # accept client
            sys.stderr.write('Waiting socket\n')
            (c, _) = serversocket.accept()
            clientsocket = c.makefile()
            # receive its name
            sys.stderr.write('Accepted socket\n')
            name = clientsocket.readline()[:-1]
            sys.stderr.write('Added app ' + name + '\n')
            # add to corresponding signaler
            self.handler.signaler.add_socket(clientsocket)

class EventHandler(pyinotify.ProcessEvent):
    '''
    Listens the inotify events
    '''
    def __init__(self, manager, folder):
        pyinotify.ProcessEvent.__init__(self)
        self.manager = manager
        self.watched_folder = folder
        self.signaler = DbusSignaler()
        SocketListener(self).start()

    def mask(self):
        return pyinotify.IN_CREATE | pyinotify.IN_DELETE | pyinotify.IN_CLOSE_WRITE | pyinotify.IN_MOVED_TO | pyinotify.IN_MOVED_FROM | pyinotify.IN_ISDIR | pyinotify.IN_ATTRIB | pyinotify.IN_DONT_FOLLOW #IGNORE:E1101

    def process_IN_ATTRIB(self, event):
        self.raise_signal("attrib", event)

    def process_IN_CREATE(self, event):
        self.raise_signal("modify", event)
    
    def process_IN_CLOSE_WRITE(self, event):
        self.raise_signal("modify", event)
    
    def process_IN_DELETE(self, event):
        self.raise_signal("delete", event)

    def process_IN_MOVED_TO(self, event):
        if not hasattr(event, 'src_pathname'):
            # We come from a directory outside
            # so this is equivalent to a creation
            self.process_IN_CREATE(event)
        else:
            self.raise_signal("move", event)
    
    def raise_signal(self, ty, event):
        try:
            if not hasattr(event, 'src_pathname'):
                self._raise_signal(ty, event.pathname, '', event.dir)
            else:
                self._raise_signal(ty, event.pathname, event.src_pathname, event.dir)
        except:
            sys.stderr.write('An exception ocurred at ' + str(datetime.now()) + ':\n')
            traceback.print_exc(file=sys.stderr)
            sys.stderr.write('\n\n')
    
    def _raise_signal(self, ty, pathname, src_pathname, is_dir):
        signaler.raise_event(ty, pathname, src_pathname, is_dir)
    
    def send_repeated_creation(self, pathname, is_dir):
        signaler.raise_event("repeat", pathname, '', is_dir)
