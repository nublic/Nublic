import pexpect
import spwd
import os.path

# Needs a group 'nublic' in the system

# APACHE_PASSWD_FILE = "/var/nublic/conf/apache.passwd"
APACHE_PASSWD_FILE = "apache.passwd" # for debugging purposes

def user_exists(username):
    try:
        spwd.getspnam(username)
        return True
    except KeyError:
        return False

def create_user(username, password):
    if ' ' in username or user_exists(username):
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

def change_user_password(username, old_password, new_password):
    if ' ' in username or not user_exists(username):
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

def delete_user(username):
    if ' ' in username or not user_exists(username):
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

if __name__ == '__main__':
    create_user('ejemploso', 'zasio')
    change_user_password('ejemploso', 'zasio', 'zadop')
    delete_user('ejemploso')
