
from ..dbus_in_other_thread import call_expecting_return, call_without_return
from ..rpcbd_client import rpcbd_call_return, rpcbd_call
import string

import user

MIRROR_DATA_ROOT = "/var/nublic/data/mirrors/"

def _call_mirror_method_return(m, use_dbus=False):
    if use_dbus:
        return call_expecting_return('com.nublic.files', '/com/nublic/Mirrors', 'com.nublic.files', m)
    else:
        return rpcbd_call_return(5440, m)

def _call_mirror_method(m, use_dbus=False):
    if use_dbus:
        return call_without_return('com.nublic.files', '/com/nublic/Mirrors', 'com.nublic.files', m)
    else:
        return rpcbd_call(5440, m)

def get_all_mirrors(use_dbus=False):
    notSplitted = _call_mirror_method_return(lambda i: i.get_all_mirrors(), use_dbus)
    if notSplitted == '':
        return []
    # Take elements apart
    splitted = string.split(notSplitted, ':')
    mirrorList = []
    for midS in splitted:
        mirrorList.append(Mirror(int(midS), use_dbus))
    return mirrorList

def create_mirror(name, owner, use_dbus=False):
    mid = _call_mirror_method_return(lambda i: i.create_mirror(name, owner), use_dbus)
    return Mirror(mid)

class Mirror:
    '''
    Represents a mirror in the Nublic file system
    '''
    def __init__(self, mid, use_dbus=False):
        self._id = mid
        self.use_dbus = use_dbus
    
    def exists(self):
        return _call_mirror_method_return(lambda i: i.mirror_exists(self._id), self.use_dbus)
    
    def get_id(self):
        return self._id
    
    def get_name(self):
        return _call_mirror_method_return(lambda i: i.get_mirror_name(self._id), self.use_dbus)
    
    def change_name(self, name):
        return _call_mirror_method_return(lambda i: i.change_mirror_name(self._id, name), self.use_dbus)
    
    def get_owner(self):
        return user.User(_call_mirror_method_return(lambda i: i.get_mirror_owner(self._id), self.use_dbus), self.use_dbus)
    
    def get_path(self):
        return MIRROR_DATA_ROOT + str(self._id)
    
    def delete(self, remove_in_file_system):
        _call_mirror_method(lambda i: i.delete_mirror(self._id, remove_in_file_system), self.use_dbus)
    
    def can_be_read_by(self, usr):
        return usr.can_read(self.get_path())
    
    def can_be_written_by(self, usr):
        return usr.can_write(self.get_path())
    
