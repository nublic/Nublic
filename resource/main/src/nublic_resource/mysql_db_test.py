'''
Created on 06/09/2011

@author: david
'''
import unittest
from nublic_resource.mysql_db import MysqlDB
#from nublic_resource.database_stored import ExistingKeyError
from nublic_resource import database_stored


class MysqlDBTest(unittest.TestCase):

    def setUp(self):
        self.mysql = MysqlDB()
        pass

    def tearDown(self):
        try:
            self.mysql.uninstall("Test_app", "test")
        except Exception:
            pass

    def testName(self):
        pass

    def testProviderType(self):
        self.assertEqual(self.mysql.providerType, 'mysql-db',
                         "Wrong provider id")

    def testPort(self):
        self.assertEqual(self.mysql.value("Test_app", "test", "port"),
                         "3306",
                         "Wrong provider id")

    def testInstall(self):
        self.mysql.install("Test_app", "test")

        # Test no double installation
        with self.assertRaises(database_stored.ExistingKeyError):
            self.mysql.install("Test_app", "test")

        # Test all values are retrievable
        user = self.mysql.value("Test_app", "test", "user")
        self.assertIsNotNone(user, "User for Mysql not available with value")
        password = self.mysql.value("Test_app", "test", "pass")
        self.assertIsNotNone(password,
                             "pass for Mysql not available with value")
        database = self.mysql.value("Test_app", "test", "database")
        self.assertIsNotNone(database,
                             "database for Mysql not available with value")

        self.assertEquals(self.mysql.value("Test_app", "test", "uri"),
                          "mysql://")

        self.mysql.uninstall("Test_app", "test")

    def testUninstall(self):
        self.assertEqual(self.mysql.providerType, 'mysql-db',
                         "Wrong provider id")


#if __name__ == "__main__":
    #import sys;sys.argv = ['', 'Test.testName']
    #unittest.main()
