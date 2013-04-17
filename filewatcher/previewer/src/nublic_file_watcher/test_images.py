# -*- coding: utf-8 -*-
import unittest
import images
from file_info import FileInfo
import os
import shutil
from pgmagick.api import Image

import logging
logging.basicConfig()


class TmpFileTest(unittest.TestCase):
    def test_make_tmp_file(self):
        self.assertEquals(images.make_tmp_file(
            "/tmp/loco", "tonto"), "/tmp/tontoloco")
        self.assertEquals(images.make_tmp_file(
            "/tmp/ññ", "tonto"), "/tmp/tontoññ")
        self.assertEquals(images.make_tmp_file(
            "/tmp/ññ", "test_"), "/tmp/test_ññ")
        self.assertEquals(
            images.make_tmp_file("/tmp/out.jpg", "test_"), "/tmp/test_out.jpg")
        self.assertEquals(
            images.make_tmp_file("/tmp/dir/moredir/yet/another/dir", "test_"),
            "/tmp/dir/moredir/yet/another/test_dir")


class ImagesTest(unittest.TestCase):
    def setUp(self):
        self.test_dir = "/tmp/test_pdf/test_dir/"
        os.system("rm -rf /tmp/test_pdf/test_dir/")
        shutil.copytree("test_files", "/tmp/test_pdf/test_dir")

    def test_image_to_thumbnail(self):
        test_file = "test_files/Neuschwanstein.jpg"
        test_output = self.test_dir + "test_thumbnail_Neuschwanstein.png"
        self.assertFalse(os.path.exists(test_output))
        images.image_to_thumb(test_file, test_output)
        self.assertTrue(os.path.exists(test_output))
        thumb = FileInfo(test_output)
        self.assertEquals(thumb.mime_type(), "image/png")
        thumb_image = Image(test_output)
        self.assertLessEqual(thumb_image.width, images.THUMBNAIL_SIZE)
        self.assertLessEqual(thumb_image.height, images.THUMBNAIL_SIZE)
        self.assertLessEqual(os.stat(test_output).st_size, 500 * 1024)

    def test_pdf_view_converter(self):
        test_file = "test_files/Neuschwanstein.jpg"
        test_output = self.test_dir + "test_thumbnail_Neuschwanstein.jpg"
        images.image_view_converter(test_file, test_output)
        self.assertTrue(os.path.exists(test_output))
        thumb = FileInfo(test_output)
        self.assertEquals(thumb.mime_type(), "image/jpeg")
        thumb_image = Image(test_output)
        self.assertLessEqual(thumb_image.width, images.VIEW_SIZE)
        self.assertLessEqual(thumb_image.height, images.VIEW_SIZE)
        self.assertLessEqual(os.stat(test_output).st_size, 500 * 1024)

    def test_pdf_view_converter2(self):
        test_file = "test_files/Schottland 1.JPG"
        test_output = self.test_dir + "test_thumbnail_Schottland 1.JPG"
        images.image_view_converter(test_file, test_output)
        self.assertTrue(os.path.exists(test_output))
        thumb = FileInfo(test_output)
        self.assertEquals(thumb.mime_type(), "image/jpeg")
        thumb_image = Image(test_output)
        self.assertLessEqual(thumb_image.width, images.VIEW_SIZE)
        self.assertLessEqual(thumb_image.height, images.VIEW_SIZE)
        self.assertLessEqual(os.stat(test_output).st_size, 500 * 1024)

    def test_pdf_view_converter3(self):
        test_file = "test_files/Schottland 2.JPG"
        test_output = self.test_dir + "test_thumbnail_Schottland 2.JPG"
        images.image_view_converter(test_file, test_output)
        self.assertTrue(os.path.exists(test_output))
        thumb = FileInfo(test_output)
        self.assertEquals(thumb.mime_type(), "image/jpeg")
        thumb_image = Image(test_output)
        self.assertLessEqual(thumb_image.width, images.VIEW_SIZE)
        self.assertLessEqual(thumb_image.height, images.VIEW_SIZE)
        self.assertLessEqual(os.stat(test_output).st_size, 500 * 1024)
