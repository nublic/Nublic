#!/usr/bin/python

import fstab

ftab = fstab.Fstab()
ftab.read('/etc/fstab')
for line in ftab.lines:
    if line.has_filesystem():
        if line.directory == '/':
            options = line.get_options()
            options.append('acl')
            line.set_options(options)
ftab.write('/etc/fstab')
print('fstab file changed successfully')

