#!/usr/bin/python

'''
Created on 15/08/2010

@author: Cesar Navarro Estruch, David Navarro Estruch
@copyright: 2011 Nublic
'''
from elixir import *
from optparse import OptionParser
from nublic_notification.model import Notification, Action, StockAction
from nublic_notification.notification import new_message 

def main():
    usage = "sends a message to the Nublic notification system"
    parser = OptionParser(usage)

    parser.add_option("-a", "--app", action="store", type="string")
    parser.add_option("-u", "--user", action="store", type="string")
    parser.add_option("-l", "--level", action="store", type="string")
    parser.add_option("-t", "--text", action="store", type="string")
    parser.add_option("-c", "--action", action="append", nargs=3, type="string") # 
    parser.add_option("-s", "--stock", action="append", type="string")
    (options, args) = parser.parse_args()
    
    setup_all()

    opt_actions = options.action
    actions = []
    for opt in opt_actions:
        a = Action()
        a.label, a.link, a.description = opt
        actions.append(a)

    stocks = options.stock
      
    new_message(options.app, options.user, options.level, options.text, actions);
   
if __name__ == '__main__':
    main()
