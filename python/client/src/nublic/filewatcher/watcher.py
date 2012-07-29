'''
:author Alejandro Serrano <alex@nublic.com>
'''

import dbus.mainloop.glib
import gobject
#import logging
from multiprocessing import Process, Queue
from pykka.actor import ThreadingActor
import threading

from change import parse_file_change

def init_watcher(app_name, processors, logger=None):
    # Init queue and process
    q = Queue()
    p = Process(target=_dbus_listener, args=(app_name,q,))
    # Start actor
    #if logger != None:
    #    logger.error('Starting actor')
    actor = FileWatcherActor.start(app_name, processors, logger)
    # Now start process and start listening
    #if logger != None:
    #    logger.error('Starting D-Bus process')
    p.start()
    #if logger != None:
    #    logger.error('Starting watcher thread')
    WatcherThread(actor, q, logger).start()

class WatcherThread(threading.Thread):
    def __init__(self, actor, queue, logger):
        self._actor = actor
        self._queue = queue
        self._logger = logger
        threading.Thread.__init__(self)
        
    def run(self):
        while True:
            #if self._logger != None:
            #    self._logger.error('Waiting message')
            e = self._queue.get()
            #if self._logger != None:
            #    self._logger.error('Message received')
            self._actor.tell({'command': 'forward', 'change': e})

def _dbus_listener(app_name, q):
    # Set up main loop for D-Bus
    dbus.mainloop.glib.DBusGMainLoop(set_as_default=True)
    # Run watcher
    FileWatcher(app_name, q)
    # Run main loop
    loop = gobject.MainLoop()
    gobject.threads_init()
    dbus.mainloop.glib.threads_init()
    loop.run()

class FileWatcher():
    '''
    Class that handles watching to actors
    '''
    def __init__(self, app_name, queue):
        self._app_name = app_name
        self._queue = queue
        # Connect to D-Bus
        bus = dbus.SystemBus()
        object_path = '/com/nublic/filewatcher/' + app_name
        o = bus.get_object('com.nublic.filewatcher', object_path)
        iface = dbus.Interface(o, dbus_interface='com.nublic.filewatcher')
        iface.connect_to_signal('file_changed', self.file_changed)
    
    def file_changed(self, ty, pathname, src_pathname, isdir, context):
        change = parse_file_change(ty, pathname, src_pathname, context, isdir)
        self._queue.put(change)

class FileWatcherActor(ThreadingActor):
    '''
    Main actor that watches for incoming changes
    '''
    
    def __init__(self, app_name, processors, logger):
        self._app_name = app_name
        self._logger = logger
        self._messageId = 0
        self._processors = []
        #self._logger.error('Processors: %s', str(processors))
        for processor in processors:
            #if self._logger != None:
            #    self._logger.error('Adding a processor')
            self._processors.append(processor(self))
    
    def on_receive(self, message):
        #self._logger.error('Message received: %s', str(message))
        if message['command'] == 'forward':
            self._messageId += 1
            for processor in self._processors:
                #self._logger.error('Sending message')
                processor.tell({'change': message.get('change'), 'id': self._messageId})
        elif message['command'] == 'back':
            # Don't do anything by now
            pass