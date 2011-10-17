'''
Created on 08/09/2011

@author: Alejandro Serrano Mena
@copyright: 2011 Nublic
'''

# import dbus
import dbus.service

class DbusSignaler(dbus.service.Object):
    '''
    Sends signals via D-Bus
    '''
    def __init__(self, app_name, context):
        bus_name = dbus.service.BusName('com.nublic.filewatcher', bus=dbus.SystemBus())
        dbus.service.Object.__init__(self, bus_name, '/com/nublic/filewatcher/' + app_name)
        self.context = '/var/nublic/data/' + context

    def raise_event(self, ty, pathname, src_pathname, isdir):
        if pathname.startswith(self.context):
            self.file_changed(ty, pathname, src_pathname, isdir, self.context)

    @dbus.service.signal(dbus_interface='com.nublic.filewatcher', signature='sssbs')
    def file_changed(self, ty, pathname, src_pathname, isdir, context):
        print "%s %s (context %s)" % (ty, pathname, context)
