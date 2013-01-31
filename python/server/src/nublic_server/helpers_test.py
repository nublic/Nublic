#!/usr/bin/python
# -*- coding: utf-8 -*-

import unittest
#import files
#import os
#import tempfile
#import shutil
#
from flask import Flask
from nublic_server.helpers import init_bare_nublic_server  # , init_nublic_server
#import nublic
#from mock import patch


class InitServerTest(unittest.TestCase):
    def setUp(self):
        self.app = Flask(__name__)

        #self.fileWrite = tempfile.NamedTemporaryFile()
        #self.fileWrite.file.write("Probando1")
        #self.fileWrite.file.flush()

    def tearDown(self):
        # self.fileWrite.close()
        pass

    def init_bare_nublic_server_test(self):
        init_bare_nublic_server(self.app, '/dev/null')
        self.assertIsNotNone(self.app.logger)
        self.assertEqual(self.app.name, self.app.logger.name)
        self.assertEqual(len(self.app.logger.handlers), 2)
        # @TODO: It should test that the logger works

    #def init_nublic_server_test(self):
        #with patch.object(nublic.resource.App):
            #init_nublic_server(self.app, '/dev/null')

    #def testMkdir(self):
    #    files.mkdir(os.path.join(self.dirWrite, "try"), self.uid, -1)
    #    try:
    #        files.mkdir(os.path.join(self.dirRead, "try"), self.uid, -1)
    #    except PermissionError as e:
    #        self.assertEqual(e.operation, "Write", "Permission error wrong")
    #    else:
    #        self.fail("Permission should be denied")

    #def testCopy(self):
    #    files.copy(self.fileWrite.name, self.dirWrite, self.uid)
    #    result = os.path.join(self.dirWrite, \
    #                          os.path.basename(self.fileWrite.name))
    #    with open(result) as copied:
    #        self.assertEqual(copied.read(), open(self.fileWrite.name).read(),\
    #                         "Copied file is not equal to the original")
    #    result_st = os.stat(result)
    #    origin_st = os.stat(self.fileWrite.name)
    #    self.assertEqual(stat.S_IMODE(origin_st.st_mode), \
    #                     stat.S_IMODE(result_st.st_mode), \
    #                     "Permissions are not the same on copied file")
