
from dbus_in_other_thread import call_expecting_return

def is_package_installed(pkg):
    return call_expecting_return('com.nublic.apt',
                                 '/com/nublic/Apt',
                                 'com.nublic.apt',
                                 lambda i: i.is_package_installed(pkg))
    
def install_package(pkg):
    return call_expecting_return('com.nublic.apt',
                                 '/com/nublic/Apt',
                                 'com.nublic.apt',
                                 lambda i: i.install_package(pkg))

def remove_package(pkg):
    return call_expecting_return('com.nublic.apt',
                                 '/com/nublic/Apt',
                                 'com.nublic.apt',
                                 lambda i: i.remove_package(pkg))

def update_cache():
    return call_expecting_return('com.nublic.apt',
                                 '/com/nublic/Apt',
                                 'com.nublic.apt',
                                 lambda i: i.update_cache())

def upgrade_system():
    return call_expecting_return('com.nublic.apt',
                                 '/com/nublic/Apt',
                                 'com.nublic.apt',
                                 lambda i: i.upgrade_system())
