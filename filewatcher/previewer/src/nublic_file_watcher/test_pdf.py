# -*- coding: utf-8 -*-
import unittest
import pdf
from file_info import FileInfo
import os
import shutil
from pgmagick.api import Image

import logging
logging.basicConfig()


class TmpFileTest(unittest.TestCase):
    def test_make_tmp_file(self):
        self.assertEquals(pdf.make_tmp_file(
            "/tmp/loco", "tonto"), "/tmp/tontoloco")
        self.assertEquals(pdf.make_tmp_file(
            "/tmp/ññ", "tonto"), "/tmp/tontoññ")
        self.assertEquals(pdf.make_tmp_file(
            "/tmp/ññ", "test_"), "/tmp/test_ññ")
        self.assertEquals(
            pdf.make_tmp_file("/tmp/out.pdf", "test_"), "/tmp/test_out.pdf")
        self.assertEquals(
            pdf.make_tmp_file("/tmp/dir/moredir/yet/another/dir", "test_"),
            "/tmp/dir/moredir/yet/another/test_dir")
        self.assertEquals(
            pdf.make_tmp_file("/tmp/dir/moredir/yet/another/dir"),
            "/tmp/dir/moredir/yet/another/tmp_dir")


class PdfTest(unittest.TestCase):
    def setUp(self):
        self.test_dir = "/tmp/test_pdf/test_dir/"
        os.system("rm -rf /tmp/test_pdf/test_dir/")
        shutil.copytree("test_files", "/tmp/test_pdf/test_dir")

    def test_pdf_to_thumbnail(self):
        test_file = "test_files/recetas.pdf"
        test_output = self.test_dir + "test_thumbnail_recetas.png"
        self.assertFalse(os.path.exists(test_output))
        pdf.pdf_to_thumb(test_file, test_output)
        self.assertTrue(os.path.exists(test_output))
        thumb = FileInfo(test_output)
        self.assertEquals(thumb.mime_type(), "image/png")
        thumb_image = Image(test_output)
        self.assertLessEqual(thumb_image.width, pdf.THUMBNAIL_SIZE)
        self.assertLessEqual(thumb_image.height, pdf.THUMBNAIL_SIZE)
        self.assertLessEqual(os.stat(test_output).st_size, 500 * 1024)

    def test_pdf_view_converter(self):
        test_file = self.test_dir + "recetas.pdf"
        test_output = self.test_dir + "pdf.pdf"
        pdf.pdf_view_converter(test_file, test_output)
        self.assertTrue(os.path.exists(test_output))
        self.assertTrue(os.path.islink(test_output))

    def test_djvu_view_converter(self):
        test_file = "test_files/2001_compression_overview.djvu"
        test_output = self.test_dir + "djvu.pdf"
        pdf.djvu_view_converter(test_file, test_output)
        self.assertTrue(os.path.exists(test_output))
        info = FileInfo(test_output)
        self.assertEquals(info.mime_type(), "application/pdf")

    def test_office_view_converter(self):
        test_file = self.test_dir + "trucos-linux.odf"
        test_output = self.test_dir + "odf.pdf"
        pdf.office_view_converter(test_file, test_output)
        self.assertTrue(os.path.exists(test_output))
        info = FileInfo(test_output)
        self.assertEquals(info.mime_type(), "application/pdf")

    def test_office_view_converter2(self):
        test_file = "test_files/presentacion-pcm.pptx"
        test_output = self.test_dir + "pptx.pdf"
        pdf.office_view_converter(test_file, test_output)
        self.assertTrue(os.path.exists(test_output))
        info = FileInfo(test_output)
        self.assertEquals(info.mime_type(), "application/pdf")

    def test_office_view_converter3(self):
        test_file = \
            "test_files/estadística_ine_uso_tecnologias_y_ordenadores.xls"
        test_output = self.test_dir + "xls.pdf"
        pdf.office_view_converter(test_file, test_output)
        self.assertTrue(os.path.exists(test_output))
        info = FileInfo(test_output)
        self.assertEquals(info.mime_type(), "application/pdf")

    def test_dvi_view_converter(self):
        test_file = "test_files/cw.dvi"
        test_output = self.test_dir + "dvi.pdf"
        pdf.dvi_view_converter(test_file, test_output)
        self.assertTrue(os.path.exists(test_output))
        info = FileInfo(test_output)
        self.assertEquals(info.mime_type(), "application/pdf")

    def test_ps_view_converter(self):
        test_file = "test_files/cw.ps"
        test_output = self.test_dir + "ps.pdf"
        pdf.ps_view_converter(test_file, test_output)
        self.assertTrue(os.path.exists(test_output))
        info = FileInfo(test_output)
        self.assertEquals(info.mime_type(), "application/pdf")


class PdfConverterPathTest(unittest.TestCase):
    def setUp(self):
        self.test_file = "test_files/recetas.pdf"
        self.p = pdf.PdfConverter(
            self.test_file, cache_path="test_files/cache")

    def test_cache_path2(self):
        self.assertEquals(self.p.cache_path, "test_files/cache")

    def test_thumb_path2(self):
        self.assertEquals(
            self.p.thumb_path, "test_files/cache/thumbnail.png")

    def test_view_path2(self):
        self.assertEquals(self.p.view_path, "test_files/cache/view.pdf")

    def test_needs_pdf(self):
        self.assertTrue(self.p.needs_pdf())


class PdfConverterTest(unittest.TestCase):
    def setUp(self):
        #shutil.copytree("test_files", "/tmp/test_pdf/test_dir")
        os.system("cp test_files/recetas.pdf /tmp/test_pdf/")
        self.test_file = "/tmp/test_pdf/recetas.pdf"
        self.test_cache_dir = "/tmp/test_pdf/cache"
        os.system("rm -rf " + self.test_cache_dir)
        os.environ.update({'BROWSER_CACHE_FOLDER': self.test_cache_dir})
        if not os.path.exists(self.test_cache_dir):
            os.makedirs(self.test_cache_dir)
        self.pdf = pdf.PdfConverter(self.test_file)

    def test_generate_view(self):
        self.pdf.generate_view()
        cache_dirs = os.listdir(self.test_cache_dir)
        self.assertEquals(len(cache_dirs), 1,
                          "Cache dir must be created")
        cache_dir = os.path.join(self.test_cache_dir, cache_dirs[0])
        cache_files = os.listdir(cache_dir)
        self.assertEquals(len(cache_files), 2,
                          "Cache dir must have thumb and view")
        self.assertIn("view.pdf", cache_files,
                          "Cache dir must have view")
        self.assertIn("thumbnail.png", cache_files,
                          "Cache dir must have thumb")
        # Check cache_files
        view_file = os.path.join(cache_dir, "view.pdf")
        thumb_file = os.path.join(cache_dir, "thumbnail.png")
        self.assertTrue(os.path.exists(view_file),
                          "Cache dir must have view")
        self.assertTrue(os.path.exists(thumb_file),
                          "Cache dir must have thumb")
        self.assertTrue(os.path.islink(view_file),
                          "Cache dir must be a link")

    def test_generate_updated_view(self):
        self.pdf.generate_view()
        os.system("touch /tmp/test_pdf/recetas.pdf")
        self.pdf.generate_view()
        cache_dirs = os.listdir(self.test_cache_dir)
        self.assertEquals(len(cache_dirs), 1,
                          "Cache dir must be created")
        cache_dir = os.path.join(self.test_cache_dir, cache_dirs[0])
        cache_files = os.listdir(cache_dir)
        self.assertEquals(len(cache_files), 2,
                          "Cache dir must have thumb and view")
        self.assertIn("view.pdf", cache_files,
                          "Cache dir must have view")
        self.assertIn("thumbnail.png", cache_files,
                          "Cache dir must have thumb")
        # Check cache_files
        view_file = os.path.join(cache_dir, "view.pdf")
        thumb_file = os.path.join(cache_dir, "thumbnail.png")
        self.assertTrue(os.path.exists(view_file),
                          "Cache dir must have view")
        self.assertTrue(os.path.exists(thumb_file),
                          "Cache dir must have thumb")
        self.assertTrue(os.path.islink(view_file),
                          "Cache dir must be a link")

    def test_generate_broken_link_view(self):
        self.pdf.generate_view()
        os.system("rm /tmp/test_pdf/cache/a8a258361d0cd185206ce0b61fa168649822217e/view.pdf")
        os.system("ln -s /broken_link /tmp/test_pdf/cache/a8a258361d0cd185206ce0b61fa168649822217e/view.pdf")
        self.pdf.generate_view()
        os.system("touch /tmp/test_pdf/recetas.pdf")
        self.pdf.generate_view()
        cache_dirs = os.listdir(self.test_cache_dir)
        self.assertEquals(len(cache_dirs), 1,
                          "Cache dir must be created")
        cache_dir = os.path.join(self.test_cache_dir, cache_dirs[0])
        cache_files = os.listdir(cache_dir)
        self.assertEquals(len(cache_files), 2,
                          "Cache dir must have thumb and view")
        self.assertIn("view.pdf", cache_files,
                          "Cache dir must have view")
        self.assertIn("thumbnail.png", cache_files,
                          "Cache dir must have thumb")
        # Check cache_files
        view_file = os.path.join(cache_dir, "view.pdf")
        thumb_file = os.path.join(cache_dir, "thumbnail.png")
        self.assertTrue(os.path.exists(view_file),
                          "Cache dir must have view")
        self.assertTrue(os.path.exists(thumb_file),
                          "Cache dir must have thumb")
        self.assertTrue(os.path.islink(view_file),
                          "Cache dir must be a link")





