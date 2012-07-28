
import dbus
import dbus.mainloop.glib
import string

import user

WORK_FOLDER_DATA_ROOT = "/var/nublic/data/work-folders/"

def _call_work_folder_method(m):
    dbus_loop = dbus.mainloop.glib.DBusGMainLoop()
    bus = dbus.SystemBus(mainloop = dbus_loop)
    o = bus.get_object('com.nublic.files', '/com/nublic/SyncedFolders')
    iface = dbus.Interface(o, dbus_interface='com.nublic.files')
    return m(iface)

def get_all_work_folders():
    notSplitted = _call_work_folder_method(lambda i: i.get_all_synced_folders())
    if notSplitted == '':
        return []
    # Take elements apart
    splitted = string.split(notSplitted, ':')
    folderList = []
    for fS in splitted:
        folderList.append(WorkFolder(int(fS)))
    return folderList

def create_work_folder(name, owner):
    fid = _call_work_folder_method(lambda i: i.create_synced_folder(name, owner))
    return WorkFolder(fid)

class WorkFolder:
    '''
    Represents a work folder (SparkleShare-like) in the Nublic file system
    '''
    def __init__(self, fid):
        self._id = fid
    
    def exists(self):
        return _call_work_folder_method(lambda i: i.synced_folder_exists(self._id))
    
    def get_id(self):
        return self._id
    
    def get_name(self):
        return _call_work_folder_method(lambda i: i.get_synced_folder_name(self._id))
    
    def change_name(self, name):
        return _call_work_folder_method(lambda i: i.change_synced_folder_name(self._id, name))
    
    def get_owner(self):
        return user.User(_call_work_folder_method(lambda i: i.get_synced_folder_owner(self._id)))
    
    def get_path(self):
        return WORK_FOLDER_DATA_ROOT + str(self._id)
    
    def delete(self, remove_in_file_system):
        return _call_work_folder_method(lambda i: i.delete_synced_folder(self._id, remove_in_file_system))
    
    def can_be_read_by(self, usr):
        return usr.can_read(self.get_path())
    
    def can_be_written_by(self, usr):
        return usr.can_write(self.get_path())
    