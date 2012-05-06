#!/usr/bin/python
'''
Created on 06/09/2011

@author: Alejandro Serrano Mena
@copyright: 2011 Nublic
'''

import gobject
import os
import pyinotify
import apps
# from dbus_signals import DbusSignaler
from handler import EventHandler
from manager import WatchManager2

def start_watching(folder):
    apps_info = apps.load_all_apps()
    config = apps.load_app_config()
    wm = WatchManager2()
    handler = EventHandler(wm, config, apps_info, folder)
    notifier = pyinotify.Notifier(wm, handler, timeout=10)
    gobject.timeout_add(100, quick_check, notifier)
    # Exclude files beginning with . or ending in ~
    e_filter = pyinotify.ExcludeFilter(['((/[^/]+)*/\\..*)|((/[^/]+)*/.+~)'])
    wm.add_watch(folder, handler.mask(), rec=True, auto_add=True, exclude_filter=e_filter)
    print "Starting to watch..."

def quick_check(notifier):
    notifier.process_events()
    while notifier.check_events():
        notifier.read_events()
        notifier.process_events()
    return True

def scan_folder(folder):
    apps_info = apps.load_all_apps()
    config = apps.load_app_config()
    wm = WatchManager2()
    handler = EventHandler(wm, apps_info, config)
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
    
