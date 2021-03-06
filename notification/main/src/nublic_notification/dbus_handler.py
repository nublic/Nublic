#!/usr/bin/python

'''
Created on 18/07/2011

@author: David Navarro Estruch
@copyright: 2011 Nublic
'''
import gtk
import dbus
import dbus.service
import dbus.exceptions
import gobject
from dbus.mainloop.glib import DBusGMainLoop

from elixir import metadata, setup_all
import dbus
import time

def __get_bind_uri(dbus_loop):
    # Get conexion values 
    bus = dbus.SystemBus(mainloop = dbus_loop)
    valueService = bus.get_object('com.nublic.resource', '/com/nublic/resource/nublic_notification/db')
    return valueService.value("uri", dbus_interface= 'com.nublic.resource.value')
    '''return message_sender("uri")''' 

def __check_nublic_resource_is_on(dbus_loop):
    WAITING_MAX = 200
    waited = 0
    should_exit = False
    while waited < WAITING_MAX and not should_exit:
        # Just do the thing below, but waiting for errors
        try:
            __get_bind_uri(dbus_loop)
            should_exit = True
        except dbus.exceptions.DBusException:
            time.sleep(2)
            waited = waited + 1

def initial_program_setup():
    pass

def do_main_program():
    dbus_loop = DBusGMainLoop(set_as_default=True)
    __check_nublic_resource_is_on(dbus_loop)
    metadata.bind = __get_bind_uri(dbus_loop)
    setup_all(create_tables = True)
    dbus_value = DBusValue(loop = dbus_loop)
    
    loop = gobject.MainLoop()
    
    gobject.threads_init()
    loop.run()
    
def program_cleanup():
    pass

def reload_program_config():
    pass

class DBusValue(dbus.service.Object):
    bus_path = 'com.nublic.notification'
    _base_object_path = '/com/nublic/notification'
    
    def __init__(self, loop = None):
        # Init DBus object
        self.object_path = "/com/nublic/notification/Messages"
        bus_name = dbus.service.BusName('com.nublic.notification', bus = dbus.SystemBus(mainloop = loop))
        dbus.service.Object.__init__(self, bus_name, object_path = self.object_path)

    @dbus.service.method('com.nublic.notification', in_signature = 'ssss', out_signature='s')
    def new_message(self, app, user, level, text):
        return str(notification.new_message(app, user, level, text))
