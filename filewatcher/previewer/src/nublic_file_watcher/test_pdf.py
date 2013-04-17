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
        test_file = "test_files/estadística_ine_uso_tecnologias_y_ordenadores.xls"
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
        self.p = pdf.PdfConverter(self.test_file)

    def test_cache_path(self):
        self.assertEquals(
            self.p.cache_path(), "/var/nublic/cache/browser/5ce0763869d65ce770fd114773b98f5f827b4e4c")

    def test_view_path(self):
        self.assertEquals(
            self.p.view_path(), "/var/nublic/cache/browser/5ce0763869d65ce770fd114773b98f5f827b4e4c/view.pdf")

    def test_thumb_path(self):
        self.assertEquals(
            self.p.thumb_path(), "/var/nublic/cache/browser/5ce0763869d65ce770fd114773b98f5f827b4e4c/thumbnail.png")


class PdfConverterPath2Test(unittest.TestCase):
    def setUp(self):
        self.test_file = "test_files/recetas.pdf"
        self.p = pdf.PdfConverter(
            self.test_file, cache_path="test_files/cache")

    def test_cache_path2(self):
        self.assertEquals(self.p.cache_path(), "test_files/cache")

    def test_thumb_path2(self):
        self.assertEquals(
            self.p.thumb_path(), "test_files/cache/thumbnail.png")

    def test_view_path2(self):
        self.assertEquals(self.p.view_path(), "test_files/cache/view.pdf")

    def test_needs_pdf(self):
        self.assertTrue(self.p.needs_pdf())


class PdfConverterPathTest(unittest.TestCase):
    def setUp(self):
        self.test_dir = "/tmp/test_pdf/test_dir/"
        os.system("rm -rf /tmp/test_pdf/test_dir/")
        shutil.copytree("test_files", "/tmp/test_pdf/test_dir")

    def test_generate_thumb(self):
        test_file = "test_files/recetas.pdf"
        p = pdf.PdfConverter(test_file, cache_path=self.test_dir)
        self.assertEquals(p.cache_path(), self.test_dir)
        thumb = p.generate_thumb()
        self.assertEquals(thumb, self.test_dir + "thumbnail.png")
        thumb_image = Image(thumb)
        self.assertLessEqual(thumb_image.width, pdf.THUMBNAIL_SIZE)
        self.assertLessEqual(thumb_image.height, pdf.THUMBNAIL_SIZE)
        self.assertLessEqual(os.stat(thumb).st_size, 500 * 1024)

    def test_generate_pdf_pdf(self):
        test_file = "test_files/recetas.pdf"
        p = pdf.PdfConverter(test_file, cache_path=self.test_dir)
        self.assertEquals(p.cache_path(), self.test_dir)
        pdf_out = p.generate_pdf()
        self.assertEquals(pdf_out, self.test_dir + "view.pdf")
        i = FileInfo(pdf_out)
        self.assertEqual(i.mime_type(), "inode/symlink")

    def test_generate_pdf_djvu(self):
        test_file = "test_files/2001_compression_overview.djvu"
        p = pdf.PdfConverter(test_file, cache_path=self.test_dir)
        self.assertEquals(p.cache_path(), self.test_dir)
        pdf_out = p.generate_pdf()
        self.assertEquals(pdf_out, self.test_dir + "view.pdf")
        i = FileInfo(pdf_out)
        self.assertEqual(i.mime_type(), "application/pdf")

    def test_generate_pdf_ps(self):
        test_file = "test_files/cw.ps"
        p = pdf.PdfConverter(test_file, cache_path=self.test_dir)
        self.assertEquals(p.cache_path(), self.test_dir)
        pdf_out = p.generate_pdf()
        self.assertEquals(pdf_out, self.test_dir + "view.pdf")
        i = FileInfo(pdf_out)
        self.assertEqual(i.mime_type(), "application/pdf")

    def test_generate_pdf_dvi(self):
        test_file = "test_files/cw.dvi"
        p = pdf.PdfConverter(test_file, cache_path=self.test_dir)
        self.assertEquals(p.cache_path(), self.test_dir)
        pdf_out = p.generate_pdf()
        self.assertEquals(pdf_out, self.test_dir + "view.pdf")
        i = FileInfo(pdf_out)
        self.assertEqual(i.mime_type(), "application/pdf")

    def test_generate_pdf_odf(self):
        test_file = "test_files/trucos-linux.odf"
        p = pdf.PdfConverter(test_file, cache_path=self.test_dir)
        self.assertEquals(p.cache_path(), self.test_dir)
        pdf_out = p.generate_pdf()
        self.assertEquals(pdf_out, self.test_dir + "view.pdf")
        i = FileInfo(pdf_out)
        self.assertEqual(i.mime_type(), "application/pdf")

    def test_generate_pdf_xls(self):
        test_file = "test_files/estadística_ine_uso_tecnologias_y_ordenadores.xls"
        p = pdf.PdfConverter(test_file, cache_path=self.test_dir)
        self.assertEquals(p.cache_path(), self.test_dir)
        pdf_out = p.generate_pdf()
        self.assertEquals(pdf_out, self.test_dir + "view.pdf")
        i = FileInfo(pdf_out)
        self.assertEqual(i.mime_type(), "application/pdf")

    def test_generate_pdf_pptx(self):
        test_file = "test_files/presentacion-pcm.pptx"
        p = pdf.PdfConverter(test_file, cache_path=self.test_dir)
        self.assertEquals(p.cache_path(), self.test_dir)
        pdf_out = p.generate_pdf()
        self.assertEquals(pdf_out, self.test_dir + "view.pdf")
        i = FileInfo(pdf_out)
        self.assertEqual(i.mime_type(), "application/pdf")
