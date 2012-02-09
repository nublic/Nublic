'''
Created on 06/09/2011

@author: Alejandro Serrano Mena
@copyright: 2011 Nublic
'''

import os
import os.path #IGNORE:W0404
import pyinotify
import solr
import re
import apps
import sys

class FakeCreationEvent:
    def __init__(self, pathname, isdir):
        self.pathname = pathname
        self.dir = isdir

class EventHandler(pyinotify.ProcessEvent):
    '''
    Listens the inotify events
    '''
    def __init__(self, manager, config, apps_info, folder):
        pyinotify.ProcessEvent.__init__(self)
        self.signalers = apps.create_initial_signalers(config, apps_info)
        self.manager = manager
        self.config = config
        self.apps_info = apps_info
        self.watched_folder = folder
    
    def mask(self):
        return pyinotify.IN_CREATE | pyinotify.IN_DELETE | pyinotify.IN_CLOSE_WRITE | pyinotify.IN_MOVED_TO | pyinotify.IN_MOVED_FROM | pyinotify.IN_ISDIR | pyinotify.IN_ATTRIB | pyinotify.IN_DONT_FOLLOW #IGNORE:E1101
    
    def process_IN_CREATE(self, event):
        # Create Solr element
        file_info = solr.new_doc(event.pathname, event.dir)
        file_info.save()
        # Notify via D-Bus
        self.handle_process("create", event)
        # If dir, we have to simulate file creation
        # because pyinotify only works well with inner dirs
        if event.dir:
            # Path to take account
            account_path = event.pathname.replace('/var/nublic/data/', '', 1)
            # Try to see if there is a signaler to add
            for app_id, app in self.apps_info.iteritems():
                if app.supports_filewatcher():
                    fw_folders = app.filewatcher.paths
                    for expr in fw_folders:
                        # sys.stderr.write("Trying to add " + event.pathname + " as " + expr + "\n")
                        regex = re.compile(expr, re.IGNORECASE)
                        if regex.match(account_path):
                            # We found a matching path
                            to_add = True
                            for already_path in self.config[u'apps'][app_id]:
                                if already_path.startswith(account_path):
                                    self.config[u'apps'][app_id].delete(already_path)
                                elif event.pathname.startswith(already_path):
                                    to_add = False
                            if to_add:
                                # sys.stderr.write("Added!\n")
                                self.config[u'apps'][app_id].append(account_path + '/')
                                apps.write_app_config(self.config)
                                
            # Touch all files
            for inner_file in os.listdir(event.pathname):
                file_name = os.path.join(event.pathname, inner_file)
                if os.path.isfile(file_name):
                    # Send fake creation event
                    fake_event = FakeCreationEvent(file_name, False)
                    self.process_IN_CREATE(fake_event)
    
    def process_IN_DELETE(self, event):
        # Delete in Solr
        file_info = solr.retrieve_doc(event.pathname)
        if file_info != None:
            file_info.delete()
        # Notify via D-Bus
        self.handle_process("delete", event)
    
    def process_IN_ATTRIB(self, event):
        # Check it's there
        if event.dir:
            file_info = solr.retrieve_doc(event.pathname)
            if file_info == None:
                file_info = solr.new_doc(event.pathname, event.dir)
                file_info.save()
                self.handle_process("modify", event)
        # Notify via D-Bus
        self.handle_process("attrib", event)
    
    def process_IN_CLOSE_WRITE(self, event):
        # Change in Solr
        file_info = solr.retrieve_doc(event.pathname)
        if file_info == None:
            file_info = solr.new_doc(event.pathname, event.dir)
        # This recomputes the MIME type
        file_info.save()
        # Notify via D-Bus
        self.handle_process("modify", event)

    def process_IN_MOVED_TO(self, event):
        if not hasattr(event, 'src_pathname'):
            # We come from a directory outside
            # so this is equivalent to a creation
            self.process_IN_CREATE(event)
        else:
            # The movement is in between watched folders
            
            # If is is a directory, change children
            if event.dir:
                # Change names in sudirectories
                dir_name = event.src_pathname
                new_dir_name = event.pathname
                for file_info in solr.retrieve_docs_in_dir(dir_name):
                    file_path = file_info.get_pathname()
                    new_file_path = file_path.replace(dir_name, new_dir_name, 1)
                    if file_info.is_directory():
                        self.change_watched_path(file_path, new_file_path)
                    file_info.set_new_pathname(new_file_path)
                    file_info.save()
                    print "%s -> %s" % (file_path, new_file_path)
                # Change the path for inotify events
                self.change_watched_path(event.src_pathname, event.pathname)
            # Change in Solr
            if solr.has_doc(event.src_pathname):
                file_info = solr.retrieve_doc(event.src_pathname)
                file_info.set_new_pathname(event.pathname)
                file_info.save()
            else:
                # Create a new file
                file_info = solr.new_doc(event.pathname, event.dir)
                file_info.save()
            # Notify via D-Bus
            # Special case, we have an extra parameter
            self.send_signal("move", event.pathname, event.src_pathname, event.dir)
    
    def change_watched_path(self, source, target):
        mgr = self.manager
        watch_ = mgr.get_watch(mgr.get_wd(source))
        watch_.path = target
    
    def handle_process(self, ty, event):
        self.send_signal(ty, event.pathname, '', event.dir)
    
    def send_signal(self, ty, pathname, src_pathname, is_dir):
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
