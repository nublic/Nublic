
from ..dbus_in_other_thread import call_expecting_return, call_without_return
import string

import user

WORK_FOLDER_DATA_ROOT = "/var/nublic/data/work-folders/"

def _call_work_folder_method_return(m):
    return call_expecting_return('com.nublic.files', '/com/nublic/SyncedFolders', 'com.nublic.files', m)

def _call_work_folder_method(m):
    return call_without_return('com.nublic.files', '/com/nublic/SyncedFolders', 'com.nublic.files', m)

def get_all_work_folders():
    notSplitted = _call_work_folder_method_return(lambda i: i.get_all_synced_folders())
    if notSplitted == '':
        return []
    # Take elements apart
    splitted = string.split(notSplitted, ':')
    folderList = []
    for fS in splitted:
        folderList.append(WorkFolder(int(fS)))
    return folderList

def create_work_folder(name, owner):
    fid = _call_work_folder_method_return(lambda i: i.create_synced_folder(name, owner))
    return WorkFolder(fid)

class WorkFolder:
    '''
    Represents a work folder (SparkleShare-like) in the Nublic file system
    '''
    def __init__(self, fid):
        self._id = fid
    
    def exists(self):
        return _call_work_folder_method_return(lambda i: i.synced_folder_exists(self._id))
    
    def get_id(self):
        return self._id
    
    def get_name(self):
        return _call_work_folder_method_return(lambda i: i.get_synced_folder_name(self._id))
    
    def change_name(self, name):
        return _call_work_folder_method_return(lambda i: i.change_synced_folder_name(self._id, name))
    
    def get_owner(self):
        return user.User(_call_work_folder_method_return(lambda i: i.get_synced_folder_owner(self._id)))
    
    def get_path(self):
        return WORK_FOLDER_DATA_ROOT + str(self._id)
    
    def delete(self, remove_in_file_system):
        _call_work_folder_method(lambda i: i.delete_synced_folder(self._id, remove_in_file_system))
    
    def can_be_read_by(self, usr):
        return usr.can_read(self.get_path())
    
    def can_be_written_by(self, usr):
        return usr.can_write(self.get_path())
    