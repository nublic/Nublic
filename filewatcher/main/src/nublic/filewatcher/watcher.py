#!/usr/bin/python
'''
Created on 06/09/2011

@author: Alejandro Serrano Mena
@copyright: 2011 Nublic
'''

import gobject
import pyinotify
from dbus_signals import *
from handler import *
from manager import *

def start_watching(folder):
    wm = WatchManager2()
    signaler = DbusSignaler('Browser')
    handler = EventHandler(signaler)
    notifier = pyinotify.Notifier(wm, handler, timeout=10)
    gobject.timeout_add(500, quick_check, notifier)
    e_filter = pyinotify.ExcludeFilter(['(/[^/]+)*/\\..*'])
    wdd = wm.add_watch(folder, handler.mask(), rec=True, auto_add=True, exclude_filter=e_filter)
    print "Starting to watch..."

def quick_check(notifier):
    notifier.process_events()
    while notifier.check_events():
        notifier.read_events()
        notifier.process_events()
    return True
