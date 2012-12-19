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


def init_watcher(app_name, processors, logger=None):
    """ DEPRECATED """
    # Init process
    p = subprocess.Popen(["/usr/bin/nublic-file-watcher-client", app_name],
        stdout=subprocess.PIPE, universal_newlines=True)
    atexit.register(lambda: p.kill())
    # Start actor
    #if logger != None:
    #    logger.error('Starting actor')
    actor = FileWatcherActor.start(app_name, processors, logger)
    # Now start listening
    #if logger != None:
    #    logger.error('Starting watcher thread')
    WatcherThread(actor, p, logger).start()


class WatcherThread(threading.Thread):
    """ DEPRECATED """
    def __init__(self, actor, p, logger):
        self._actor = actor
        self._process = p
        self._logger = logger
        self._unpickler = Unpickler(p.stdout)
        threading.Thread.__init__(self)

    def run(self):
        while True:
            #if self._logger != None:
            #    self._logger.error('Waiting message')
            e = json.loads(self._unpickler.load())
            change = parse_file_change(e['ty'], e['pathname'], e['src_pathname'], e['isdir'])
            #self._unpickler.memo = {}
            #if self._logger != None:
            #    self._logger.error('Message received')
            self._actor.tell({'command': 'forward', 'change': change})


def init_socket_watcher_for_everything(processors, logger=None):
    actor = FileWatcherActor.start('Filewatcher', processors, logger)
    SocketWatcherThread(actor).start()


def init_socket_watcher(app_name, processors, logger=None):
    actor = FileWatcherActor.start(app_name, processors, logger)
    SocketWatcherThread(actor).start()


class SocketWatcherThread(threading.Thread):
    """
    Wraps a Filewatcher actor to read from a Socket
    """
    def __init__(self, actor, address='localhost', port=5438):
        self._actor = actor
        self._logger = actor.get_logger()
        self.app_name = actor.app_name
        self._address = address
        self._port = port
        threading.Thread.__init__(self)

    def run(self):
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        s.connect((self._address, self._port))
        fs = s.makefile()
        fs.write(self.app_name + '\n')
        fs.flush()
        while True:
            line = fs.readline()[:-1]
            e = json.loads(line)
            change = parse_file_change(e['ty'], e['pathname'], e['src_pathname'], e['context'], e['isdir'])
            #self._unpickler.memo = {}
            if self._logger != None:
                self._logger.error('Message received')
            self._actor.tell({'command': 'forward', 'change': change})


class FileWatcherActor(ThreadingActor):
    '''
    Main actor that watches for incoming changes
    '''

    def __init__(self, app_name, processors, logger):
        super(FileWatcherActor, self).__init__()
        self.app_name = app_name
        self.logger = logger
        self._messageId = 0
        self._processors = []
        #self.logger.error('Processors: %s', str(processors))
        for processor in processors:
            #if self.logger != None:
            #    self.logger.error('Adding a processor')
            self._processors.append(processor(self))

    def get_logger(self):
        return self.logger

    def on_receive(self, message):
        #self.logger.error('Message received in actor: %s', str(message))
        if message['command'] == 'forward':
            self._messageId += 1
            for processor in self._processors:
                #self.logger.error('Sending message')
                processor.tell({'change': message.get('change'), 'id': self._messageId})
        #elif message['command'] == 'back':
            # Don't do anything by now
            #pass
