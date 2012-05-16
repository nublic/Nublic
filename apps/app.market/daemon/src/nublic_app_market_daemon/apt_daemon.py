import apt
import apt.progress.base
import dbus.service
from threading import (Lock)

class AcquireProgress(apt.progress.base.AcquireProgress):
    def __init__(self):
        apt.progress.AcquireProgress.__init__(self)
        self.some_error = False

    def error(self, item):
        apt.progress.AcquireProgress.error(self, item)
        self.some_error = True

    def has_any_error_ocurred(self):
        return self.some_error

class InstallProgress(apt.progress.base.InstallProgress):
    def __init__(self):
        apt.progress.InstallProgress.__init__(self)
        self.some_error = False

    def error(self, pkg, errormsg):
        apt.progress.InstallProgress.error(self, pkg, errormsg)
        self.some_error = True

    def has_any_error_ocurred(self):
        return self.some_error

class Apt(dbus.service.Object):
    '''
    Small APT daemon for Nublic use
    '''
    def __init__(self, loop = None):
        bus_name = dbus.service.BusName('com.nublic.apt', bus=dbus.SystemBus())
        dbus.service.Object.__init__(self, bus_name, '/com/nublic/Apt')
        self.lock = Lock()

    @dbus.service.method('com.nublic.apt', in_signature = 's', out_signature = 'b')
    def is_package_installed(self, package):
        self.lock.acquire()
        try:
            cache = apt.Cache()
            pkg = cache[package]
            self.lock.release()
            return pkg.is_installed
        except:
            self.lock.release()
            return False

    @dbus.service.method('com.nublic.apt', in_signature = 's', out_signature = 'b')
    def install_package(self, package):
        self.lock.acquire()
        try:
            cache = apt.Cache()
            pkg = cache[package]
            pkg.mark_install()
            acq_progress = AcquireProgress()
            ins_progress = InstallProgress()
            pkg.commit(acq_progress, ins_progress)
            self.lock.release()
            return not (acq_progress.has_any_error_ocurred() or ins_progress.has_any_error_ocurred())
        except:
            self.lock.release()
            return False

    @dbus.service.method('com.nublic.apt', in_signature = 's', out_signature = 'b')
    def remove_package(self, package):
        self.lock.acquire()
        try:
            cache = apt.Cache()
            pkg = cache[package]
            pkg.mark_delete()
            acq_progress = AcquireProgress()
            ins_progress = InstallProgress()
            pkg.commit(acq_progress, ins_progress)
            self.lock.release()
            return not (acq_progress.has_any_error_ocurred() or ins_progress.has_any_error_ocurred())
        except:
            self.lock.release()
            return False

    @dbus.service.method('com.nublic.apt', in_signature = '', out_signature = 'b')
    def update_cache(self):
        self.lock.acquire()
        try:
            cache = apt.Cache()
            acq_progress = AcquireProgress()
            cache.update(acq_progress)
            self.lock.release()
            return not acq_progress.has_any_error_ocurred()
        except:
            self.lock.release()
            return False

    @dbus.service.method('com.nublic.apt', in_signature = '', out_signature = 'b')
    def upgrade_system(self):
        self.lock.acquire()
        try:
            cache = apt.Cache()
            cache.upgrade(True)
            self.lock.release()
            return True
        except:
            self.lock.release()
            return False
    
