'''
Created on 06/09/2011

@author: Alejandro Serrano Mena
@copyright: 2011 Nublic
'''

import pyinotify
import solr

class EventHandler(pyinotify.ProcessEvent):
    '''
    Listens the inotify events
    '''
    def __init__(self, signaler):
        pyinotify.ProcessEvent.__init__(self)
        self.signaler = signaler
    
    def mask(self):
        return pyinotify.IN_CREATE | pyinotify.IN_DELETE | pyinotify.IN_CLOSE_WRITE | pyinotify.IN_MOVED_TO | pyinotify.IN_MOVED_FROM | pyinotify.IN_ISDIR | pyinotify.IN_ATTRIB | pyinotify.IN_DONT_FOLLOW #IGNORE:E1101
    
    def process_IN_CREATE(self, event):
        # Create Solr element
        file_info = solr.new_doc(event.pathname, event.dir)
        file_info.save()
        # Notify via D-Bus
        self.handle_process("create", event)
    
    def process_IN_DELETE(self, event):
        # Delete in Solr
        file_info = solr.retrieve_doc(event.pathname)
        file_info.delete()
        # Notify via D-Bus
        self.handle_process("delete", event)
    
    def process_IN_ATTRIB(self, event):
        # No changes in Solr
        # Notify via D-Bus
        self.handle_process("attrib", event)
    
    def process_IN_CLOSE_WRITE(self, event):
        # Change in Solr
        file_info = solr.retrieve_doc(event.pathname)
        # This recomputes the MIME type
        file_info.save()
        # Notify via D-Bus
        self.handle_process("modify", event)

    def process_IN_MOVED_TO(self, event):
        # If is is a directory, change children
        if event.dir:
            dir_name = event.src_pathname
            new_dir_name = event.pathname
            for file_info in solr.retrieve_docs_in_dir(dir_name):
                file_path = file_info.get_pathname()
                new_file_path = file_path.replace(dir_name, new_dir_name, 1)
                file_info.set_new_pathname(new_file_path)
                file_info.save()
        # Change in Solr
        file_info = solr.retrieve_doc(event.src_pathname)
        file_info.set_new_pathname(event.pathname)
        file_info.save()
        # Notify via D-Bus
        # Special case, we have an extra parameter
        self.send_signal("move", event.pathname, event.src_pathname, event.dir)
    
    def handle_process(self, ty, event):
        self.send_signal(ty, event.pathname, '', event.dir)
    
    def send_signal(self, ty, pathname, src_pathname, is_dir):
        self.signaler.file_changed(ty, pathname, src_pathname, is_dir)
    
    def send_repeated_creation(self, pathname, is_dir):
        # Check if it is in Solr
        if not solr.has_doc(pathname):
            file_info = solr.new_doc(pathname, is_dir)
            file_info.save()
            self.signaler.file_changed("create", pathname, '', is_dir)
        else:
            file_info = solr.retrieve_doc(pathname)
            file_info.save()
            # Send repeat message via D-Bus
            self.signaler.file_changed("repeat", pathname, '', is_dir)
