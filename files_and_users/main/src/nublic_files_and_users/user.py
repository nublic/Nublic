import dbus.service
import os.path
import pexpect
import pwd
import spwd

from elixir import *
from model import User

# Needs a group 'nublic' in the system

# APACHE_PASSWD_FILE = "/var/nublic/conf/apache.passwd"
APACHE_PASSWD_FILE = "apache.passwd" # for debugging purposes

class User(dbus.service.Object):
    def __init__(self, loop = None):
        bus_name = dbus.service.BusName('com.nublic.users', bus=dbus.SystemBus())
        dbus.service.Object.__init__(self, bus_name, '/com/nublic/Users')
    
    @dbus.service.signal(dbus_interface='com.nublic.users', signature='ss')
    def user_created(self, username, name):
        print "User %s created" % username
    
    @dbus.service.signal(dbus_interface='com.nublic.users', signature='ss')
    def user_deleted(self, username, name):
        print "User %s deleted" % username
    
    @dbus.service.signal(dbus_interface='com.nublic.users', signature='ss')
    def user_shown_name_changed(self, username, name):
        print "User %s changed shown name to %s" % (username, name)
    
    @dbus.service.method('com.nublic.users', in_signature = 's', out_signature='')
    def user_exists(self, username):
        try:
            spwd.getspnam(username)
            return True
        except KeyError:
            return False
    
    def get_user_uid(self, username):
        user = pwd.getpwnam(username)
        return user[2] # Corresponds to uid
    
    @dbus.service.method('com.nublic.users', in_signature = 'sss', out_signature='')
    def create_user(self, username, password, name):
        if ' ' in username or self.user_exists(username):
            raise NameError()
        # passwd
        pexpect.run('useradd -M -G nublic -N ' + username)
        passwd_child = pexpect.spawn('passwd ' + username)
        passwd_child.expect('.*:')
        passwd_child.sendline(password)
        passwd_child.expect('.*:')
        passwd_child.sendline(password)
        print("Added in passwd")
        # samba
        smbpasswd_child = pexpect.spawn('smbpasswd -a ' + username)
        smbpasswd_child.expect('.*:')
        smbpasswd_child.sendline(password)
        smbpasswd_child.expect('.*:')
        smbpasswd_child.sendline(password)
        print("Added in smbpasswd")
        # apache
        if os.path.exists(APACHE_PASSWD_FILE):
            htpasswd_child = pexpect.spawn('htpasswd ' + APACHE_PASSWD_FILE + ' ' + username)
        else:
            htpasswd_child = pexpect.spawn('htpasswd -c ' + APACHE_PASSWD_FILE + ' ' + username)
        htpasswd_child.expect('.*:')
        htpasswd_child.sendline(password)
        htpasswd_child.expect('.*:')
        htpasswd_child.sendline(password)
        print("Added in htpasswd")
        # database
        uid = self.get_user_id(username)
        user = User(username, uid, name)
        user.save()
        session.commit()
        # Notify
        self.user_created(username, name)
    
    @dbus.service.method('com.nublic.users', in_signature = 'sss', out_signature='')
    def change_user_password(self, username, old_password, new_password):
        if ' ' in username or not self.user_exists(username):
            raise NameError()
        # passwd
        passwd_child = pexpect.spawn('passwd ' + username)
        passwd_child.expect('.*:')
        passwd_child.sendline(old_password)
        passwd_child.expect('.*:')
        passwd_child.sendline(new_password)
        passwd_child.expect('.*:')
        passwd_child.sendline(new_password)
        print("Changed in passwd")
        # samba
        smbpasswd_child = pexpect.spawn('smbpasswd ' + username)
        smbpasswd_child.expect('.*:')
        smbpasswd_child.sendline(new_password)
        smbpasswd_child.expect('.*:')
        smbpasswd_child.sendline(new_password)
        print("Changed in smbpasswd")
        # apache
        htpasswd_child = pexpect.spawn('htpasswd ' + APACHE_PASSWD_FILE + ' ' + username)
        htpasswd_child.expect('.*:')
        htpasswd_child.sendline(new_password)
        htpasswd_child.expect('.*:')
        htpasswd_child.sendline(new_password)
        print("Changed in htpasswd")
    
    @dbus.service.method('com.nublic.users', in_signature = 's', out_signature='s')
    def get_user_shown_name(self, username):
        if ' ' in username or not self.user_exists(username):
            raise NameError()
        # query in database
        user = User.get_by(username=username)
        return user.name
    
    @dbus.service.method('com.nublic.users', in_signature = 'ss', out_signature='')
    def change_user_shown_name(self, username, name):
        if ' ' in username or not self.user_exists(username):
            raise NameError()
        # change in database
        user = User.get_by(username=username)
        user.name = name
        user.save_or_update()
        session.commit()
        # notify
        self.user_shown_name_changed(username, name)
    
    @dbus.service.method('com.nublic.users', in_signature = 's', out_signature='')
    def delete_user(self, username):
        if ' ' in username or not self.user_exists(username):
            raise NameError()
        # passwd
        pexpect.run('userdel ' + username)
        print("Deleted in passwd")
        # samba
        pexpect.run('smbpasswd -x ' + username)
        print("Deleted in smbpasswd")
        # apache
        pexpect.run('htpasswd -D ' + username)
        print("Deleted in htpasswd")
        # delete in database
        user = User.get_by(username=username)
        name = user.name
        user.delete()
        session.commit()
        # Notify
        self.user_deleted(username, name)
