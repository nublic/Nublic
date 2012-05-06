'''
Created on 06/09/2011

@author: Alejandro Serrano Mena
@copyright: 2011 Nublic
'''

import os
import os.path
import pyinotify
import solr
from dbus_signals import (DbusSignaler)
#import sys

class FakeCreationEvent:
    def __init__(self, pathname, isdir):
        self.pathname = pathname
        self.dir = isdir

class FakeMoveEvent:
    def __init__(self, pathname, src_pathname, isdir):
        self.pathname = pathname
        self.src_pathname = src_pathname
        self.dir = isdir

class EventHandler(pyinotify.ProcessEvent):
    '''
    Listens the inotify events
    '''
    def __init__(self, manager, config, apps_info, folder):
        pyinotify.ProcessEvent.__init__(self)
        self.manager = manager
        self.watched_folder = folder
        self.config = config
        self.apps_info = apps_info
        self.signalers = []
        self.create_initial_signalers()

    def create_initial_signalers(self):
        for app_id in self.apps_info:
            app = self.apps_info[app_id]
            if app.supports_filewatcher():
                self.signalers.append(DbusSignaler(self.config, app))
    
    def mask(self):
        return pyinotify.IN_CREATE | pyinotify.IN_DELETE | pyinotify.IN_CLOSE_WRITE | pyinotify.IN_MOVED_TO | pyinotify.IN_MOVED_FROM | pyinotify.IN_ISDIR | pyinotify.IN_ATTRIB | pyinotify.IN_DONT_FOLLOW #IGNORE:E1101

    def create_or_send_modifications(self, event_name, event):
        # If not file, check parent folder is in database
        if not event.dir:
            parent = os.path.dirname(event.pathname)
            if parent != '/var/nublic/data':  # Not in top path
                if not solr.has_doc(parent):
                    fake_event = FakeCreationEvent(parent, True)
                    self.process_IN_MODIFY(fake_event)
        # Check if it is itself in Solr
        file_info = solr.retrieve_doc(event.pathname)
        if file_info == None:
            # Create new Solr document
            file_info = solr.new_doc(event.pathname, event.dir)
            file_info.save()
            self.raise_signal("create", event)
            # Possibly at to watched list
            if event.dir:
                for signaler in self.signalers:
                    signaler.add_context(event.pathname)
        else:
            # Recreate Solr info
            file_info.save()
            self.raise_signal(event_name, event)

    def process_IN_ATTRIB(self, event):
        self.create_or_send_modifications("attrib", event)

    def process_IN_CREATE(self, event):
        self.create_or_send_modifications("modify", event)
    
    def process_IN_CLOSE_WRITE(self, event):
        self.create_or_send_modifications("modify", event)
    
    def process_IN_DELETE(self, event):
        # Delete in Solr
        file_info = solr.retrieve_doc(event.pathname)
        if file_info != None:
            file_info.delete()
        # Delete from watched folders if there
        if event.dir:
            for signaler in self.signalers:
                signaler.remove_context(event.pathname)
        # Notify via D-Bus
        self.handle_process("delete", event)

    def process_IN_MOVED_TO(self, event):
        if not hasattr(event, 'src_pathname'):
            # We come from a directory outside
            # so this is equivalent to a creation
            self.process_IN_CREATE(event)
        else:
            # The movement is in between watched folders
            # Change pathname in Solr
            if solr.has_doc(event.src_pathname):
                file_info = solr.retrieve_doc(event.src_pathname)
                file_info.set_new_pathname(event.pathname)
                file_info.save()
                self.raise_signal("move", event)
            else:
                # This is equivalent to creation
                self.create_or_send_modifications("move", event)
            # We are in a dir, change inner elements
            if event.dir:
                # We are in a folder
                dir_name = event.src_pathname
                new_dir_name = event.pathname
                # Change the path for inotify events
                self.change_watched_path(event.src_pathname, event.pathname)
                # Change in saved watched folders
                for signaler in self.signalers:
                    signaler.replace_context(dir_name, new_dir_name, True)
                # Update files in Solr and send events for them
                for file_info in solr.retrieve_docs_in_dir(dir_name):
                    file_path = file_info.get_pathname()
                    new_file_path = file_path.replace(dir_name, new_dir_name, 1)
                    file_info.set_new_pathname(new_file_path)
                    file_info.save()
                    if file_info.is_directory():
                        # It may be watched, so try to replace it
                        for signaler in self.signalers:
                            signaler.replace_context(file_path, new_file_path, True)
                            
    def change_watched_path(self, source, target):
        mgr = self.manager
        watch_ = mgr.get_watch(mgr.get_wd(source))
        watch_.path = target
        # Recursively change path
        for inner_file in os.listdir(target):
            file_name = os.path.join(target, inner_file)
            if os.path.isdir(file_name):
                prev_file_name = os.path.join(source, inner_file)
                self.change_watched_path(prev_file_name, file_name)
    
    def raise_signal(self, ty, event):
        if not hasattr(event, 'src_pathname'):
            self._raise_signal(ty, event.pathname, '', event.dir)
        else:
            self._raise_signal(ty, event.pathname, event.src_pathname, event.dir)
    
    def _raise_signal(self, ty, pathname, src_pathname, is_dir):
        for signaler in self.signalers:
            signaler.raise_event(ty, pathname, src_pathname, is_dir)
    
    def send_repeated_creation(self, pathname, is_dir):
        # Check if it is in Solr
        if not solr.has_doc(pathname):
            file_info = solr.new_doc(pathname, is_dir)
            file_info.save()
            for signaler in self.signalers:
                signaler.raise_event("create", pathname, '', is_dir)
        else:
            file_info = solr.retrieve_doc(pathname)
            file_info.save()
            # Send repeat message via D-Bus
            for signaler in self.signalers:
                signaler.raise_event("repeat", pathname, '', is_dir)
