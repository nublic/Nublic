import apt
import apt.progress.base
# import dbus.service
from threading import (Lock)
from rpcbd import Handler
import logging
log = logging.getLogger(__name__)


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


class Apt(Handler):  # (dbus.service.Object, Handler):
    '''
    Small APT daemon for Nublic use
    '''
    assume_methods_block = False

    def __init__(self, connection):
        #bus_name = dbus.service.BusName('com.nublic.apt', bus=dbus.SystemBus())
        #dbus.service.Object.__init__(self, bus_name, '/com/nublic/Apt')
        super(Apt, self).__init__(connection)
        self.lock = Lock()

    #@dbus.service.method('com.nublic.apt', in_signature = 's', out_signature = 'b')
    def is_package_installed(self, package):
        log.info("is_package_installed for package %s: Locked", package)
        self.lock.acquire()
        try:
            cache = apt.Cache()
            pkg = cache[package]
            self.lock.release()
            log.info("Unlocked")
            return pkg.is_installed
        except BaseException:
            log.exception("Error checking if package %s is installed", package)
            self.lock.release()
            log.info("Unlocked")
            return False

    #@dbus.service.method('com.nublic.apt', in_signature = 's', out_signature = 'b')
    def install_package(self, package):
        log.info("install_package package %s: Locked", package)
        self.lock.acquire()
        try:
            cache = apt.Cache()
            pkg = cache[package]
            pkg.mark_install()
            acq_progress = AcquireProgress()
            ins_progress = InstallProgress()
            log.info("Installing %s...", package)
            cache.commit(acq_progress, ins_progress)
            self.lock.release()
            log.info("Unlocked")
            return not (acq_progress.has_any_error_ocurred() or ins_progress.has_any_error_ocurred())
        except BaseException:
            log.exception("Error installing package %s", package)
            self.lock.release()
            log.info("Unlocked")
            return False

    #@dbus.service.method('com.nublic.apt', in_signature = 's', out_signature = 'b')
    def remove_package(self, package):
        log.info("remove_package: Locked")
        self.lock.acquire()
        try:
            cache = apt.Cache()
            pkg = cache[package]
            pkg.mark_delete()
            acq_progress = AcquireProgress()
            ins_progress = InstallProgress()
            cache.commit(acq_progress, ins_progress)
            cache.open()
            log.info("Removing %s...", package)
            self.lock.release()
            log.info("Unlocked")
            return not (acq_progress.has_any_error_ocurred() or ins_progress.has_any_error_ocurred())
        except BaseException:
            log.exception("Exception in remove_package for package %s", package)
            self.lock.release()
            log.info("Unlocked")
            return False

    #@dbus.service.method('com.nublic.apt', in_signature = '', out_signature = 'b')
    def update_cache(self):
        log.info("update_cache: Locked\n")
        self.lock.acquire()
        try:
            cache = apt.Cache()
            acq_progress = AcquireProgress()
            cache.update(acq_progress)
            self.lock.release()
            log.info("Unlocked")
            return not acq_progress.has_any_error_ocurred()
        except BaseException:
            log.exception("Error in update_cache")
            self.lock.release()
            log.info("Unlocked")
            return False

    #@dbus.service.method('com.nublic.apt', in_signature = '', out_signature = 'b')
    def upgrade_system(self):
        log.info("upgrade_system locked")
        self.lock.acquire()
        try:
            cache = apt.Cache()
            cache.upgrade(True)
            acq_progress = AcquireProgress()
            ins_progress = InstallProgress()
            cache.commit(acq_progress, ins_progress)
            self.lock.release()
            log.info("Unlocked")
            return True
        except BaseException:
            log.exception("Error at upgrade_system")
            self.lock.release()
            log.info("Unlocked")
            return False
