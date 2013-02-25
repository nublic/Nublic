'''
Created on 11/08/2010

@author: David Navarro Estruch
@copyright: 2011 Nublic
'''
import string
from sqlalchemy.sql.expression import text
from sqlalchemy import create_engine
from random import Random

from database_stored import DatabaseStored
from model import metadata
import model
from database_stored import (ExistingKeyError,
                             NotExistingKeyError)


class PostgresqlDB(DatabaseStored):
    '''
    Resource class that provides a database access to postgresql.
    Gives a user with access to a empty database.

    Subkeys:
        * user: Username
        * password: Password
        * database: Database_name
        * port: Returns the connection port
        * uri: Returns the connection uri
    '''
    __lenght_of_database_sufix = 16
    __lenght_of_user_name = 14  # Max of 16 in database
    __lenght_of_password = 32
    __random_characters = string.ascii_letters + string.digits + ""
    __random_lowercase = string.ascii_lowercase + string.digits + ""
    __root_password = model.postgres_root_password
    __root_user = model.postgres_root_user
    __connection_protocol = "postgresql"
    __connection_port = "5432"
    providerType = "postgresql-db"

    def __init__(self):
        DatabaseStored.__init__(self, "postgresql-db")
        self._uri = None

    def install(self, app, key):
        if self.__is_key(app, key):
            raise ExistingKeyError(key)
        database_name, user_name, password = \
            self.__create_postgre_resource(app, key)
        self.save_value(app, key, 'user', user_name)
        self.save_value(app, key, 'pass', password)
        self.save_value(app, key, 'database', database_name)
        self._uri = self.__generate_connection_uri(user_name, password,
                                                   database_name)
        '''self.save_value(app, key, 'uri',
                        self.__generate_connection_uri(user_name, password,
                                                       database_name))'''

    def uninstall(self, app, key):
        if not self.__is_key(app, key):
            raise NotExistingKeyError(key)
        user_name = self.value(app, key, 'user')
        database_name = self.value(app, key, 'database')
        self.remove_key(app, key)
        self.__delete_mysql_resource(database_name, user_name)

    def request(self, app, key):
        pass

    def release(self, app, key):
        pass

    def value(self, app, key, subkey=None):
        '''
        Provides the values for the given app key/subkey.

        Some values are shared to every postgresql-db resource.
        '''
        if subkey == "uri":
            if self._uri:
                return self._uri
            else:
                user = self.value(app, key, "user")
                password = self.value(app, key, "pass")
                database = self.value(app, key, "database")
                return self.__generate_connection_uri(user, password, database)
        else:
            return DatabaseStored.value(self, app, key, subkey)

    def available_values(self, app, key):
        return ['database', 'pass', 'port', 'uri', 'user']

    def __is_key(self, app, key):
        return not (self.get_key(app, key) is None)

    def __create_random_data(self):
        '''
        Creates a tuple with database, user, password
        with randomized data
        '''
        rand_gen = Random()
        database_name = "".join(rand_gen.sample(string.ascii_lowercase, 1) +
                                rand_gen.sample(self.__random_lowercase,
                                                self.__lenght_of_database_sufix - 1))
        user_name = "".join(rand_gen.sample(string.ascii_lowercase, 1) +
                            rand_gen.sample(self.__random_lowercase,
                                            self.__lenght_of_user_name - 1))
        password = "".join(rand_gen.sample(self.__random_characters,
                                           self.__lenght_of_password)).lower()
        return database_name, user_name, password

    def __generate_connection_uri(self, user, password, database=None):
        '''
        Returns a connection uri with database
        '''
        if database is None:
            return (self.__connection_protocol + "://" + user + ":" + password
                    + "@localhost:" + self.__connection_port)
        else:
            return (self.__connection_protocol + "://" + user + ":" + password
                    + "@localhost:" + self.__connection_port + "/" + database)

    def __create_postgre_resource(self, app, key):
        engine = create_engine(self.__generate_connection_uri(
            self.__root_user, self.__root_password))
        #bind = metadata.bind
        # Creates a connection with permission to create users and databases
        #metadata.bind = self.__generate_connection_uri(
            #self.__root_user, self.__root_password)
        # Create database cannot be executed as a transaction block so we
        # should change the isolation level to create a database
        #metadata.bind.engine.connect().\
            #connection.connection.set_isolation_level(0)
        #connection = metadata.bind.engine.connect()
        connection = engine.connect()
        connection.connection.connection.set_isolation_level(0)
        # Generate data and queries
        database_name, user_name, password = self.__create_random_data()
        # It is needed to concatenate this way to avoid the usual but
        # incompatible way to escape strings while executing some admin-level
        # sql commands
        sql_create_user = (
            "CREATE USER " + user_name +
            " WITH PASSWORD '" + password + "';")
        sql_create_database = (
            "CREATE DATABASE " + database_name +
            " WITH OWNER " + user_name +
            " ENCODING 'UTF8' "  # @TODO: Check if it is correct
            " LC_CTYPE='en_US.utf8'"
            " LC_COLLATE='en_US.utf8' TEMPLATE template0;")
        # Show the queries
        print("LOG: " + sql_create_user)
        print("LOG: " + sql_create_database)
        # Perform the queries
        connection.execute(sql_create_user)
        connection.execute(sql_create_database)
        #text(sql_create_user, metadata.bind).execute(
            #user=user_name, password=password)
        #text(sql_create_database, metadata.bind).execute(
            #database=database_name)
        #text(sql_revoke_permissions, metadata.bind).execute(user = user_name)
        #metadata.bind.engine.dispose()
        connection.close()
        #metadata.bind.engine.dispose()
        engine.dispose()
        # Restores the old database
        #metadata.bind = bind
        return database_name, user_name, password

    def __delete_mysql_resource(self, database_name, user_name):
        bind = metadata.bind
        # Creates a connection with permission to create users and databases
        metadata.bind = self.__generate_connection_uri(
            self.__root_user, self.__root_password)
        #connection = metadata.bind.engine.connect()
        #connection.connection.connection.set_isolation_level(0)
        metadata.bind.engine.connect().\
            connection.connection.set_isolation_level(0)
        # Generate data and queries
        sql_delete_database = "DROP DATABASE " + database_name + ";"
        sql_delete_user = "DROP USER " + user_name + ";"
        # Perform the queries
        text(sql_delete_database, metadata.bind).execute(
            database=database_name)
        text(sql_delete_user, metadata.bind).execute(user=user_name)
        #connection.close()
        metadata.bind.engine.dispose()
        # Restores the old database_name
        metadata.bind = bind
