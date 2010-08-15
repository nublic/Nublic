#!/usr/bin/python

'''
Created on 10/08/2010

@author: David Navarro Estruch
'''
import dbus
import dbus.service
from dbus.mainloop.glib import DBusGMainLoop
from scamall.resource.select_provider import SelectProvider
import gobject



class dbusMain(dbus.service.Object):
    '''
    TODO
    '''


    def __init__(self, params):
        '''
        Constructor
        '''
        pass
      
class DBusValue(dbus.service.Object):
    bus_path = 'com.scamall.resource'
    _base_object_path = '/com/scamall/resource'
    
    def __init__(self, app_name, key):
        self.app = app_name
        self.key = key
        provider = SelectProvider().get_provider(app_name, key)
        self.provider = provider
        self.object_path = self._base_object_path + '/' + app_name + '/' + key 
        # Init DBus object
        bus_name = dbus.service.BusName('com.scamall.resource', bus = dbus.SystemBus())
        dbus.service.Object.__init__(self, bus_name, self.object_path)

    @dbus.service.method('com.scamall.resource')
    def value(self, subkey):
        return self.provider.value(self.app, self.key, subkey)

if __name__ == '__main__':
    DBusGMainLoop(set_as_default=True)
    dbus = DBusValue("app","key")
    
    loop = gobject.MainLoop()
    loop.run()

