'''
Created on 10/08/2010

@author: David Navarro Estruch
@copyright: 2011 Nublic
'''
from elixir import setup_all, session

from .provider import Provider
from model import App, Key, Value

class DatabaseStored(Provider):
    '''
    Provides a class for Providers who wants to save their values in a database.
    A new Provider must inherit this class and provide a request custom method.
    '''
    
    def __init__(self, type):
        '''
        Constructor
        
        @see:  nublic.resource.Provider.__init__
        '''
        Provider.__init__(self, type)
        setup_all()

    def value(self, app, key, subkey = None):
        '''
        Provides the values stored in the database.
        If you want to perform something else just override
        this function.
        
        @see: nublic.resource.provider.Provider
        @raise TypeProviderError
        @raise IntegrityError and other SQLAlchemyErrors
        '''
        value = self.get_value(app, key, subkey)
        if value == None:
            raise NotExistingSubkeyError(subkey)
        else:
            return value.value
        
    def save_value(self, app_id, key, subkey, value):
        '''
        Saves a value in the database.
        Creates the needed references of key and app if needed.
        
        @param app_id: string The identification of the requesting app
        @param key: string The key that will identify all given values
        @param subkey: string The subkey that identifies this specific value
        @param value: string Value to store
        
        @raise TypeProviderError: Notifies that the given key matches with another ResourceType
        '''
        app = App.get_by(name=app_id)
        if app == None:
            app = App(name=app_id)
        key_db = Key.get_by(name=key,app_name=app_id)
        if key_db == None:
            key_db = Key(name=key,app=app, type_name=self.type)
        if key_db.type_name != self.type:
            raise TypeProviderError(self.type, key_db.type_name)
            
        value_db = Value()
        value_db.key = key_db
        value_db.subkey = subkey
        value_db.value = value
        session.commit()
        
    def remove_key(self, app_id, key):
        '''
        Removes a key and all their values stored in the database.
        
        @param app_id: string The identification of the requesting app_id
        @param key: string The key that is going to be erased
        '''
        key_db = Key.get_by(name = key, app_name = app_id)
        for value in key_db.values:
            value.delete()
        key_db.delete()
        session.commit()

    def get_value(self, app, key, subkey):
        '''
        Gets a model.Value object from the database.
        
        @param app: string App id
        @param key: string Key name
        @param subkey: string Subkey id
        @return: model.Value
        '''
        q = Value.query.filter_by(subkey=subkey, key_name=key)
        q = q.filter(Value.key.has(app_name=app))
        value = q.first()
        return value

    def get_key(self, app, key):
        '''
        Gets a model.Key object from the database.
        
        @param app: string App id
        @param key: string Key name
        @return: model.Key
        '''
        return Key.get_by(app_name = app, name = key)


class TypeProviderError(Exception):
    def __init__(self, type, storedType):
        self.type = type
        self.storedType = storedType
        
    def __repr__(self):
        return "Type provided " + self.type + \
               "but in the database is stored " + self.storedType

class ExistingKeyError(Exception):
    def __init__(self, key):
        self.key = key
        
class NotExistingKeyError(Exception):
    def __init__(self, key):
        self.key = key
        
class NotExistingSubkeyError(Exception):
    def __init__(self, subkey):
        self.subkey = subkey
