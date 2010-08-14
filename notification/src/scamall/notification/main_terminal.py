#!/usr/bin/python

'''
Created on 15/08/2010

@author: Cesar Navarro Estruch
'''
from optparse import OptionParser
from scamall.notification.model import Notification, Action, StockAction

def main():
    usage = "sends a message to the Scamall notification system"
    parser = OptionParser(usage)
    
    parser.add_option("-a", "--app", action="store", type="string")
    parser.add_option("-u", "--user", action="store", type="string")
    parser.add_option("-l", "--level", action="store", type="string")
    parser.add_option("-t", "--text", action="store", type="string")
    parser.add_option("-c", "--action", action="append", nargs=3, type="string") # 
    parser.add_option("-s", "--stock", action="append", type="string")
    (options, args) = parser.parse_args()

    print options
    print args

    parser.error("Ey, falta ordenes")

if __name__ == '__main__':
    main()