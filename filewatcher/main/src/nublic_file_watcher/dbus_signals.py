'''
Created on 08/09/2011

@author: Alejandro Serrano Mena
@copyright: 2011 Nublic
'''

# import dbus
import apps
import dbus.service
import os
import os.path
import re
import sys

def to_utf8(string):
    return unicode(string, 'utf-8')

def from_utf8(string):
    return string.encode('utf-8')

class DbusSignaler(dbus.service.Object):
    '''
    Sends signals via D-Bus
    '''
    def __init__(self, config, app):
        # Initialize D-Bus
        bus_name = dbus.service.BusName('com.nublic.filewatcher', bus=dbus.SystemBus())
        dbus.service.Object.__init__(self, bus_name, '/com/nublic/filewatcher/' + app.app_id.title())
        # Try to find configuration
        self.app_info = app
        self.config = config
        # If new app, try to add a watch
        if app.app_id in config[u'apps']:
            self.supports_watching = True
        else:
            if app.supports_filewatcher():
                self.supports_watching = True
                fw_folders = app.filewatcher.paths
                if u'__all__' in fw_folders:
                    # Add watcher upon everything
                    self.config[u'apps'][app.app_id] = [ u'' ]
                else:
                    # Add a watcher with no contexts
                    self.config[u'apps'][app.app_id] = []
                apps.write_app_config(self.config)
            else:
                self.supports_watching = False
        # If watching is supported, make reference to config
        if self.supports_watching:
            self.app_config = config[u'apps'][app.app_id]
        else:
            self.app_config = None

    def raise_event(self, ty, pathname, src_pathname, isdir):
        if not self.supports_watching:
            return

        # Try to find both elements
        has_pathname = False
        has_src_pathname = False
        for context in self.app_config:
            complete_context = u'/var/nublic/data/' + context
            if to_utf8(pathname).startswith(complete_context):
                has_pathname = True
            if to_utf8(src_pathname).startswith(complete_context):
                has_src_pathname = True
        if ty == "move":
            if has_pathname and has_src_pathname:
                self.file_changed(ty, pathname, src_pathname, isdir, context)
            elif has_pathname and not has_src_pathname: # to, but no from -> create
                self.file_changed("create", pathname, '', isdir, context)
            elif not has_pathname and has_src_pathname: # from, but no to -> delete
                self.file_changed("delete", src_pathname, '', isdir, context)
        else:
            if has_pathname:
                self.file_changed(ty, pathname, src_pathname, isdir, context)

    def unicode_path(self, pathname):
        unicode_pathname = to_utf8(pathname)
        account_path = unicode_pathname.replace(u'/var/nublic/data/', '', 1) + u'/'
        return unicode_pathname, account_path
                    
    def add_context(self, pathname, do_touch = False):
        if not self.supports_watching:
            return

        #try:
        #    sys.stderr.write('Trying to add ' + pathname + '\n')
        #except:
        #    pass

        _, account_path = self.unicode_path(pathname)
        for expr in self.app_info.filewatcher.paths:
            regex = re.compile(expr, re.IGNORECASE)
            if regex.match(account_path[0:-1]):
                # We found a matching path
                to_add = True
                # Look whether parent folders are watched or not
                for already_path in self.app_config:
                    if already_path.startswith(account_path):
                        self.app_config.remove(already_path)
                    elif account_path.startswith(already_path):
                        to_add = False
                # If finally we add, do so and write config
                if to_add:
                    self.app_config.append(account_path)
                    apps.write_app_config(self.config)
                    #sys.stderr.write('Added ' + pathname + '\n')
                    # If we want to touch after adding, do so
                    if do_touch:
                        #sys.stderr.write('And touched\n')
                        self.touch(pathname, pathname)

    def touch(self, pathname, ctx):
        for inner_file in os.listdir(pathname):
            file_name = os.path.join(pathname, inner_file)
            self.file_changed("create", file_name, '', os.path.isdir(file_name), ctx)
            if os.path.isdir(file_name):
                self.touch(file_name, ctx)
                                
    def remove_context(self, pathname):
        if not self.supports_watching:
            return

        #try:
        #    sys.stderr.write('Trying to remove ' + pathname + '\n')
        #except:
        #    pass

        _, account_path = self.unicode_path(pathname)
        if account_path in self.app_config:
            self.app_config.remove(account_path)
            apps.write_app_config(self.config)

    def replace_context(self, prev_pathname, new_pathname, do_touch = False):
        if not self.supports_watching:
            return

        #try:
        #    sys.stderr.write('Trying to replace ' + prev_pathname + ' with ' + new_pathname + '\n')
        #except:
        #    pass
        
        _, prev_account_path = self.unicode_path(prev_pathname)
        _, new_account_path = self.unicode_path(new_pathname)
            
        if prev_account_path in self.app_config:
            # We had this path, so replace with new one
            self.app_config.remove(prev_account_path)
            self.app_config.append(new_account_path)
            apps.write_app_config(self.config)
        else:
             # Try as a new pathname
             self.add_context(new_pathname, do_touch)
             
    @dbus.service.signal(dbus_interface='com.nublic.filewatcher', signature='sssbs')
    def file_changed(self, ty, pathname, src_pathname, isdir, context):
        try:
            sys.stderr.write("%s %s (context %s)\n" % (ty, pathname, context))
        except:
            pass
