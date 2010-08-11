'''
Created on 11/08/2010

@author: David Navarro Estruch
'''
from .database_stored import DatabaseStored

class MysqlDB(DatabaseStored):
    '''
    classdocs
    '''


    def __init__(self):
        '''
        Constructor
        '''
        DatabaseStored.__init__("mysql-db")
        