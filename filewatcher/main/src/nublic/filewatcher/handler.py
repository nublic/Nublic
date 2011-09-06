'''
Created on 06/09/2011

@author: Alejandro Serrano Mena
@copyright: 2011 Nublic
'''
import pyinotify

class EventHandler(pyinotify.ProcessEvent):
    '''
    Listens the inotify events
    '''
    
    def mask(self):
        return pyinotify.IN_CREATE | pyinotify.IN_DELETE | pyinotify.IN_MODIFY \
             | pyinotify.IN_MOVED_TO | pyinotify.IN_MOVED_FROM  \
             | pyinotify.IN_ISDIR | pyinotify.IN_ATTRIB | pyinotify.IN_DONT_FOLLOW
    
    def process_IN_CREATE(self, event):
        self.handle_process(event, "create")
    
    def process_IN_DELETE(self, event):
        self.handle_process(event, "delete")
    
    def process_IN_ATTRIB(self, event):
        self.handle_process(event, "attrib")
    
    def process_IN_MOVED_TO(self, event):
        print(event.pathname)
        print(event.src_pathname)
        print(str(event.dir))
        print("move")
    
    def process_IN_MODIFY(self, event):
        self.handle_process(event, "modify")
    
    '''
    @param event pyinotify.Event
    '''
    def handle_process(self, event, ty):
        print(event.pathname)
        print(str(event.dir))
        print(ty)
