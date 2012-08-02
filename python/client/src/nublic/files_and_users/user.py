
import os
import string

from ..dbus_in_other_thread import call_expecting_return, call_without_return
import mirror
import work_folder

DATA_ROOT = "/var/nublic/data"

def _call_user_method_return(m):
    return call_expecting_return('com.nublic.users', '/com/nublic/Users', 'com.nublic.users', m)

def _call_user_method(m):
    return call_without_return('com.nublic.users', '/com/nublic/Users', 'com.nublic.users', m)

def get_all_users():
    notSplitted = _call_user_method_return(lambda i: i.get_all_users())
    if notSplitted == '':
        return []
    # Take elements apart
    splitted = string.split(notSplitted, ':')
    userList = []
    for username in splitted:
        userList.append(User(username))
    return userList

class User:
    def __init__(self, username):
        self._username = username
        self._uid = None
        self._readable_paths = [DATA_ROOT]
        self._writable_paths = [DATA_ROOT + '/nublic-only']
        
    def exists(self):
        return _call_user_method_return(lambda i: i.user_exists(self._username))
    
    def create(self, password, shown_name):
        return _call_user_method_return(lambda i: i.user_create(self._username, password, shown_name))
    
    def delete(self):
        return _call_user_method_return(lambda i: i.delete_user(self._username))
    
    def get_username(self):
        return self._username
    
    def get_id(self):
        if self._uid == None:
            self._uid = _call_user_method_return(lambda i: i.get_user_uid(self._username))
        return self._uid
    
    def get_shown_name(self):
        return _call_user_method_return(lambda i: i.get_user_shown_name(self._username))
    
    def change_shown_name(self, shown_name):
        _call_user_method(lambda i: i.change_user_shown_name(self._username, shown_name))
    
    def change_password(self, old, new):
        _call_user_method(lambda i: i.change_user_password(self._username, old, new))
    
    def is_owner(self, path):
        owner_uid = os.stat(path).st_uid
        return self.get_id() == owner_uid
    
    def assign_file(self, path, touch_after):
        if not path.startswith(DATA_ROOT + '/'):
            raise ValueError('You are not allowed to change that path\'s owner')
        path_to_send = path.replace(DATA_ROOT, '', 1)
        _call_user_method(lambda i: i.assign_file(self._username, path_to_send, touch_after))
    
    def _check_permissions(self, path, allowed_paths, group_bits, others_bits):
        # Check if it is in a allowed path
        filtered = filter(lambda p: path.startswith(p + '/') or path == p, allowed_paths)
        if not filtered:
            return False
        # If it is the owner, it can do whathever it wants
        if self.is_owner(path):
            return True
        # Check group and others permissions
        mode = os.stat(path).st_mode
        return (mode & group_bits != 0) or (mode & others_bits != 0)
    
    def can_read(self, path):
        return self._check_permissions(path, self._readable_paths, 0040, 0004)
    
    def can_write(self, path):
        return self._check_permissions(path, self._writable_paths, 0020, 0002)
    
    def get_owned_mirrors(self):
        return filter(lambda m: self.is_owner(m.get_path()), mirror.get_all_mirrors())
    
    def get_owned_work_folders(self):
        return filter(lambda m: self.is_owner(m.get_path()), work_folder.get_all_work_folders())
    
    def get_accessible_mirrors(self):
        return filter(lambda m: self.can_read(m.get_path()), mirror.get_all_mirrors())
    
    def get_accessible_work_folders(self):
        return filter(lambda m: self.can_read(m.get_path()), work_folder.get_all_work_folders())
    
    def add_public_key(self, key):
        _call_user_method(lambda i: i.add_public_key(self._username, key))
    