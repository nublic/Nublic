'''
Created on 11/08/2010

@author: David Navarro Estruch
@copyright: 2011 Nublic
'''
import string
from sqlalchemy.sql.expression import text
from random import Random

from .database_stored import DatabaseStored
from .model import metadata
from nublic_resource.database_stored import ExistingKeyError,\
    NotExistingKeyError

class MysqlDB(DatabaseStored):
    '''
    Resource class that provides a database access to mysql.
    Gives a user with access to a empty database.
    
    Subkeys:
        * user: Username
        * password: Password
        * database: Database_name
        * port: Returns the connection port
        * uri: Returns the connection uri
    '''

    __lenght_of_database_sufix = 16
    __lenght_of_user_name = 14 # Max of 16 in database
    __lenght_of_password = 32
    __random_characters = string.letters + string.digits + ""
    __root_password = 'Solutions'
    __root_user = 'root'
    __port = '3306'
    __connection_protocol = "mysql"

    def __init__(self):
        DatabaseStored.__init__(self, "mysql-db")
        # @todo: Needs to read the root password and root user from somewhere

    def install(self, app, key):
        if self.__is_key(app, key):
            raise ExistingKeyError(key)
        database_name, user_name, password = \
                    self.__create_mysql_resource(app, key)
        self.save_value(app, key, 'user', user_name)
        self.save_value(app, key, 'pass', password)
        self.save_value(app, key, 'database', database_name)
                    
    def uninstall(self, app, key):
        if not self.__is_key(app, key):
            raise NotExistingKeyError(key) 
        user_name = self.value(app, key, 'user')
        database_name = self.value(app, key, 'database')
        self.remove_key(app, key)
        self.__delete_mysql_resource(database_name, user_name)
        
    def value(self, app, key, subkey = None):
        '''
        Provides the values for the given app key/subkey.
        
        Some values are shared to every mysql-db resource.
        '''
        if subkey == "port":
            return self.__port
        elif subkey == "uri":
            user = self.value(app, key, "user")
            password = self.value(app, key, "pass")
            database = self.value(app, key, "database")
            return self.__generate_connection_uri(user, password, database)
        else:
            return DatabaseStored.value(self, app, key, subkey)

    def available_values(self, app, key):
        return ['database', 'pass', 'port', 'uri', 'user']

    def __is_key(self, app, key):
        return not (self.get_key(app, key) == None)

    def __create_random_data(self, app, key):
        '''
        Creates a tuple with database, user, password
        with randomized data
        '''
        randGen = Random()
        database_name = "".join(randGen.sample(self.__random_characters,
                                        self.__lenght_of_database_sufix))
        user_name = "".join(randGen.sample(self.__random_characters,
                                    self.__lenght_of_user_name))
        password = "".join(randGen.sample(self.__random_characters,
                                  self.__lenght_of_password))
        return database_name, user_name, password

    def __generate_connection_uri(self, user, password, database = None):
        '''
        Returns a conection uri with database 
        '''
        if database == None:
            return self.__connection_protocol + "://" + user + ":" + password \
                    + "@localhost:" + self.__port
        else:
            return self.__connection_protocol + "://" + user + ":" + password \
                    + "@localhost:" + self.__port + "/" + database
        
    def __create_mysql_resource(self, app, key):
        bind = metadata.bind
        # Creates a connection with permission to create users and databases
        metadata.bind = self.__generate_connection_uri(self.__root_user, self.__root_password)
        # Generate data and queries
        database_name, user_name, password = self.__create_random_data(app, key)
        sql_create_database = "CREATE DATABASE `:database`;"
        sql_create_user = "CREATE USER :user@localhost IDENTIFIED BY :password"
        sql_revoke_permissions = "REVOKE ALL ON *.* FROM :user@localhost"
        sql_grant_permissions = "GRANT ALL ON `:database`.* TO :user@localhost"
        # Perform the queries
        text(sql_create_database, metadata.bind).execute(database = database_name)
        text(sql_create_user, metadata.bind).execute(user = user_name, password = password)
        text(sql_revoke_permissions, metadata.bind).execute(user = user_name)
        text(sql_grant_permissions, metadata.bind).execute(database = database_name, user = user_name)
        # Restores the old database
        metadata.bind = bind
        return database_name, user_name, password

    def __delete_mysql_resource(self, database_name, user_name):
        bind = metadata.bind
        # Creates a connection with permission to create users and databases
        metadata.bind = self.__generate_connection_uri(self.__root_user, self.__root_password)
        # Generate data and queries
        sql_delete_database = "DROP DATABASE `:database`;"
        sql_delete_user = "DROP USER :user@localhost"
        # Perform the queries
        text(sql_delete_database, metadata.bind).execute(database = database_name)
        text(sql_delete_user, metadata.bind).execute(user = user_name)
        # Restores the old database_name
        metadata.bind = bind

