'''
Created on 06/09/2011

@author: david
'''
import unittest
from nublic.resource.mysql_db import MysqlDB


class MysqlDBTest(unittest.TestCase):


    def setUp(self):
        self.mysql = MysqlDB() 
        pass


    def tearDown(self):
        pass


    def testName(self):
        pass
    
    def testProviderType(self):
        self.assertEqual(self.mysql.providerType, 'mysql-db', 
                         "Wrong provider id")  

    def testInstall(self):
        self.assertEqual(self.mysql.providerType, 'mysql-db', 
                         "Wrong provider id")  
        self.assertTrue(False, "Oops")

    def testUninstall(self):
        self.assertEqual(self.mysql.providerType, 'mysql-db', 
                         "Wrong provider id")  


if __name__ == "__main__":
    #import sys;sys.argv = ['', 'Test.testName']
    unittest.main()