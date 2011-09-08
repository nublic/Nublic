'''
Created on 08/09/2011

@author: Alejandro Serrano Mena
@copyright: 2011 Nublic
'''

import dbus

"""
Sends signals via D-Bus
"""
class DbusSignaler(dbus.service.Object):
    def __init__(self, app_name):
        dbus.service.Object.__init__(self, dbus.SystemBus(), '/com/nublic/fielwatcher/' + app_name)

    @dbus.service.signal(dbus_interface='com.nublic.filewatcher',
                         signature='sssb')
    def file_changed(self, ty, pathname, src_pathname, isdir):
        print "%s %s" % (ty, pathname)
