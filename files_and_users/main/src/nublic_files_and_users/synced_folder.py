import dbus.service
import os
import os.path
import pexpect
import shutil

from elixir import *
from model import *

SYNCED_ROOT = '/var/nublic/data/synced/'

class User(dbus.service.Object):
    def __init__(self, user_dbus, loop = None):
        self.user_dbus = user_dbus
        bus_name = dbus.service.BusName('com.nublic.files', bus=dbus.SystemBus())
        dbus.service.Object.__init__(self, bus_name, '/com/nublic/SyncedFolders')
    
    @dbus.service.signal(dbus_interface='com.nublic.files', signature='iss')
    def synced_folder_created(self, mid, name, owner):
        print "Synced folder %s created for user %s" % (name, owner)
    
    @dbus.service.signal(dbus_interface='com.nublic.files', signature='is')
    def synced_folder_deleted(self, mid, name):
        print "Synced folder %s deleted" % name
    
    @dbus.service.method('com.nublic.files', in_signature = 'i', out_signature='b')
    def synced_folder_exists(self, mid):
        m = SyncedFolder.get_by(id=mid)
        return not m is None
    
    @dbus.service.method('com.nublic.files', in_signature = 'i', out_signature='s')
    def get_synced_folder_name(self, mid):
        m = SyncedFolder.get_by(id=mid)
        return m.name
    
    @dbus.service.method('com.nublic.files', in_signature = 'i', out_signature='s')
    def get_synced_folder_owner(self, mid):
        m = SyncedFolder.get_by(id=mid)
        return m.user.username
    
    @dbus.service.method('com.nublic.files', in_signature = 'ss', out_signature='i')
    def create_synced_folder(self, name, owner):
        if not self.user_dbus.user_exists(owner):
            return -1
        # create in database
        user = User.get(username=owner)
        m = SyncedFolder(name=name, user=user)
        m.save()
        session.commit()
        # create in filesystem
        path = SYNCED_ROOT + str(m.id)
        os.makedirs(path, 0755)
        pexpect.run("git init", cwd=path)
        # change owner
        os.chown(path, user.uid, -1)
        for (dirpath, dirnames, filenames) in os.walk(path):
            for dirname in dirnames:
                c_path = os.path.join(dirpath, dirname)
                os.chown(c_path, user.uid, -1)
            for filename in filenames:
                c_path = os.path.join(dirpath, filename)
                os.chown(c_path, user.uid, -1)
        # return path id
        return m.id
    
    @dbus.service.method('com.nublic.files', in_signature = 'is', out_signature='')
    def change_synced_folder_name(self, mid, new_name):
        m = SyncedFolder.get_by(id=mid)
        if m is None:
            raise NameError()
        # change name
        m.name = new_name
        m.save_or_update()
        session.commit()
    
    @dbus.service.method('com.nublic.files', in_signature = 'ib', out_signature='')
    def delete_synced_folder(self, mid, remove_in_fs):
        m = SyncedFolder.get_by(id=mid)
        if m is None:
            raise NameError()
        # Delete in database
        m.delete()
        session.commit()
        # Remove from filesystem
        if (remove_in_fs):
            shutil.rmtree(SYNCED_ROOT + str(m.id))
