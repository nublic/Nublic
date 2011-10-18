'''
Collection of functions and classes that a selects the correct provider 
for any source 
Created on 11/08/2010

@author: David Navarro Estruch
@copyright: 2011 Nublic
'''
from database_stored import DatabaseStored
from mysql_db import MysqlDB
from postgresql_db import PostgresqlDB

def get_provider(app, key):
    '''
    Generates a Provider from an app key
    '''
    resource_type = get_provider_name(app, key)
    return generate_provider(resource_type)

def get_provider_name(app, key):
    '''
    Gets the Provider id from an app key
    '''
    database = DatabaseStored(app)
    key_stored = database.get_key(app, key)
    if key_stored == None:
        raise NotExistingProviderError()
    return key_stored.type_name

def generate_provider(type_name):
    '''
    Generates a Provider given his type id
    '''
    if type_name == 'mysql-db':
        return MysqlDB()
    elif type_name == "postgresql-db":
        return PostgresqlDB()
    else:
        return None

def get_all_providers():
    '''
    Gets a dictionary with all apps containing a list of all keys.
    
    '''
    pass

class NotExistingProviderError(Exception):
    pass