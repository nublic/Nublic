#!/usr/bin/python

'''
Created on 15/08/2010

@author: David Navarro Estruch
@copyright: 2011 Nublic
'''

import unittest
from notification import Notification, new_message

from elixir import create_all, setup_all, drop_all


class testNotification(unittest.TestCase):
    """
    A test class for the Notification module.
    """

    def __init__(self, methodName='runTest'):
        # Clean and create database
        setup_all(create_tables=True)
        drop_all()
        create_all()
        unittest.TestCase.__init__(self, methodName=methodName)

    def setUp(self):
        """
        set up data used in the tests.
        setUp is called before each test function execution.
        """
        pass

    def testNewMessage(self):
        new_message("app", "user1", "level", "CriticalMessage")
        notice = Notification.get_by(app="app")
        self.assertEqual(notice.app, "app",
                         "Value retrieved should be the stored")
        self.assertEqual(notice.user, "user1",
                         "Value retrieved should be the stored")
        self.assertEqual(notice.level, "level",
                         "Value retrieved should be the stored")
        self.assertEqual(notice.text, "CriticalMessage",
                         "Value retrieved should be the stored")
        #self.assertEqual(self.blogger.get_title(), title)


#
#def suite():
#
#    suite = unittest.TestSuite()
#
#    suite.addTest(unittest.makeSuite(testBlogger))
#
#    return suite
#    pass
#
#if __name__ == '__main__':
#    unittest.main()
