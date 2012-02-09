'''
Created on 08/09/2011

@author: Alejandro Serrano Mena
@copyright: 2011 Nublic
'''

# import dbus
import dbus.service
import sys

def to_utf8(string):
    return unicode(string, 'utf-8')

def from_utf8(string):
    return string.encode('utf-8')

class DbusSignaler(dbus.service.Object):
    '''
    Sends signals via D-Bus
    '''
    def __init__(self, app_name, contexts):
        bus_name = dbus.service.BusName('com.nublic.filewatcher', bus=dbus.SystemBus())
        dbus.service.Object.__init__(self, bus_name, '/com/nublic/filewatcher/' + app_name)
        self.contexts = map(lambda x: u'/var/nublic/data/' + x, contexts)
        self.app_name = app_name

    def raise_event(self, ty, pathname, src_pathname, isdir):
        for context in self.contexts:
            if to_utf8(pathname).startswith(context):
                sys.stderr.write("Sending to context " + context + " and app " + self.app_name + "\n")
                self.file_changed(ty, pathname, src_pathname, isdir, context)

    @dbus.service.signal(dbus_interface='com.nublic.filewatcher', signature='sssbs')
    def file_changed(self, ty, pathname, src_pathname, isdir, context):
        pass
        # print "%s %s (context %s)" % (ty, pathname, context)
