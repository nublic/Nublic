'''
:author Alejandro Serrano <alex@nublic.com>
'''

#import logging
import atexit
import subprocess
from pykka.actor import ThreadingActor
import threading
from pickle import Unpickler
import simplejson as json
import socket

from change import parse_file_change

import logging
log = logging.getLogger(__name__)


def init_watcher(app_name, processors):
    """ DEPRECATED """
    # Init process
    p = subprocess.Popen(["/usr/bin/nublic-file-watcher-client", app_name],
                         stdout=subprocess.PIPE, universal_newlines=True)
    atexit.register(lambda: p.kill())
    # Start actor
    actor = FileWatcherActor.start(app_name, processors, log)
    # Now start listening
    WatcherThread(actor, p, log).start()


class WatcherThread(threading.Thread):
    """ DEPRECATED """
    def __init__(self, actor, p):
        self._actor = actor
        self._process = p
        self._unpickler = Unpickler(p.stdout)
        threading.Thread.__init__(self)

    def run(self):
        while True:
            e = json.loads(self._unpickler.load())
            change = parse_file_change(
                e['ty'], e['pathname'], e['src_pathname'], e['isdir'])
            #self._unpickler.memo = {}
            self._actor.tell({'command': 'forward', 'change': change})


def init_socket_watcher_for_everything(processors):
    actor = FileWatcherActor.start('Filewatcher', processors)
    SocketWatcherThread(actor).start()


def init_socket_watcher(app_name, processors):
    actor = FileWatcherActor.start(app_name, processors)
    SocketWatcherThread(actor).start()


class SocketWatcherThread(threading.Thread):
    """
    Wraps a Filewatcher actor to read from a Socket
    """
    def __init__(self, actor, address='localhost', port=5438):
        self._actor = actor
        self.app_name = actor.proxy().app_name.get()
        self._address = address
        self._port = port
        threading.Thread.__init__(self)

    def run(self):
        log.info("Running watcher %s on address %s and port %s",
                 self.app_name, self._address, self._port)
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        s.connect((self._address, self._port))
        fs = s.makefile()
        fs.write(self.app_name + '\n')
        log.info("Write app_name %s into the socket", self.app_name)
        fs.flush()
        while True:
            line = fs.readline()[:-1]
            e = json.loads(line)
            change = parse_file_change(e['ty'], e['pathname'],
                                       e['src_pathname'], e['isdir'])
            #self._unpickler.memo = {}
            log.info('Actor %s: Message received' % self.app_name)
            self._actor.tell({'command': 'forward', 'change': change})


class FileWatcherActor(ThreadingActor):
    '''
    Main actor that watches for incoming changes
    '''

    def __init__(self, app_name, processors):
        super(FileWatcherActor, self).__init__()
        self.app_name = app_name
        self._messageId = 0
        self._processors = []
        log.info('Processors: %s', unicode(processors))
        for processor in processors:
            log.info('Adding a processor')
            self._processors.append(processor)

    def on_receive(self, message):
        log.info('Message received in actor: %s', unicode(message))
        if message['command'] == 'forward':
            self._messageId += 1
            for processor in self._processors:
                log.info('Sending message')
                processor.tell(
                    {'change': message['change'], 'id': self._messageId})
        #elif message['command'] == 'back':
            # Don't do anything by now
            # pass
