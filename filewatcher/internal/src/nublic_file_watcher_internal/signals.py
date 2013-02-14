'''
Created on 08/09/2011

@author: Alejandro Serrano Mena
@author: David Navarro Estruch
@copyright: 2011 Nublic
'''
import simplejson
import socket
import logging
log = logging.getLogger(__name__)


class Signaler(object):
    '''
    Sends signals
    '''
    def __init__(self):
        # Initialize sockets
        self.sockets = []

    def add_socket(self, socket):
        self.sockets.append(socket)

    def raise_event(self, ty, pathname, src_pathname, isdir):
        self.file_changed(ty, pathname, src_pathname, isdir)

    def file_changed(self, ty, pathname, src_pathname, isdir):
        o = {'ty': ty, 'pathname': unicode(pathname, 'utf8'),
             'src_pathname': unicode(src_pathname, 'utf8'),
             'isdir': bool(isdir)}
        o_string = simplejson.dumps(o)
        log.debug("File_changed %s. Sending info %s", o['pathname'], o_string)
        errors = []
        for sock in self.sockets:
            try:
                sock.write(o_string + '\n')
                sock.flush()
            except socket.error as e:
                log.exception("Error socket.error on socket write or flush: %s", repr(e))
                errors.append(sock)
            except BaseException as e:
                log.exception(
                    "Error UNEXPECTED on socket write or flush: %s", repr(e))
        # Remove sockets that give error on write (listener stopped)
        self.sockets = [s for s in self.sockets if s not in errors]
        log.debug("%s %s (src_pathname %s)[%s]",
                  ty, pathname, src_pathname, 'dir' if isdir else 'file')
