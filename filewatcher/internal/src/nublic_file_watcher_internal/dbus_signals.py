'''
Created on 08/09/2011

@author: Alejandro Serrano Mena
@copyright: 2011 Nublic
'''

# import dbus
# import dbus.service
import os
import os.path
import simplejson
import sys

def to_utf8(string):
    return unicode(string, 'utf-8')

def from_utf8(string):
    return string.encode('utf-8')

class DbusSignaler(): #dbus.service.Object):
    '''
    Sends signals
    '''
    def __init__(self):
        # Initialize D-Bus
        #bus_name = dbus.service.BusName('com.nublic.filewatcher', bus=dbus.SystemBus())
        #dbus.service.Object.__init__(self, bus_name, '/com/nublic/filewatcher/Filewatcher')
        # Initialize sockets
        self.sockets = []

    def add_socket(self, socket):
        self.sockets.append(socket)

    def raise_event(self, ty, pathname, src_pathname, isdir):
        self.file_changed(ty, pathname, src_pathname, isdir)

    #@dbus.service.signal(dbus_interface='com.nublic.filewatcher', signature='sssbs')
    def file_changed(self, ty, pathname, src_pathname, isdir):
        o = { 'ty': unicode(ty), 'pathname': unicode(pathname.decode('utf-8')), \
              'src_pathname': unicode(src_pathname.decode('utf-8')), \
              'isdir': bool(isdir) }
        o_string = simplejson.dumps(o)
        for socket in self.sockets:
            try:
                socket.write(o_string + '\n')
                socket.flush()
            except BaseException as e:
                sys.stderr.write("Error on socket write or flush: %s\n" % repr(e))
                pass
        try:
            sys.stderr.write("%s %s (src_pathname %s)[%s]\n" % (ty, pathname, src_pathname, 'dir' if isdir else 'file'))
        except:
            pass
