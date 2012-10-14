import unittest
import files
import os
import tempfile
import shutil
import stat
from nublic_server.files import PermissionError, get_folders


class FileOwnTests(unittest.TestCase):
    def setUp(self):
        self.uid = os.getuid()
        self.fileWrite = tempfile.NamedTemporaryFile()
        self.fileWrite.file.write("Probando1")
        self.fileWrite.file.flush()
        self.fileRead = tempfile.NamedTemporaryFile(mode="rw")
        os.chmod(self.fileRead.name,0400)
        self.dirWrite = tempfile.mkdtemp()
        self.dirRead = tempfile.mkdtemp()
        os.chmod(self.dirRead, 0400)
    
    def tearDown(self):
        self.fileWrite.close()
        self.fileRead.close()
        shutil.rmtree(self.dirWrite)
        shutil.rmtree(self.dirRead)
        
    def testMkdir(self):
        files.mkdir(os.path.join(self.dirWrite,"try"), self.uid, -1)
        try:
            files.mkdir(os.path.join(self.dirRead,"try"), self.uid, -1)
        except PermissionError as e:
            self.assertEqual(e.operation, "Write", "Permission error wrong")
        else:
            self.fail("Permission should be denied") 

    def testTryWrite(self):
        files.tryWrite(self.fileWrite.name, self.uid)
        try:
            files.tryWrite(self.fileRead.name, self.uid)
        except PermissionError as e:
            self.assertEqual(e.operation, "Write", \
                             "Check for write does not work on Read file") 
        else:
            self.fail("No exception sent when trying to write")
    
    def testTryRead(self):
        files.tryRead(self.fileWrite.name, self.uid)
        files.tryRead(self.fileRead.name, self.uid)
        try:
            files.tryRead(self.fileRead.name + self.fileWrite.name, self.uid)
        except PermissionError as e:
            self.assertEqual(e.operation, "Read", \
                    "Check for read does not work on non existing file") 
        else:
            self.fail("No exception sent when trying to read non-existing file")

    def testCopy(self):
        files.copy(self.fileWrite.name, self.dirWrite, self.uid)
        result = os.path.join(self.dirWrite, \
                              os.path.basename(self.fileWrite.name))
        with open(result) as copied:
            self.assertEqual(copied.read(), open(self.fileWrite.name).read(),\
                             "Copied file is not equal to the original")
        result_st = os.stat(result)
        origin_st = os.stat(self.fileWrite.name)
        self.assertEqual(stat.S_IMODE(origin_st.st_mode), \
                         stat.S_IMODE(result_st.st_mode), \
                         "Permissions are not the same on copied file")
        
class FileDirectoryTests(unittest.TestCase):
    def setUp(self):
        self.uid = os.getuid()
        self.dirWrite = tempfile.mkdtemp()
        self.dirRead = tempfile.mkdtemp(dir = self.dirWrite)
        self.fileWrite = tempfile.NamedTemporaryFile(dir = self.dirWrite)
        self.fileWrite.file.write("Probando1")
        self.fileWrite.file.flush()
    
    def tearDown(self):
        shutil.rmtree(self.dirWrite, ignore_errors = True)

    def testGetFolders(self):
        folders = get_folders(3, self.dirWrite, self.uid)
        print(str(folders))
        