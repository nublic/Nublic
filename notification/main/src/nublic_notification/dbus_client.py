#!/usr/bin/python
'''
Created on 10/08/2010

Library for using Notifications on Python Applications

@author: David Navarro Estruch
@copyright: 2011 Nublic
'''

import dbus

class Notification(object):
    '''
    Represents a notification in the Notification System
    '''
    def __init__(self):
        self.id = self.level = self.text = \
            self.read = self.app = self.user = self.actions = None
    
    def send(self):
        bus = dbus.SystemBus()
        valueService = bus.get_object('com.nublic.notification', '/com/nublic/notification/Messages')
        message_sender = valueService.get_dbus_method('new_message','com.nublic.notification')
        return message_sender(self.app, self.user, self.level,  self.text)
        # To send actions and stockActions we will use dbus.Array(self.actions, dbus.Signature('a(ss)'))

