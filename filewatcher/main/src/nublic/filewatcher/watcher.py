#!/usr/bin/python
'''
Created on 06/09/2011

@author: Alejandro Serrano Mena
@copyright: 2011 Nublic
'''

import pyinotify
from handler import *
import time

if __name__ == '__main__':
    wm = pyinotify.WatchManager()
    handler = EventHandler()
    notifier = pyinotify.Notifier(wm, handler)
    now = time.time()
    wdd = wm.add_watch('/home/serras', handler.mask(), rec=True, auto_add=True)
    print("finished")
    print(time.time() - now)
    notifier.loop()
