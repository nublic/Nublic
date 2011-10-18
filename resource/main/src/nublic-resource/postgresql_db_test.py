'''
Created on 06/09/2011

@author: david
'''
import unittest
from nublic-resource.postgresql_db import PostgresqlDB
from nublic-resource.database_stored import ExistingKeyError
from nublic-resource import database_stored

class PostgresqlDBTest(unittest.TestCase):

        
    def setUp(self):
        self.postgresql = PostgresqlDB() 
        pass


    def tearDown(self):
        try:
            self.postgresql.uninstall("Test_app", "test")
        except Exception:
            pass


    def testName(self):
        pass
    
    def testProviderType(self):
        self.assertEqual(self.postgresql.providerType, 'postgresql-db', 
                         "Wrong provider id")  

    def testInstall(self):
        self.postgresql.install("Test_app", "test")
        
        # Test no double installation
        with self.assertRaises(database_stored.ExistingKeyError):
            self.postgresql.install("Test_app", "test")

        # Test all values are retrievable 
        user = self.postgresql.value("Test_app", "test", "user")
        self.assertIsNotNone(user, "User for postgresql not available with value")
        password = self.postgresql.value("Test_app", "test", "pass")
        self.assertIsNotNone(password, 
                             "pass for postgresql not available with value")
        database = self.postgresql.value("Test_app", "test", "database")
        self.assertIsNotNone(database, 
                             "database for postgresql not available with value")

        self.assertEquals(self.postgresql.value("Test_app", "test", "uri"),
                          "postgresql://" + user + ":" + password + \
                          "@localhost/" + database)
        # @ŧodo Connect to the database to see if it works
        self.postgresql.uninstall("Test_app", "test")
        # @todo Try to connect to the database to see that it doesn't work

    
