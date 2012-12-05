import os
import string

from ..dbus_in_other_thread import call_expecting_return, call_without_return
from ..rpcbd_client import rpcbd_call_return, rpcbd_call
import mirror
import work_folder
from nublic_server.files import PermissionError

DATA_ROOT = "/var/nublic/data"


def _call_user_method_return(m, use_dbus=False):
    if use_dbus:
        return call_expecting_return('com.nublic.users', '/com/nublic/Users', 'com.nublic.users', m)
    else:
        return rpcbd_call_return(5440, m)


def _call_user_method(m):
    if use_dbus:
        return call_without_return('com.nublic.users', '/com/nublic/Users', 'com.nublic.users', m)
    else:
        return rpcbd_call(5440, m)


def get_all_users(use_dbus=False):
    notSplitted = _call_user_method_return(lambda i: i.get_all_users(), use_dbus)
    if notSplitted == '':
        return []
    # Take elements apart
    splitted = string.split(notSplitted, ':')
    userList = []
    for username in splitted:
        userList.append(User(username, use_dbus))
    return userList


def get_file_owner(path, use_dbus=False):
    owner_uid = os.stat(path).st_uid
    for u in get_all_users(use_dbus):
        if u.get_id() == owner_uid:
            return u
    return None


def is_file_shared(path):
    mode = os.stat(path).st_mode
    return (mode & 0040 != 0) or (mode & 0004 != 0)


class User:
    def __init__(self, username, use_dbus=False):
        self._username = username
        self._uid = None
        self._readable_paths = [DATA_ROOT]
        self._writable_paths = [os.path.join(DATA_ROOT, 'nublic-only')]
        self.use_dbus = use_dbus

    def as_map(self):
        return { 'username' : self.get_username()
               , 'exists'   : self.exists()
               , 'uid'      : self.get_id()
               , 'shown'    : self.get_shown_name()
               }

    def exists(self):
        return _call_user_method_return(lambda i: i.user_exists(self._username), self.use_dbus)

    def create(self, password, shown_name):
        return _call_user_method_return(lambda i: i.create_user(self._username, password, shown_name), self.use_dbus)

    def delete(self):
        return _call_user_method_return(lambda i: i.delete_user(self._username), self.use_dbus)

    def get_username(self):
        return self._username

    def get_id(self):
        if self._uid == None:
            self._uid = _call_user_method_return(lambda i: i.get_user_uid(self._username), self.use_dbus)
        return self._uid

    def get_shown_name(self):
        return _call_user_method_return(lambda i: i.get_user_shown_name(self._username), self.use_dbus)

    def change_shown_name(self, shown_name):
        _call_user_method(lambda i: i.change_user_shown_name(self._username, shown_name), self.use_dbus)

    def change_password(self, old, new):
        _call_user_method(lambda i: i.change_user_password(self._username, old, new), self.use_dbus)

    def is_owner(self, path):
        owner_uid = os.stat(path).st_uid
        return self.get_id() == owner_uid

    def assign_file(self, path, touch_after=True):
        if not path.startswith(DATA_ROOT + '/'):
            raise ValueError('You are not allowed to change that path\'s owner')
        path_to_send = path.replace(DATA_ROOT, '', 1)
        _call_user_method(lambda i: i.assign_file(self._username, path_to_send, touch_after), self.use_dbus)

    def _check_permissions(self, path, allowed_paths, group_bits, others_bits):
        # Check if it is in a allowed path
        filtered = filter(lambda p: path.startswith(p + '/') or path == p, allowed_paths)
        if not filtered:
            return False
        if not os.path.exists(path):
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

    def try_write(self, path):
        ''' Throws PermissionError if the file
        given does not have permission to be written'''
        if not self.can_write(path):
            raise PermissionError(self.get_username(), path, "Write")

    def try_read(self, path):
        ''' Throws PermissionError if the file
        given does not have permission to be read'''
        if not self.can_read(path):
            raise PermissionError(self.get_username(), path, "Read")

    def try_write_recursive(self, path):
        ''' Throws PermissionError if ANY file under the file
        given does not have permission to be written'''
        if not os.path.exists(path):
            raise PermissionError(self.get_username(), path, "Read")
        if os.path.isdir(path):
            [self.try_write_recursive(os.path.join(path, s))
                    for s in os.listdir(path)]
        else:
            self.try_write(path)

    def try_read_recursive(self, path, uid):
        ''' Throws PermissionError if ANY file under the file
        given does not have permission to be written'''
        if not os.path.exists(path):
            raise PermissionError(self.get_username(), path, "Read")
        if os.path.isdir(path):
            [self.try_read_recursive(s) for s in os.listdir(path)]
        else:
            self.try_read(path)

    def get_owned_mirrors(self):
        return filter(lambda m: self.is_owner(m.get_path()), mirror.get_all_mirrors())

    def get_owned_work_folders(self):
        return filter(lambda m: self.is_owner(m.get_path()), work_folder.get_all_work_folders(self.use_dbus))

    def get_accessible_mirrors(self):
        return filter(lambda m: self.can_read(m.get_path()), mirror.get_all_mirrors(self.use_dbus))

    def get_accessible_work_folders(self):
        return filter(lambda m: self.can_read(m.get_path()), work_folder.get_all_work_folders(self.use_dbus))

    def add_public_key(self, key):
        _call_user_method(lambda i: i.add_public_key(self._username, key), self.use_dbus)
