from dbus_in_other_thread import call_expecting_return
from rpcbd_client import rpcbd_call_return

def is_package_installed(pkg, use_dbus=False):
    if use_dbus:
        return call_expecting_return('com.nublic.apt',
                                     '/com/nublic/Apt',
                                     'com.nublic.apt',
                                     lambda i: i.is_package_installed(pkg))
    else:
        return rpcbd_call_return(5442, lambda i: i.is_package_installed(pkg))
    
def install_package(pkg, use_dbus=False):
    if use_dbus:
        return call_expecting_return('com.nublic.apt',
                                     '/com/nublic/Apt',
                                     'com.nublic.apt',
                                     lambda i: i.install_package(pkg, timeout=300))
    else:
        return rpcbd_call_return(5442, lambda i: i.install_package(pkg, timeout=300))

def remove_package(pkg, use_dbus=False):
    if use_dbus:
        return call_expecting_return('com.nublic.apt',
                                     '/com/nublic/Apt',
                                     'com.nublic.apt',
                                     lambda i: i.remove_package(pkg, timeout=300))
    else:
        return rpcbd_call_return(5442, lambda i: i.remove_package(pkg, timeout=300))

def update_cache(use_dbus=False):
    if use_dbus:
        return call_expecting_return('com.nublic.apt',
                                     '/com/nublic/Apt',
                                     'com.nublic.apt',
                                     lambda i: i.update_cache())
    else:
        return rpcbd_call_return(5442, lambda i: i.update_cache())

def upgrade_system(use_dbus=False):
    if use_dbus:
        return call_expecting_return('com.nublic.apt',
                                     '/com/nublic/Apt',
                                     'com.nublic.apt',
                                     lambda i: i.upgrade_system())
    else:
        return rpcbd_call_return(5442, lambda i: i.upgrade_system())
