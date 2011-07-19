# Initial version from: http://www.python.org/dev/peps/pep-3143/ from Public Domain
# @author: David Navarro Estruch
# @copyright: Nublic

import os
import grp
import signal
import daemon
import lockfile

from notification import (
    initial_program_setup,
    do_main_program,
    program_cleanup,
    reload_program_config,
    )

context = daemon.DaemonContext(
    #working_directory='/var/lib/foo',
    umask=0o002,
    pidfile=lockfile.FileLock('/var/run/scamall_notification.pid'),
    )

context.signal_map = {
    signal.SIGTERM: program_cleanup,
    signal.SIGHUP: 'terminate',
    signal.SIGUSR1: reload_program_config,
    }

#mail_gid = grp.getgrnam('mail').gr_gid
#context.gid = mail_gid

#important_file = open('spam.data', 'w')
#interesting_file = open('eggs.data', 'w')
#context.files_preserve = [important_file, interesting_file]

initial_program_setup()

with context:
    do_main_program()

with daemon.DaemonContext():
    do_main_program()