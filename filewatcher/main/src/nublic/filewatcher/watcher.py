#!/usr/bin/python
'''
Created on 06/09/2011

@author: Alejandro Serrano Mena
@copyright: 2011 Nublic
'''

import pyinotify
from handler import *
import time
import re
import os

'''
Reimplementation of the original WatchManager.
When adding a watch recursively, it doesn't follow
hidden folders (those starting with '.').
'''
class WatchManager2(pyinotify.WatchManager):
    def _WatchManager__walk_rec(self, top, rec):
        """
        Yields each subdirectories of top, doesn't follow symlinks.
        If rec is false, only yield top.

        @param top: root directory.
        @type top: string
        @param rec: recursive flag.
        @type rec: bool
        @return: path of one subdirectory.
        @rtype: string
        """
        if not rec or os.path.islink(top) or not os.path.isdir(top):
            yield top
        else:
            for root, dirs, files in os.walk(top):
                index = 0
                while index < len(dirs):
                    if dirs[index][0] == '.':
                        del dirs[index]
                        index = index - 1
                    index = index + 1
                yield root

if __name__ == '__main__':
    pyinotify.log.setLevel(10)
    wm = WatchManager2()
    handler = EventHandler()
    notifier = pyinotify.Notifier(wm, handler)
    filter = pyinotify.ExcludeFilter(['(/[^/]+)*/\\..*'])
    wdd = wm.add_watch('/home/serras', handler.mask(), rec=True, auto_add=True, exclude_filter=filter)
    notifier.loop()
