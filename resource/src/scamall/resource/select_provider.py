'''
Created on 11/08/2010

@author: David Navarro Estruch
'''
from scamall.resource.database_stored import DatabaseStored
from scamall.resource.mysql_db import MysqlDB

class SelectProvider(object):
    '''
    Selects the correct provider for any source 
    '''

    def __init__(self):
        '''
        Constructor
        '''
        pass
        
    def get_provider(self, app, key):
        '''
        Generates a Provider from an app key
        '''
        type = self.get_provider_name(app, key)
        return self.generate_provider(type)

    def get_provider_name(self, app, key):
        '''
        Gets the Provider id from an app key
        '''
        db = DatabaseStored(app, key)
        key_stored = db.get_key(app,key)
        return key_stored.type_name

    def generate_provider(self, type_name):
        '''
        Generates a Provider given his type id
        '''
        if type_name == 'mysql-db':
            return MysqlDB()
        else:
            return None