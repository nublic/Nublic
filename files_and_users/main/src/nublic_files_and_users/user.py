import dbus.service
import os.path
import pexpect
import pwd
import spwd
import grp
import sys

from elixir import *
from model import *
from utf8 import *

# Needs a group 'nublic' in the system

# APACHE_PASSWD_FILE = "/var/nublic/conf/apache.passwd"
APACHE_PASSWD_FILE = "/var/nublic/secure/.htpasswd" # for debugging purposes
USER_SEPARATOR = ':'
DATA_ROOT = "/var/nublic/data"

class UserDBus(): #(dbus.service.Object):
    def __init__(self, loop = None, initialize_dbus=True):
        if initialize_dbus:
            bus_name = dbus.service.BusName('com.nublic.users', bus=dbus.SystemBus())
            dbus.service.Object.__init__(self, bus_name, '/com/nublic/Users')
    
    #@dbus.service.signal(dbus_interface='com.nublic.users', signature='ss')
    def user_created(self, username, name):
        print "User %s created" % username
    
    #@dbus.service.signal(dbus_interface='com.nublic.users', signature='ss')
    def user_deleted(self, username, name):
        print "User %s deleted" % username
    
    #@dbus.service.signal(dbus_interface='com.nublic.users', signature='ss')
    def user_shown_name_changed(self, username, name):
        print "User %s changed shown name to %s" % (username, name)
    
    #@dbus.service.method('com.nublic.users', in_signature = 's', out_signature = 'b')
    def user_exists(self, username):
        try:
            spwd.getspnam(from_utf8(username))
            return True
        except KeyError:
            return False
    
    #@dbus.service.method('com.nublic.users', in_signature = '', out_signature = 's')
    def get_all_users(self):
        ''' Gets all users separated by :'''
        r = ""
        for u in User.query.all():
            if r != "":
                r = r + ":"
            r = r + u.username
        return r
    
    #@dbus.service.method('com.nublic.users', in_signature = 's', out_signature = 'i')
    def get_user_uid(self, username):
        user = pwd.getpwnam(from_utf8(username))
        return user[2] # Corresponds to uid
    
    #@dbus.service.method('com.nublic.users', in_signature = 'sss', out_signature = '')
    def create_user(self, username_, password_, name_):
        # Convert to utf-8
        username = from_utf8(username_)
        password = from_utf8(password_)
        name = from_utf8(name_)
        # Do things
        if ' ' in username or self.user_exists(username) or USER_SEPARATOR in name:
            raise NameError()
        # passwd
        pexpect.run('useradd -s /usr/bin/lshell -m -G nublic -N ' + username)
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
        # ssh
        uid = self.get_user_uid(username)
        home_folder = '/home/' + username
        ssh_folder = home_folder + '/.ssh'
        os.mkdir(ssh_folder)
        os.chown(ssh_folder, uid, self.get_nublic_gid())
        os.chmod(ssh_folder, 0700)
        ssh_file = ssh_folder + '/authorized_keys'
        self.touch(ssh_file)
        os.chown(ssh_file, uid, self.get_nublic_gid())
        os.chmod(ssh_file, 0600)
        # database
        usr = User(username=username, uid=uid, name=name)
        session.add(usr)
        session.commit()
        # Notify
        self.user_created(username, name)
    
    def touch(self, fname, times = None):
        with file(fname, 'a'):
            os.utime(fname, times)
    
    #@dbus.service.method('com.nublic.users', in_signature = 'sss', out_signature = '')
    def change_user_password(self, username_, old_password_, new_password_):
        # Convert to utf-8
        username = from_utf8(username_)
        old_password = from_utf8(old_password_)
        new_password = from_utf8(new_password_)
        # Do things
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
    
    #@dbus.service.method('com.nublic.users', in_signature = 's', out_signature = 's')
    def get_user_shown_name(self, username_):
        # Convert to utf-8
        sys.stderr.write('Starting to get user shown name for ' + username_ + '\n')
        username = from_utf8(username_)
        # Do things
        if ' ' in username or not self.user_exists(username):
            raise NameError()
        # query in database
        sys.stderr.write('Starting to ask the database ' + username_ + '\n')
        user = User.get_by(username=username)
        return user.name
    
    #@dbus.service.method('com.nublic.users', in_signature = 'ss', out_signature = '')
    def change_user_shown_name(self, username_, name_):
        # Convert to utf-8
        username = from_utf8(username_)
        name = from_utf8(name_)
        # Do things
        if ' ' in username or not self.user_exists(username) or USER_SEPARATOR in name:
            raise NameError()
        # change in database
        user = User.get_by(username=username)
        user.name = name
        # user.save_or_update()
        session.commit()
        # notify
        self.user_shown_name_changed(username, name)
    
    #@dbus.service.method('com.nublic.users', in_signature = 's', out_signature = '')
    def delete_user(self, username_):
        # Convert to utf-8
        username = from_utf8(username_)
        # Do things
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
    
    def get_nublic_gid(self):
        nublic = grp.getgrnam('nublic')
        return nublic.gr_gid
    
    #@dbus.service.method('com.nublic.users', in_signature = 'ssb', out_signature = '')
    def assign_file(self, username_, path_, touch_after):
        # Convert to utf-8
        username = from_utf8(username_)
        path = from_utf8(path_)
        # Do things
        # Do not allow going to parent
        if path.find("..") != -1:
            raise NameError()
        # Get user id
        user = User.get_by(username=username)
        if user is None:
            raise NameError()
        # Make chown
        real_path = DATA_ROOT + path
        os.chown(real_path, user.uid, self.get_nublic_gid())
        pexpect.run('setfacl -m u:jetty:rwx "' + real_path + '"')
        if (touch_after):
            pexpect.run('sudo -u ' + username + ' touch "' + real_path + '"')

    #@dbus.service.method('com.nublic.users', in_signature = 'ss', out_signature = '')
    def add_public_key(self, username_, key):
        # Convert to utf-8
        username = from_utf8(username_)
        # Get user id
        user = User.get_by(username=username)
        if user is None:
            raise NameError()
        # Make chown
        ssh_file = '/home/' + username + '/.ssh/authorized_keys'
        f = open(ssh_file, 'a')
        f.write(key + '\n')
        f.close()
        os.chmod(ssh_file, 0600)
