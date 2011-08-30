#!/usr/bin/python
'''
Created on 15/08/2010
@copyright: 2011 Nublic
@author: David Navarro Estruch
'''
from elixir import *
from optparse import OptionParser
from model import Notification, Action, StockAction


def new_message(app, user, level, text):
    newNotif = Notification()
    newNotif.app = app
    newNotif.user = user
    newNotif.level = level
    newNotif.text = text
    #newNotif.actions = actions
    #newNotif.save()
    session.commit()
    return newNotif
    

