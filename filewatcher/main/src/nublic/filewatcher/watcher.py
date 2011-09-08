#!/usr/bin/python
'''
Created on 06/09/2011

@author: Alejandro Serrano Mena
@copyright: 2011 Nublic
'''

import pyinotify
from dbus_signals import *
from handler import *
from manager import *

def start_watching(folder, loop):
    wm = WatchManager2()
    signaler = DbusSignaler('Browser', loop)
    handler = EventHandler(signaler)
    notifier = pyinotify.Notifier(wm, handler)
    e_filter = pyinotify.ExcludeFilter(['(/[^/]+)*/\\..*'])
    wdd = wm.add_watch(folder, handler.mask(), rec=True, auto_add=True, exclude_filter=e_filter)
    print "Starting to watch..."
    notifier.loop()
