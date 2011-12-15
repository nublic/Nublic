'''
DBus client for Nublic files and users daemon
@author: Alejandro Serrano
'''

from dbus.mainloop.glib import DBusGMainLoop
import dbus
import string

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

def create_mirror(name, owner):
    dbus_loop = DBusGMainLoop()
    bus = dbus.SystemBus(mainloop = dbus_loop)
    mirrorService = bus.get_object('com.nublic.files', '/com/nublic/Mirrors')
    return mirrorService.create_mirror(name, owner, dbus_interface= 'com.nublic.files')

def delete_mirror(mid):
    dbus_loop = DBusGMainLoop()
    bus = dbus.SystemBus(mainloop = dbus_loop)
    mirrorService = bus.get_object('com.nublic.files', '/com/nublic/Mirrors')
    mirrorService.delete_mirror(mid, dbus_interface= 'com.nublic.files')

def list_mirrors(mid):
    dbus_loop = DBusGMainLoop()
    bus = dbus.SystemBus(mainloop = dbus_loop)
    mirrorService = bus.get_object('com.nublic.files', '/com/nublic/Mirrors')
    notSplitted = mirrorService.get_all_mirrors(dbus_interface= 'com.nublic.files')
    splitted = string.split(notSplitted, ':')
    mirrorList = []
    for midS in splitted:
        mid = int(midS)
        name = mirrorService.get_mirror_name(mid, dbus_interface= 'com.nublic.files')
        owner = mirrorService.get_mirror_owner(mid, dbus_interface= 'com.nublic.files')
        mirrorList.append({ 'id': mid, 'name': name, 'owner': owner })
    return mirrorList

def create_synced_folder(name, owner):
    dbus_loop = DBusGMainLoop()
    bus = dbus.SystemBus(mainloop = dbus_loop)
    mirrorService = bus.get_object('com.nublic.files', '/com/nublic/SyncedFolders')
    return mirrorService.create_synced_folder(name, owner, dbus_interface= 'com.nublic.files')

def delete_synced_folder(mid):
    dbus_loop = DBusGMainLoop()
    bus = dbus.SystemBus(mainloop = dbus_loop)
    mirrorService = bus.get_object('com.nublic.files', '/com/nublic/SyncedFolders')
    mirrorService.delete_synced_folder(mid, dbus_interface= 'com.nublic.files')

def list_synced_folders(mid):
    dbus_loop = DBusGMainLoop()
    bus = dbus.SystemBus(mainloop = dbus_loop)
    mirrorService = bus.get_object('com.nublic.files', '/com/nublic/SyncedFolders')
    notSplitted = mirrorService.get_all_synced_folders(dbus_interface= 'com.nublic.files')
    splitted = string.split(notSplitted, ':')
    mirrorList = []
    for midS in splitted:
        mid = int(midS)
        name = mirrorService.get_synced_folder_name(mid, dbus_interface= 'com.nublic.files')
        owner = mirrorService.get_synced_folder_owner(mid, dbus_interface= 'com.nublic.files')
        mirrorList.append({ 'id': mid, 'name': name, 'owner': owner })
    return mirrorList
