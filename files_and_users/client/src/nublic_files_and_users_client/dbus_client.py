'''
DBus client for Nublic files and users daemon
@author: Alejandro Serrano
'''

from dbus.mainloop.glib import DBusGMainLoop
import dbus

def create_user(username, name, password):
    dbus_loop = DBusGMainLoop()
    bus = dbus.SystemBus(mainloop = dbus_loop)
    userService = bus.get_object('com.nublic.users', '/com/nublic/Users')
    userService.create_user(username, password, name, dbus_interface= 'com.nublic.users')

def delete_user(username):
    dbus_loop = DBusGMainLoop()
    bus = dbus.SystemBus(mainloop = dbus_loop)
    userService = bus.get_object('com.nublic.users', '/com/nublic/Users')
    userService.delete_user(username, dbus_interface= 'com.nublic.users')
