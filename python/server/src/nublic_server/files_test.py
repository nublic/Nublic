#!/usr/bin/python
# -*- coding: utf-8 -*-

import unittest
#import files
import os
import tempfile
import shutil
#import stat
from nublic_server.files import get_last_dir_name


class FileOwnTests(unittest.TestCase):
    def setUp(self):
        self.uid = os.getuid()
        self.fileWrite = tempfile.NamedTemporaryFile()
        self.fileWrite.file.write("Probando1")
        self.fileWrite.file.flush()
        self.fileRead = tempfile.NamedTemporaryFile(mode="rw")
        os.chmod(self.fileRead.name, 0400)
        self.dirWrite = tempfile.mkdtemp()
        self.dirRead = tempfile.mkdtemp()
        os.chmod(self.dirRead, 0400)

    def tearDown(self):
        self.fileWrite.close()
        self.fileRead.close()
        shutil.rmtree(self.dirWrite)
        shutil.rmtree(self.dirRead)

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


class FileDirectoryTests(unittest.TestCase):
    def setUp(self):
        self.uid = os.getuid()
        self.dirWrite = tempfile.mkdtemp()
        self.dirRead = tempfile.mkdtemp(dir=self.dirWrite)
        self.fileWrite = tempfile.NamedTemporaryFile(dir=self.dirWrite)
        self.fileWrite.file.write("Probando1")
        self.fileWrite.file.flush()

    def tearDown(self):
        shutil.rmtree(self.dirWrite, ignore_errors=True)

    #def testGetFolders(self):
    #    folders = get_folders(3, self.dirWrite, self.uid)

        #print(unicode(folders))


class DirectoryTests(unittest.TestCase):
    def test_get_last_dir_name(self):
        self.assertEqual(get_last_dir_name("/loco/mongo"), "mongo")
        self.assertEqual(get_last_dir_name("/loco/mongo/"), "mongo")
        self.assertEqual(get_last_dir_name("/locño/mon`aügo"), "mon`aügo")
        self.assertEqual(get_last_dir_name("/locño/mon`aügo/"), "mon`aügo")
        self.assertEqual(get_last_dir_name("/"), "")


if __name__ == '__main__':
            unittest.main()
