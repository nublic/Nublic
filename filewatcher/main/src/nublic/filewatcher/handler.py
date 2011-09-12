'''
Created on 06/09/2011

@author: Alejandro Serrano Mena
@copyright: 2011 Nublic
'''

import pyinotify
import solr

"""
Listens the inotify events
"""
class EventHandler(pyinotify.ProcessEvent):
    
    def __init__(self, signaler):
        pyinotify.ProcessEvent.__init__(self)
        self.signaler = signaler
    
    def mask(self):
        return pyinotify.IN_CREATE | pyinotify.IN_DELETE | pyinotify.IN_MODIFY \
             | pyinotify.IN_MOVED_TO | pyinotify.IN_MOVED_FROM  \
             | pyinotify.IN_ISDIR | pyinotify.IN_ATTRIB | pyinotify.IN_DONT_FOLLOW
    
    def process_IN_CREATE(self, event):
        self.handle_process("create", event)
        # Create Solr element
        file_info = solr.FileInfo(event.pathname, event.dir)
        file_info.save()
    
    def process_IN_DELETE(self, event):
        self.handle_process("delete", event)
    
    def process_IN_ATTRIB(self, event):
        self.handle_process("attrib", event)
    
    def process_IN_MODIFY(self, event):
        self.handle_process("modify", event)

    def process_IN_MOVED_TO(self, event):
        # Special case, we have an extra parameter
        self.send_signal("move", event.pathname, event.src_pathname, event.dir)
    
    def handle_process(self, ty, event):
        self.send_signal(ty, event.pathname, '', event.dir)
    
    def send_signal(self, ty, pathname, src_pathname, is_dir):
        self.signaler.file_changed(ty, pathname, src_pathname, is_dir)
