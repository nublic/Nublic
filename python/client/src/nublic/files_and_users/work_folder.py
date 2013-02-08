from ..dbus_in_other_thread import call_expecting_return, call_without_return
from ..rpcbd_client import rpcbd_call_return, rpcbd_call
import string

import user

WORK_FOLDER_DATA_ROOT = "/var/nublic/data/work-folders/"


def _call_work_folder_method_return(m, use_dbus=False):
    if use_dbus:
        return call_expecting_return('com.nublic.files', '/com/nublic/SyncedFolders', 'com.nublic.files', m)
    else:
        return rpcbd_call_return(5440, m)


def _call_work_folder_method(m, use_dbus=False):
    if use_dbus:
        return call_without_return('com.nublic.files', '/com/nublic/SyncedFolders', 'com.nublic.files', m)
    else:
        return rpcbd_call(5440, m)


def get_all_work_folders(use_dbus=False):
    notSplitted = _call_work_folder_method_return(
        lambda i: i.get_all_synced_folders(), use_dbus)
    if notSplitted == '':
        return []
    # Take elements apart
    splitted = string.split(notSplitted, ':')
    folderList = []
    for fS in splitted:
        folderList.append(WorkFolder(int(fS), use_dbus))
    return folderList


def create_work_folder(name, owner, use_dbus=False):
    fid = _call_work_folder_method_return(
        lambda i: i.create_synced_folder(name, owner), use_dbus)
    return WorkFolder(fid, use_dbus)


class WorkFolder:
    '''
    Represents a work folder (SparkleShare-like) in the Nublic file system
    '''
    def __init__(self, fid, use_dbus=False):
        self._id = fid
        self.use_dbus = use_dbus

    def as_map(self):
        return {'id': self.get_id(), 'exists': self.exists(), 'name': self.get_name(), 'owner': self.get_owner().as_map()
                }

    def exists(self):
        return _call_work_folder_method_return(lambda i: i.synced_folder_exists(self._id), self.use_dbus)

    def get_id(self):
        return self._id

    def get_name(self):
        return _call_work_folder_method_return(lambda i: i.get_synced_folder_name(self._id), self.use_dbus)

    def change_name(self, name):
        return _call_work_folder_method_return(lambda i: i.change_synced_folder_name(self._id, name), self.use_dbus)

    def get_owner(self):
        return user.User(_call_work_folder_method_return(lambda i: i.get_synced_folder_owner(self._id), self.use_dbus), self.use_dbus)

    def get_path(self):
        return WORK_FOLDER_DATA_ROOT + unicode(self._id)

    def delete(self, remove_in_file_system):
        _call_work_folder_method(lambda i: i.delete_synced_folder(
            self._id, remove_in_file_system), self.use_dbus)

    def can_be_read_by(self, usr):
        return usr.can_read(self.get_path())

    def can_be_written_by(self, usr):
        return usr.can_write(self.get_path())
