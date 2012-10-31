import apt
import apt.progress.base
import dbus.service
import sys
from threading import (Lock)
from rpcbd import Handler

class AcquireProgress(apt.progress.base.AcquireProgress):
    def __init__(self):
        apt.progress.base.AcquireProgress.__init__(self)
        self.some_error = False

    def error(self, item):
        apt.progress.AcquireProgress.error(self, item)
        self.some_error = True

    def has_any_error_ocurred(self):
        return self.some_error

class InstallProgress(apt.progress.base.InstallProgress):
    def __init__(self):
        apt.progress.base.InstallProgress.__init__(self)
        self.some_error = False

    def error(self, pkg, errormsg):
        apt.progress.InstallProgress.error(self, pkg, errormsg)
        self.some_error = True

    def has_any_error_ocurred(self):
        return self.some_error

class Apt(dbus.service.Object, Handler):
    '''
    Small APT daemon for Nublic use
    '''
    assume_methods_block=False
    
    def __init__(self, loop = None):
        bus_name = dbus.service.BusName('com.nublic.apt', bus=dbus.SystemBus())
        dbus.service.Object.__init__(self, bus_name, '/com/nublic/Apt')
        self.lock = Lock()

    @dbus.service.method('com.nublic.apt', in_signature = 's', out_signature = 'b')
    def is_package_installed(self, package):
        sys.stderr.write("is_package_installed: Locked\n")
        self.lock.acquire()
        try:
            cache = apt.Cache()
            pkg = cache[package]
            self.lock.release()
            sys.stderr.write("Unlocked\n")
            return pkg.is_installed
        except BaseException as e:
            sys.stderr.write("Error: " + str(e) + '\n')
            self.lock.release()
            sys.stderr.write("Unlocked\n")
            return False

    @dbus.service.method('com.nublic.apt', in_signature = 's', out_signature = 'b')
    def install_package(self, package):
        sys.stderr.write("install_package: Locked\n")
        self.lock.acquire()
        try:
            cache = apt.Cache()
            pkg = cache[package]
            pkg.mark_install()
            acq_progress = AcquireProgress()
            ins_progress = InstallProgress()
            sys.stderr.write("Installing " + package + "...\n")
            cache.commit(acq_progress, ins_progress)
            self.lock.release()
            sys.stderr.write("Unlocked\n")
            return not (acq_progress.has_any_error_ocurred() or ins_progress.has_any_error_ocurred())
        except BaseException as e:
            sys.stderr.write("Error " + str(e) + '\n')
            self.lock.release()
            sys.stderr.write("Unlocked\n")
            return False

    @dbus.service.method('com.nublic.apt', in_signature = 's', out_signature = 'b')
    def remove_package(self, package):
        sys.stderr.write("remove_package: Locked\n")
        self.lock.acquire()
        try:
            cache = apt.Cache()
            pkg = cache[package]
            pkg.mark_delete()
            acq_progress = AcquireProgress()
            ins_progress = InstallProgress()
            cache.commit(acq_progress, ins_progress)
            cache.open()
            sys.stderr.write("Removing " + package + "...\n")
            self.lock.release()
            sys.stderr.write("Unlocked\n")
            return not (acq_progress.has_any_error_ocurred() or ins_progress.has_any_error_ocurred())
        except BaseException as e:
            sys.stderr.write("Error: " + str(e) + '\n')
            self.lock.release()
            sys.stderr.write("Unlocked\n")
            return False

    @dbus.service.method('com.nublic.apt', in_signature = '', out_signature = 'b')
    def update_cache(self):
        sys.stderr.write("update_cache: Locked\n")
        self.lock.acquire()
        try:
            cache = apt.Cache()
            acq_progress = AcquireProgress()
            cache.update(acq_progress)
            self.lock.release()
            sys.stderr.write("Unlocked\n")
            return not acq_progress.has_any_error_ocurred()
        except BaseException as e:
            sys.stderr.write("Error: " + str(e) + '\n')
            self.lock.release()
            sys.stderr.write("Unlocked\n")
            return False

    @dbus.service.method('com.nublic.apt', in_signature = '', out_signature = 'b')
    def upgrade_system(self):
        sys.stderr.write("upgrade_system: Locked\n")
        self.lock.acquire()
        try:
            cache = apt.Cache()
            cache.upgrade(True)
            acq_progress = AcquireProgress()
            ins_progress = InstallProgress()
            cache.commit(acq_progress, ins_progress)
            self.lock.release()
            sys.stderr.write("Unlocked\n")
            return True
        except BaseException as e:
            sys.stderr.write("Error: " + str(e) + '\n')
            self.lock.release()
            sys.stderr.write("Unlocked\n")
            return False
    
