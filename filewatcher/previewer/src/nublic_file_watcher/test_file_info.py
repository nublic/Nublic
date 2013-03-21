# -*- coding: utf-8 -*-
import os
#import shutil
import unittest
#from os.path import join
# Configure library, it works at import time
#import file_info
from file_info import view_type, guess_mime_from_ext, FileInfo

import logging
logging.basicConfig(level=logging.INFO)

#import sys
#import os
#import os.path
#from converter import Converter
#import shlex
#import subprocess
#import pexpect


class MimesTest(unittest.TestCase):

    def test_view_type(self):
        self.assertEquals(view_type('application/pdf'), 'pdf')
        self.assertEquals(view_type('application/vnd.oasis.opendocument.spreadsheet'), 'pdf')

    def test_guess_mime_from_ext(self):
        self.assertEquals(guess_mime_from_ext('application/zip', '.docx'), 'application/vnd.openxmlformats-officedocument.wordprocessingml.document')


class FileInfoTest(unittest.TestCase):
    def setUp(self):
        self.test_dir = "test_files/"

    def test_file_plain(self):
        plain = FileInfo(self.test_dir + "Programas_a_instalar")
        self.assertEquals(plain.mime_type(), 'text/plain')
        self.assertEquals(plain.extension, '')
        self.assertEquals(plain.is_directory(), False)
        self.assertEquals(plain.view_type(), 'txt')

    def test_file_pdf(self):
        pdf = FileInfo(self.test_dir + "recetas.pdf")
        self.assertEquals(pdf.mime_type(), 'application/pdf')
        self.assertEquals(pdf.extension, '.pdf')
        self.assertEquals(pdf.is_directory(), False)
        self.assertEquals(pdf.view_type(), 'pdf')

    def test_file_xcf(self):
        xcf = FileInfo(self.test_dir + "Logo pequeño.xcf")
        self.assertEquals(xcf.mime_type(), 'image/x-xcf')
        self.assertEquals(xcf.extension, '.xcf')
        self.assertEquals(xcf.is_directory(), False)
        self.assertIsNone(xcf.view_type())

    def test_file_odf(self):
        f = FileInfo(self.test_dir + "trucos-linux.odf")
        self.assertEquals(f.mime_type(), 'application/vnd.oasis.opendocument.text')
        self.assertEquals(f.extension, '.odf')
        self.assertEquals(f.is_directory(), False)
        self.assertEquals(f.view_type(), 'pdf')

    def test_file_png(self):
        f = FileInfo(self.test_dir + "Logo pequeño.png")
        self.assertEquals(f.mime_type(), 'image/png')
        self.assertEquals(f.extension, '.png')
        self.assertEquals(f.is_directory(), False)
        self.assertEquals(f.view_type(), 'png')

    def test_file_tiff(self):
        f = FileInfo(self.test_dir + "interior.tif")
        self.assertEquals(f.mime_type(), 'image/tiff')
        self.assertEquals(f.extension, '.tif')
        self.assertEquals(f.is_directory(), False)
        self.assertEquals(f.view_type(), 'png')

    def test_file_ppt(self):
        f = FileInfo(self.test_dir + "EL_MERCADO.ppt")
        self.assertEquals(f.mime_type(), 'application/msword')
        self.assertEquals(f.extension, '.ppt')
        self.assertEquals(f.is_directory(), False)
        self.assertEquals(f.view_type(), 'pdf')

    def test_file_xls(self):
        f = FileInfo(self.test_dir + "estadística_ine_uso_tecnologias_y_ordenadores.xls")
        self.assertEquals(f.mime_type(), 'application/msword')
        self.assertEquals(f.extension, '.xls')
        self.assertEquals(f.is_directory(), False)
        self.assertEquals(f.view_type(), 'pdf')

    def test_file_ico(self):
        f = FileInfo(self.test_dir + "favicon.ico")
        self.assertEquals(f.mime_type(), 'image/x-icon')
        self.assertEquals(f.extension, '.ico')
        self.assertEquals(f.is_directory(), False)
        self.assertEquals(f.view_type(), 'png')

    def test_file_py(self):
        f = FileInfo(self.test_dir + 'setup.py')
        self.assertEquals(f.mime_type(), 'text/plain')
        self.assertEquals(f.extension, '.py')
        self.assertEquals(f.is_directory(), False)
        self.assertEquals(f.view_type(), 'txt')

    def test_file_sh(self):
        f = FileInfo(self.test_dir + 'manage_vagrant.sh')
        self.assertEquals(f.mime_type(), 'text/plain')
        self.assertEquals(f.extension, '.sh')
        self.assertEquals(f.is_directory(), False)
        self.assertEquals(f.view_type(), 'txt')

    def test_file_dir(self):
        f = FileInfo(self.test_dir + u"Música".encode('utf8'))
        self.assertEquals(f.mime_type(), 'inode/directory')
        self.assertEquals(f.extension, '')
        self.assertEquals(f.is_directory(), True)
        self.assertEquals(f.view_type(), None)

    def test_file_not_existing(self):
        path = "not_existing_file_weird_wolo"
        self.assertFalse(os.path.exists(path))
        f = FileInfo(path)
        self.assertEquals(f.mime_type(), None)
        self.assertEquals(f.extension, '')
        self.assertEquals(f.is_directory(), False)
        self.assertEquals(f.view_type(), None)
