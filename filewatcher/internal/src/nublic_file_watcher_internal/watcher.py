#!/usr/bin/python
'''
Created on 06/09/2011

@author: Alejandro Serrano Mena
@copyright: 2011 Nublic
'''

import gobject
import os
import pyinotify
# from dbus_signals import DbusSignaler
from handler import EventHandler
from manager import WatchManager2
from threading import Timer

from time import sleep
import signal
import sys

class RepeatedTimer(object):
    def __init__(self, interval, function, *args, **kwargs):
        self._timer     = None
        self.function   = function
        self.interval   = interval
        self.args       = args
        self.kwargs     = kwargs
        self.is_running = False
        self.start()

    def _run(self):
        self.is_running = False
        self.start()
        self.function(*self.args, **self.kwargs)

    def start(self):
        if not self.is_running:
            self._timer = Timer(self.interval, self._run)
            self._timer.start()
            self.is_running = True

    def stop(self):
        self._timer.cancel()
        self.is_running = False

def start_watching(folder):
    wm = WatchManager2()
    handler = EventHandler(wm, folder)
    notifier = pyinotify.Notifier(wm, handler, timeout=10)
    rt = RepeatedTimer(1, quick_check, notifier)
    # gobject.timeout_add(100, quick_check, notifier)
    # Exclude files beginning with . or ending in ~
    e_filter = pyinotify.ExcludeFilter(['((/[^/]+)*/\\..*)|((/[^/]+)*/.+~)'])
    wm.add_watch(folder, handler.mask(), rec=True, auto_add=True, exclude_filter=e_filter)
                    
    sys.stderr.write("Starting to watch...\n")

    try:
        while True:
            sleep(1)
    except KeyboardInterrupt:
        rt.stop()
        peer.shutdown()
    sys.stderr.write("Filewatcher daemon shutdown")

def quick_check():
    notifier.process_events()
    while notifier.check_events():
        notifier.read_events()
        notifier.process_events()
    
    return True

def scan_folder(folder):
    wm = WatchManager2()
    handler = EventHandler(wm)
    for element, isdir in walk_folder(folder):
        handler.send_repeated_creation(element, isdir)
    
def walk_folder(top):
    for root, dirs, files in os.walk(top):
        # Do not walk through hidden folders
        index = 0
        while index < len(dirs):
            if dirs[index][0] == '.':
                del dirs[index]
                index = index - 1
            index = index + 1
        # Do not show hidden files
        index = 0
        while index < len(files):
            if files[index][0] == '.' or files[index][-1] == '~':
                del files[index]
                index = index - 1
            index = index + 1
        yield (root, True)
        for fl in files:
            yield (os.path.join(root, fl), False)
    
