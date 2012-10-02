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
    userService.create_user(username, password, name, dbus_interface='com.nublic.users')

def change_user_name(username, name):
    dbus_loop = DBusGMainLoop()
    bus = dbus.SystemBus(mainloop = dbus_loop)
    userService = bus.get_object('com.nublic.users', '/com/nublic/Users')
    userService.change_user_shown_name(username, name, dbus_interface='com.nublic.users')

def delete_user(username):
    dbus_loop = DBusGMainLoop()
    bus = dbus.SystemBus(mainloop = dbus_loop)
    userService = bus.get_object('com.nublic.users', '/com/nublic/Users')
    userService.delete_user(username, dbus_interface='com.nublic.users')
    
def list_users():
    dbus_loop = DBusGMainLoop()
    bus = dbus.SystemBus(mainloop = dbus_loop)
    userService = bus.get_object('com.nublic.users', '/com/nublic/Users')
    notSplitted = userService.get_all_users(dbus_interface='com.nublic.users')
    if notSplitted == '':
        return []
    # Take elements apart
    splitted = string.split(notSplitted, ':')
    userList = []
    for userS in splitted:
        uid = userService.get_user_uid(userS, dbus_interface='com.nublic.users')
        shown_name = userService.get_user_shown_name(userS, dbus_interface='com.nublic.users')
        userList.append({ 'id': userS, 'uid': uid, 'shown_name': shown_name })
    return userList

def get_user_uid(username):
    dbus_loop = DBusGMainLoop()
    bus = dbus.SystemBus(mainloop = dbus_loop)
    userService = bus.get_object('com.nublic.users', '/com/nublic/Users')
    return userService.get_user_uid(username)

def create_mirror(name, owner):
    dbus_loop = DBusGMainLoop()
    bus = dbus.SystemBus(mainloop = dbus_loop)
    mirrorService = bus.get_object('com.nublic.files', '/com/nublic/Mirrors')
    return mirrorService.create_mirror(name, owner, dbus_interface='com.nublic.files')

def delete_mirror(mid):
    dbus_loop = DBusGMainLoop()
    bus = dbus.SystemBus(mainloop = dbus_loop)
    mirrorService = bus.get_object('com.nublic.files', '/com/nublic/Mirrors')
    mirrorService.delete_mirror(mid, dbus_interface='com.nublic.files')

def list_mirrors():
    dbus_loop = DBusGMainLoop()
    bus = dbus.SystemBus(mainloop = dbus_loop)
    mirrorService = bus.get_object('com.nublic.files', '/com/nublic/Mirrors')
    notSplitted = mirrorService.get_all_mirrors(dbus_interface='com.nublic.files')
    if notSplitted == '':
        return []
    # Take elements apart
    splitted = string.split(notSplitted, ':')
    mirrorList = []
    for midS in splitted:
        mid = int(midS)
        name = mirrorService.get_mirror_name(mid, dbus_interface='com.nublic.files')
        owner = mirrorService.get_mirror_owner(mid, dbus_interface='com.nublic.files')
        mirrorList.append({ 'id': mid, 'name': name, 'owner': owner })
    return mirrorList

def create_synced_folder(name, owner):
    dbus_loop = DBusGMainLoop()
    bus = dbus.SystemBus(mainloop = dbus_loop)
    mirrorService = bus.get_object('com.nublic.files', '/com/nublic/SyncedFolders')
    return mirrorService.create_synced_folder(name, owner, dbus_interface='com.nublic.files')

def delete_synced_folder(mid):
    dbus_loop = DBusGMainLoop()
    bus = dbus.SystemBus(mainloop = dbus_loop)
    mirrorService = bus.get_object('com.nublic.files', '/com/nublic/SyncedFolders')
    mirrorService.delete_synced_folder(mid, dbus_interface='com.nublic.files')

def list_synced_folders():
    dbus_loop = DBusGMainLoop()
    bus = dbus.SystemBus(mainloop = dbus_loop)
    mirrorService = bus.get_object('com.nublic.files', '/com/nublic/SyncedFolders')
    notSplitted = mirrorService.get_all_synced_folders(dbus_interface='com.nublic.files')
    if notSplitted == '':
        return []
    # Take elements apart
    splitted = string.split(notSplitted, ':')
    mirrorList = []
    for midS in splitted:
        mid = int(midS)
        name = mirrorService.get_synced_folder_name(mid, dbus_interface='com.nublic.files')
        owner = mirrorService.get_synced_folder_owner(mid, dbus_interface='com.nublic.files')
        mirrorList.append({ 'id': mid, 'name': name, 'owner': owner })
    return mirrorList
