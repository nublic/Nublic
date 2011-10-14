'''
Created on 07/09/2011

@author: Alejandro Serrano Mena
@copyright: 2011 Nublic
'''

import pyinotify
import os


class WatchManager2(pyinotify.WatchManager):
    '''
    Reimplementation of the original WatchManager.
    When adding a watch recursively, it doesn't follow
    hidden folders (those starting with '.').
    '''

    def _WatchManager__walk_rec(self, top, rec):
        '''
        Yields each subdirectories of top, doesn't follow symlinks.
        If rec is false, only yield top.

        @param top: root directory.
        @type top: string
        @param rec: recursive flag.
        @type rec: bool
        @return: path of one subdirectory.
        @rtype: string
        '''
        if not rec or os.path.islink(top) or not os.path.isdir(top):
            yield top
        else:
            for root, dirs, _ in os.walk(top):
                index = 0
                while index < len(dirs):
                    if dirs[index][0] == '.':
                        del dirs[index]
                        index = index - 1
                    index = index + 1
                yield root
