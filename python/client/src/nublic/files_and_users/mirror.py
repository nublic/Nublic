
import dbus
import dbus.mainloop.glib
import string

import user

MIRROR_DATA_ROOT = "/var/nublic/data/mirrors/"

def _call_mirror_method(m):
    dbus_loop = dbus.mainloop.glib.DBusGMainLoop()
    bus = dbus.SystemBus(mainloop = dbus_loop)
    o = bus.get_object('com.nublic.files', '/com/nublic/Mirrors')
    iface = dbus.Interface(o, dbus_interface='com.nublic.files')
    return m(iface)

def get_all_mirrors():
    notSplitted = _call_mirror_method(lambda i: i.get_all_mirrors())
    if notSplitted == '':
        return []
    # Take elements apart
    splitted = string.split(notSplitted, ':')
    mirrorList = []
    for midS in splitted:
        mirrorList.append(Mirror(int(midS)))
    return mirrorList

def create_mirror(name, owner):
    mid = _call_mirror_method(lambda i: i.create_mirror(name, owner))
    return Mirror(mid)

class Mirror:
    '''
    Represents a mirror in the Nublic file system
    '''
    def __init__(self, mid):
        self._id = mid
    
    def exists(self):
        return _call_mirror_method(lambda i: i.mirror_exists(self._id))
    
    def get_id(self):
        return self._id
    
    def get_name(self):
        return _call_mirror_method(lambda i: i.get_mirror_name(self._id))
    
    def change_name(self, name):
        return _call_mirror_method(lambda i: i.change_mirror_name(self._id, name))
    
    def get_owner(self):
        return user.User(_call_mirror_method(lambda i: i.get_mirror_owner(self._id)))
    
    def get_path(self):
        return MIRROR_DATA_ROOT + str(self._id)
    
    def delete(self, remove_in_file_system):
        return _call_mirror_method(lambda i: i.delete_mirror(self._id, remove_in_file_system))
    
    def can_be_read_by(self, usr):
        return usr.can_read(self.get_path())
    
    def can_be_written_by(self, usr):
        return usr.can_write(self.get_path())
    