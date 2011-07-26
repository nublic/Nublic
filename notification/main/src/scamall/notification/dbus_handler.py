#!/usr/bin/python

'''
Created on 18/07/2011

@author: David Navarro Estruch
'''
import gtk
import dbus
import dbus.service
import gobject
from dbus.mainloop.glib import DBusGMainLoop

import notification
from elixir import *


def initial_program_setup():
    setup_all(create_tables = False)

def do_main_program():
    dbus_loop = DBusGMainLoop(set_as_default=True)
    dbus = DBusValue(loop = dbus_loop)
    
    loop = gobject.MainLoop()
    gobject.threads_init()
    loop.run()
    

def program_cleanup():
    pass

def reload_program_config():
    pass

class DBusValue(dbus.service.Object):
    bus_path = 'com.scamall.notification'
    _base_object_path = '/com/scamall/notification'
    
    def __init__(self, loop = None):
        # Init DBus object
        self.object_path = "/com/scamall/notification/Messages"
        bus_name = dbus.service.BusName('com.scamall.notification', bus = dbus.SystemBus(mainloop = loop))
        dbus.service.Object.__init__(self, bus_name, self.object_path)

    @dbus.service.method('com.scamall.notification')
    def new_message(self, app, user, level, text):
        return str(notification.new_message(app, user, level, text))
