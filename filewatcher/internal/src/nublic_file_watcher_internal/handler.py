'''
Created on 06/09/2011

@author: Alejandro Serrano Mena
@author: David Navarro Estruch
@copyright: 2011 Nublic
'''

from signals import Signaler
import pyinotify
import socket
import threading
import re
import fnmatch

import logging
log = logging.getLogger(__name__)


def listen_socket(handler, port, address):
    serversocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    serversocket.bind((address, port))
    serversocket.listen(10)
    while True:
        # accept client
        log.info("Waiting socket")
        (c, _) = serversocket.accept()
        clientsocket = c.makefile()
        # receive its name
        log.info('Accepted socket')
        name = clientsocket.readline()[:-1]
        log.info('Added app %s', name)
        # add to corresponding signaler
        handler.signaler.add_socket(clientsocket)


class EventHandler(pyinotify.ProcessEvent):
    '''
    Listens the inotify events
    '''
    def my_init(self, manager, folder, port, address):
        self.manager = manager
        self.watched_folder = folder
        self.signaler = Signaler()
        self.thread = threading.Thread(target=listen_socket, args=(self, port, address))
        self.thread.start()
        # Excludes in "glob"-style
        # Idea taken from:
        # http://stackoverflow.com/questions/5141437/filtering-os-walk-dirs-and-files
        excludes = ['*/.*', '*~']  # for dirs and files
        exclude_regexp = r'|'.join([fnmatch.translate(x) for x in excludes]) or r'$.'
        self.exclude = re.compile(exclude_regexp)

    def mask(self):
        return (pyinotify.IN_CREATE |
                pyinotify.IN_DELETE |
                pyinotify.IN_CLOSE_WRITE |
                pyinotify.IN_MOVED_TO |
                pyinotify.IN_MOVED_FROM |
                pyinotify.IN_MOVE_SELF |
                pyinotify.IN_ISDIR |
                pyinotify.IN_ATTRIB |
                pyinotify.IN_DONT_FOLLOW)

    def is_banned(self, event):
        return self.exclude.match(event.pathname)

    def process_IN_ATTRIB(self, event):
        if not self.is_banned(event):
            self.raise_signal("attrib", event)

    def process_IN_CREATE(self, event):
        if not self.is_banned(event):
            self.raise_signal("modify", event)

    def process_IN_CLOSE_WRITE(self, event):
        if not self.is_banned(event):
            self.raise_signal("modify", event)

    def process_IN_DELETE(self, event):
        if not self.is_banned(event):
            self.raise_signal("delete", event)

    def process_IN_MOVED_TO(self, event):
        if not hasattr(event, 'src_pathname'):
            # We come from a directory outside
            # so this is equivalent to a creation
            self.process_IN_CREATE(event)
        elif not self.is_banned(event):
            self.raise_signal("move", event)

    def raise_signal(self, ty, event):
        try:
            if not hasattr(event, 'src_pathname'):
                self._raise_signal(ty, event.pathname, '', event.dir)
            else:
                self._raise_signal(
                    ty, event.pathname, event.src_pathname, event.dir)
        except:
            log.exception("An exception occurred raising a signal")

    def _raise_signal(self, ty, pathname, src_pathname, is_dir):
        self.signaler.raise_event(ty, pathname, src_pathname, is_dir)

    def send_repeated_creation(self, pathname, is_dir):
        " UNTESTED "
        if not self.exclude.match(pathname):
            self.signaler.raise_event("repeat", pathname, '', is_dir)
