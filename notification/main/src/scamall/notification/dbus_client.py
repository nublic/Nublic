#!/usr/bin/python
'''
Created on 10/08/2010

It works for usage during the install of applications.

@author: David Navarro Estruch
'''

import sys
import logging
logging.basicConfig(stream=sys.stderr)
#logging.getLogger('sqlalchemy.engine').setLevel(logging.DEBUG) # TODO DEACTIVATE
logging.getLogger('sqlalchemy.engine').setLevel(logging.CRITICAL) # TODO ACTIVATE


from optparse import OptionParser

import dbus


def main():
    usage = "sends a message to the Scamall notification system"
    parser = OptionParser(usage)

    parser.add_option("-u", "--user", action="store", type="string")
    parser.add_option("-a", "--action", action="append", nargs=3, type="string") 
    parser.add_option("-s", "--stock", action="append", nargs=2, type="string")
    (options, args) = parser.parse_args()
    
    if len(args) != 3:
        parser.error("Invalid number of parameters")
    else:
        app = args[0]
        text = args[1]
        level = args[2]
        if options.user != None:
            user = options.user
        else:
            user = ""
        
        actions = []
        if options.action:
            for a in options.action:
                actions.append(a[0], a[1])
            
        stocks = []
        if options.stock:
            for s in options.stock:
                stocks.append(s[0], s[1]) 

        bus = dbus.SystemBus()
        notification = bus.get_object('com.scamall.notification', '/com/scamall/notification')
        sendResult = notification.get_dbus_method("send", 'com.scamall.notification')
        return sendResult(app, text, level, user, dbus.Array(actions, dbus.Signature('a(ss)')), dbus.Array(stocks, dbus.Signature('a(ss)')))

if __name__ == '__main__':
    main()
