'''
Created on 14/08/2010

@author: Cesar Navarro Estruch, David Navarro Estruch
'''
from elixir import Entity, metadata, setup_all
from elixir.fields import Field
from sqlalchemy.types import Integer, UnicodeText, String, Boolean, Unicode, Text
from elixir.relationships import ManyToOne, OneToMany, OneToOne
from elixir.options import using_options

metadata.bind = 'mysql://scamall_notif:ScamUp@localhost:3306/scamall_notification'
metadata.bind.echo = True

NOTIFICATION_LEVEL_LOG = 'LOG'
NOTIFICATION_LEVEL_NOTICE = 'NOTICE'
NOTIFICATION_LEVEL_DEFAULT_PERFORMED = 'DEFAULT_PERFORMED'
NOTIFICATION_LEVEL_BLOCKER = 'BLOCKER'



class Notification(Entity):
    '''
    Represents a notification in the database
    '''
    id = Field(Integer, primary_key=True)
    level = Field(String(20))
    text = Field(UnicodeText)
    read = Field(Boolean, default=False)
    app = Field(String(256))
    user = Field(String(128))
    actions = OneToMany("Action")
    using_options(tablename='notification')

    def __init__(self):
        Entity.__init__(self)
        
class Action(Entity):
    '''
    Represents a Action in the database.
    
    An action is a way to interact with a notification to vary or perform
    something related. The way to represent the action is an url.
    
    @see 
    '''
    notification = ManyToOne('Notification', primary_key=True)
    label = Field(Unicode(30), primary_key=True)
    link = Field(Text)
    stock_action = ManyToOne("StockAction")
    using_options(tablename='action')

    def __init__(self):
        Entity.__init__(self)



class StockAction(Entity):
    '''
    Represents a Stock Action in the database.
    
    A default action with some modifications provided by Scamall.
    
    @see scamall.notification.model.Action 
    '''
    name = Field(String(128), primary_key=True)
    label = Field(Unicode(30))
    using_options(tablename='stock_action')

    def __init__(self):
        Entity.__init__(self)

