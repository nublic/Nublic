import dbus.service
import os
import os.path
import pexpect
import shutil

from elixir import *
from model import *

SYNCED_REPO_ROOT = '/var/nublic/work-folders'
SYNCED_ROOT = '/var/nublic/data/work-folders'

class SyncedFolderDBus(dbus.service.Object):
    def __init__(self, user_dbus, loop = None, initialize_dbus=True):
        self.user_dbus = user_dbus
        if initialize_dbus:
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
    
    @dbus.service.method('com.nublic.files', in_signature = '', out_signature='s')
    def get_all_synced_folders(self):
        ''' Gets all synced folders separated by :'''
        r = ""
        for m in SyncedFolder.query.all():
            if r != "":
                r = r + ":"
            r = r + str(m.id)
        return r
    
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
        user = User.get_by(username=owner)
        m = SyncedFolder(name=name, user=user)
        session.add(m)
        session.commit()
        # create in filesystem
        # first, create bare repo
        repo_path = SYNCED_REPO_ROOT + '/' + str(m.id)
        os.makedirs(repo_path, 0755)
        pexpect.run("git --bare init", cwd=repo_path)
        # now create the inner clone
        clone_path = SYNCED_ROOT + '/' + str(m.id)
        pexpect.run("git clone " + repo_path, cwd=SYNCED_ROOT)
        # create info
        info_path = clone_path + '/INFO'
        info_file = open(info_path, 'w')
        info_file.write("This is a new Nublic work folder.\nYou may safely remove this file.")
        info_file.flush()
        info_file.close()
        # add to repo and push
        pexpect.run('git --git-dir=' + clone_path + '/.git --work-tree=' + clone_path + ' add INFO', cwd=clone_path)
        pexpect.run('git --git-dir=' + clone_path + '/.git --work-tree=' + clone_path + ' -c user.name=' + owner + ' commit -m "Initial commit"', cwd=clone_path)
        pexpect.run('git --git-dir=' + clone_path + '/.git --work-tree=' + clone_path + ' push --all', cwd=clone_path)
        # create hook in server
        hook_path = repo_path + "/hooks/post-receive"
        hook_file = open(hook_path, 'w')
        hook_file.write("#!/bin/sh\n")
        hook_file.write("git --git-dir=" + clone_path + "/.git fetch\n")
        hook_file.write("git --git-dir=" + clone_path + "/.git --work-tree=" + clone_path + " merge origin/master\n")
        hook_file.flush()
        hook_file.close()
        os.chmod(hook_path, 0777)
        # change owners
        for path in [repo_path, clone_path]:
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
        # m.save_or_update()
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
