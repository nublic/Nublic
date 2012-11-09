import dbus.service
import os
import shutil

from elixir import *
from model import *

MIRROR_ROOT = '/var/nublic/data/mirrors/'

class MirrorDBus(dbus.service.Object):
    def __init__(self, user_dbus, loop = None, initialize_dbus=True):
        self.user_dbus = user_dbus
        if initialize_dbus:
            bus_name = dbus.service.BusName('com.nublic.files', bus=dbus.SystemBus())
            dbus.service.Object.__init__(self, bus_name, '/com/nublic/Mirrors')
    
    @dbus.service.signal(dbus_interface='com.nublic.files', signature='iss')
    def mirror_created(self, mid, name, owner):
        print "Mirror %s created for user %s" % (name, owner)
    
    @dbus.service.signal(dbus_interface='com.nublic.files', signature='is')
    def mirror_deleted(self, mid, name):
        print "Mirror %s deleted" % name
    
    @dbus.service.method('com.nublic.files', in_signature = 'i', out_signature='b')
    def mirror_exists(self, mid):
        m = Mirror.get_by(id=mid)
        return not m is None
    
    @dbus.service.method('com.nublic.files', in_signature = '', out_signature='s')
    def get_all_mirrors(self):
        ''' Gets all mirrors separated by :'''
        r = ""
        for m in Mirror.query.all():
            if r != "":
                r = r + ":"
            r = r + str(m.id)
        return r
    
    @dbus.service.method('com.nublic.files', in_signature = 'i', out_signature='s')
    def get_mirror_name(self, mid):
        m = Mirror.get_by(id=mid)
        return m.name
    
    @dbus.service.method('com.nublic.files', in_signature = 'i', out_signature='s')
    def get_mirror_owner(self, mid):
        m = Mirror.get_by(id=mid)
        return m.user.username
    
    @dbus.service.method('com.nublic.files', in_signature = 'ss', out_signature='i')
    def create_mirror(self, name, owner):
        if not self.user_dbus.user_exists(owner):
            return -1
        # create in database
        user = User.get_by(username=owner)
        m = Mirror(name=name, user=user)
        session.add(m)
        session.commit()
        # create in filesystem
        path = MIRROR_ROOT + str(m.id)
        os.makedirs(path, 0755)
        # change owner
        os.chown(path, user.uid, -1)
        # return path id
        return m.id
    
    @dbus.service.method('com.nublic.files', in_signature = 'is', out_signature='')
    def change_mirror_name(self, mid, new_name):
        m = Mirror.get_by(id=mid)
        if m is None:
            raise NameError()
        # Change name
        m.name = new_name
        # m.save_or_update()
        session.commit()
    
    @dbus.service.method('com.nublic.files', in_signature = 'ib', out_signature='')
    def delete_mirror(self, mid, remove_in_fs):
        m = Mirror.get_by(id=mid)
        if m is None:
            raise NameError()
        # Delete in database
        m.delete()
        session.commit()
        # Remove from filesystem
        if (remove_in_fs):
            shutil.rmtree(MIRROR_ROOT + str(m.id))
