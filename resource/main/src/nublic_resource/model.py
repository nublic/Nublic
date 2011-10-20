'''
Created on 10/08/2010

@author: David Navarro Estruch
@copyright: 2011 Nublic

'''

from elixir import UnicodeText, String, Integer, metadata
from elixir import Entity, Field, using_options
from elixir import ManyToOne
from elixir.relationships import OneToMany
from sqlalchemy.types import Unicode
import ConfigParser

# Load configuration file for user and password
__config = ConfigParser.RawConfigParser()
__config.read('/etc/nublic/resource.conf')
postgres_root_password = __config.get('DB_ACCESS','NUBLIC_RESOURCE_PASS').strip("'")
postgres_root_user = __config.get('DB_ACCESS','NUBLIC_RESOURCE_USER').strip("'")

metadata.bind = "postgresql://"+postgres_root_user+":"+postgres_root_password+'@localhost/nublic_resource'
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
    name = Field(Unicode(256), primary_key=True)
    type_name = Field(Unicode(256), primary_key=True)
    app = ManyToOne('App')
    values = OneToMany('Value')
    using_options(tablename='key')
    
    def __repr__(self):
        return "Key <"+self.name+"> of type <"+self.type_name+\
                 "> of app <"+self.app.name+">\n"
class Value(Entity):
    '''
    Represents a value from a subkey in an App
    '''
    id = Field(Integer, primary_key=True)
    subkey = Field(Unicode(256))
    value = Field(UnicodeText)
    key = ManyToOne('Key')
    using_options(tablename='value')

    def get_value(self):
        return "" + self.value
    def set_value(self, value):
        self.value = value

    def __repr__(self):
        return "key: " + self.key.name + "subkey: " + self.subkey \
                + "value: " + self.value

def key_get_all():
    return Key.query.all()
