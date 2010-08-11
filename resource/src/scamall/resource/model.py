'''
Created on 10/08/2010

@author: David Navarro Estruch
'''

from elixir import UnicodeText, String, Integer, metadata
from elixir import Entity, Field, using_options
from elixir import ManyToOne
from elixir.relationships import OneToMany

metadata.bind = 'mysql://scamall_resource:ScamUp@localhost:3306/scamall_resource'
metadata.bind.echo = True

class App(Entity):
    '''
    Represents a App registered in resource
    '''
    name = Field(String(256), primary_key=True)
    using_options(tablename='app')


class Key(Entity):
    '''
    Represents a Key from an App 
    '''
    name = Field(String(256), primary_key=True)
    type_name = Field(String(256), primary_key=True)
    app = ManyToOne('App')
    values = OneToMany('Value')
    using_options(tablename='key')

    
class Value(Entity):
    '''
    Represents a value from a subkey in an App
    '''
    id = Field(Integer, primary_key=True)
    subkey = Field(String(256), primary_key=True)
    value = Field(UnicodeText)
    key = ManyToOne('Key')
    using_options(tablename='value')

