#!/usr/bin/python

'''
Created on 18/07/2011

@author: David Navarro Estruch
@copyright: 2011 Nublic
'''
#import gtk
#import dbus
#import dbus.service
#import dbus.exceptions
#import gobject
#from dbus.mainloop.glib import DBusGMainLoop

import logging
from rpcbd import Handler, ThreadedTCPJsonRpcPeer, JSONRPC_V2
from elixir import metadata, setup_all
#import dbus
import time
from time import sleep
#import signal
import sys

from nublic.resource import App
import notification


def __get_bind_uri():
    return App("nublic_notification").get("db").value("uri")


def __check_nublic_resource_is_on():
    WAITING_MAX = 200
    waited = 0
    should_exit = False
    while waited < WAITING_MAX and not should_exit:
        # Just do the thing below, but waiting for errors
        try:
            __get_bind_uri()
            should_exit = True
        except:
            time.sleep(2)
            waited = waited + 1


def initial_program_setup():
    pass


def do_main_program():
    # dbus_loop = DBusGMainLoop(set_as_default=True)
    __check_nublic_resource_is_on()
    metadata.bind = __get_bind_uri()
    setup_all(create_tables=True)
    sys.stderr.write("Notification daemon database setup\n")

    # Initialize JSON-RPC
    logging.basicConfig(level=logging.WARNING)
    peer = ThreadedTCPJsonRpcPeer(
        JSONRPC_V2, default_handler=JsonRpcNotification)
    peer.listen_tcp(port=5441)
    sys.stderr.write("Notification daemon listening\n")

    try:
        while True:
            sleep(1)
    except KeyboardInterrupt:
        peer.shutdown()
    sys.stderr.write("Notification daemon shutdown\n")

    # Initialize D-Bus
    #dbus_value = DBusValue(loop = dbus_loop)
    #loop = gobject.MainLoop()
    #gobject.threads_init()
    #loop.run()


def program_cleanup():
    pass


def reload_program_config():
    pass

# class DBusValue(dbus.service.Object):
#     bus_path = 'com.nublic.notification'
#     _base_object_path = '/com/nublic/notification'

#     def __init__(self, loop = None):
#         # Init DBus object
#         self.object_path = "/com/nublic/notification/Messages"
#         bus_name = dbus.service.BusName('com.nublic.notification', bus = dbus.SystemBus(mainloop = loop))
#         dbus.service.Object.__init__(self, bus_name, object_path = self.object_path)

#     @dbus.service.method('com.nublic.notification', in_signature = 'ssss', out_signature='s')
#     def new_message(self, app, user, level, text):
#         return unicode(notification.new_message(app, user, level, text))


class JsonRpcNotification(Handler):
    assume_methods_block = False

    def new_message(self, app, user, level, text):
        return unicode(notification.new_message(app, user, level, text))
