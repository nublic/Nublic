'''
Created on 10/08/2010

@author: David Navarro Estruch
@copyright: 2011 Nublic
'''

class Provider(object):
    '''
    classdocs
    '''


    def __init__(self, type):
        '''
        Constructor
        '''
        self.type = type

    def install(self, app, key, *args):
        '''
        Must perform a install over the given app, key.
        Optionally can have a number of ordered args.
        
        @param app: string The AppId
        @param key: string The wanted key to identify in the future this resource
        @param args: list Optional args
        
        Raises RepeatedKeyError
        '''
        raise NotImplementedError()
    
    def uninstall(self):
        '''
        Must perform a uninstall over the given app, key.
        Optionally can have a number of ordered args.
        
        @param app: string The AppId
        @param key: string The wanted key to uninstall
        @param args: list Optional args
        
        @raise NotExistingKeyError
        '''
        raise NotImplementedError()

    def request(self, app, key, *args):
        '''
        Must perform a request over the given app, key.
        Optionally can have a number of ordered args.
        
        @param app: string The AppId
        @param key: string The wanted key to identify in the future this request
        @param args: list Optional args
        
        Raises RepeatedKeyError
        '''
        raise NotImplementedError()
    
    
    def release(self, app, key):
        '''
        Must perform a release over the given app, key.
        Optionally can have a number of ordered args.
        
        @param app: string The AppId
        @param key: string The wanted key to release
        @param args: list Optional args
        
        @raise NotExistingKeyError
        '''
        raise NotImplementedError()

        
    def value(self, app, key, subkey = None):
        '''
        Returns the value of the given pair key/subkey.
        
        @param app: string The AppId
        @param key: string The key used to Request
        @param subkey string The subkey wanted. If None return the None subkey.
        
        @return: string value asked
        @raise NotExistingKeyError: Contains the key or subkey that does not exist.
        '''
        raise NotImplementedError()

    def available_values(self, app, key):
        '''
        Returns a list of the available subkeys.

        @param app: string The AppId
        @param key: string The key used to Request
        
        @return: List of string with the available subkeys
        '''
        raise NotImplementedError()
            
class ReapeatedKeyError(Exception):
    def __init__(self, key):
        self.key = key
class NotExistingKeyError(Exception):
    def __init__(self, key):
        self.key = key
