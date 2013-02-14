#!/usr/bin/python
'''
Created on 06/09/2011

@author: Alejandro Serrano Mena
@author: David Navarro Estruch
@copyright: 2011 Nublic
'''

import os
import pyinotify
from handler import EventHandler

import logging
log = logging.getLogger(__name__)


def start_watching(folder, port, address):
    "Watch folder and all subfolders not hidden"
    log.info("Filewatcher daemon start_watching")
    wm = pyinotify.WatchManager()
    handler = EventHandler(manager=wm, folder=folder, port=port, address=address)
    notifier = pyinotify.Notifier(wm, handler, timeout=10)
    # Exclude files beginning with . or ending in ~
    e_filter = pyinotify.ExcludeFilter([r'.*\/\..*\Z(?ms)'])  # via fnmatch.translate('*/.*')
#                                        r'.*\~\Z(?ms)'])  # via fnmatch.translate('*~')
    wm.add_watch(folder, handler.mask(), rec=True,
                 auto_add=True, quiet=False, exclude_filter=e_filter)
    log.info('Starting to watch...')
    notifier.loop()
    log.info("Filewatcher daemon shutdown")


def scan_folder(folder):
    " UNTESTED "
    wm = pyinotify.WatchManager()
    handler = EventHandler(wm)
    for element, isdir in walk_folder(folder):
        handler.send_repeated_creation(element, isdir)


def walk_folder(top):
    " UNTESTED "
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
