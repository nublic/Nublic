""" Test Cache Processor """
# -*- coding: utf-8 -*-
import os
import shutil
import glob
# from os.path import join
# Configure library, it works at import time
os.environ.update({"BROWSER_CACHE_FOLDER":
                   "/tmp/test_cache_processor/cache/browser",
                   "MUSIC_CACHE_FOLDER":
                   "/tmp/test_cache_processor/cache/music",
                   "APP_MUSIC_SETTINGS":
                   "/tmp/test_cache_processor/config.cfg"})

if not os.path.exists("/tmp/test_cache_processor/cache"):
    os.makedirs("/tmp/test_cache_processor/cache")
if not os.path.exists("/tmp/test_cache_processor/test_dir"):
    os.makedirs("/tmp/test_cache_processor/test_dir")

with open("/tmp/test_cache_processor/config.cfg", "w") as f:
    config_file = """
[nublic_app_photos_db]
LOG_FILE = /tmp/test_cache_processor/log_app.file
"""
    f.write(config_file)
# Remove database
if os.path.exists("/tmp/test_cache_processor/database.sqlite"):
    os.remove("/tmp/test_cache_processor/database.sqlite")


from nublic_file_watcher.cache_processor import CacheProcessor
from nublic_server.places import get_cache_folder
import unittest

import logging
logging.basicConfig(level=logging.INFO)

# os.system("rm -rf /tmp/test_cache_processor/cache/")


class CacheProcessorTest(unittest.TestCase):

    def setUp(self):
        self.cache_processor = CacheProcessor()
        os.system("rm -rf /tmp/test_cache_processor/cache/")
        os.system("rm -rf /tmp/test_cache_processor/test_dir/")
        shutil.copytree("test_files", "/tmp/test_cache_processor/test_dir")

    def tearDown(self):
        # os.system("rm /tmp/test_cache_processor/database.sqlite")
        pass

    def apply_to_process_updated(self, filename):
        self.cache_processor.process_updated(filename, is_dir=True)
        self.cache_processor.process_updated(filename, is_dir=False)

    def apply_to_process_deleted(self, filename):
        self.cache_processor.process_deleted(filename, is_dir=True)
        self.cache_processor.process_deleted(filename, is_dir=False)

    def apply_to_process_attribs_change(self, filename):
        self.cache_processor.process_attribs_change(filename, is_dir=True)
        self.cache_processor.process_attribs_change(filename, is_dir=False)

    def apply_to_process_moved(self, from_, to_):
        self.cache_processor.process_moved(from_, to_, is_dir=True)
        self.cache_processor.process_moved(from_, to_, is_dir=False)

    def cache_exists(self, filename):
        cache = get_cache_folder(filename)
        return os.path.exists(cache)

    def assert_cache_empty(self, filename=None):
        if filename is None:
            if os.path.exists("/tmp/test_cache_processor/cache/browser"):
                self.assertEquals(
                    os.listdir("/tmp/test_cache_processor/cache/browser"), [],
                    "Cache must be empty but found %s" %
                    (os.listdir("/tmp/test_cache_processor/cache/browser"),))
        else:
            cache = get_cache_folder(filename)
            views = glob.glob(os.path.join(cache, 'view.*'))
            self.assertEquals(len(views), 0, "Views found %s" % (views,))
        if os.path.exists("/tmp/test_cache_processor/cache/music"):
            if os.path.exists("/tmp/test_cache_processor/cache/music/albums"):
                self.assertEquals(
                    os.listdir(
                        "/tmp/test_cache_processor/cache/music/albums"), [],
                    "Cache must be empty but found %s" %
                    (os.listdir(
                        "/tmp/test_cache_processor/cache/music/albums"),))
            if os.path.exists("/tmp/test_cache_processor/cache/music/artists"):
                self.assertEquals(
                    os.listdir(
                        "/tmp/test_cache_processor/cache/music/artists"), [],
                    "Cache must be empty but found %s" %
                    (os.listdir(
                        "/tmp/test_cache_processor/cache/music/artists"),))

    def assert_cache_not_existing(self, filename=None):
        cache = get_cache_folder(filename)
        self.assertFalse(os.path.exists(cache), "Cache must not exist")

    # Test do not crash on non existing file
    def test_non_existing_file_updated(self):
        filename = "/tmp/test_cache_processor/not_existing"
        filename_unicode = u"/tmp/test_cache_processor/con_eñe".encode('utf8')
        self.assert_cache_not_existing(filename)
        #  Updated
        self.apply_to_process_updated(filename)
        self.assert_cache_empty()
        self.assert_cache_not_existing(filename)
        self.apply_to_process_updated(filename_unicode)
        self.assert_cache_empty()
        self.assert_cache_not_existing(filename_unicode)
        #  Delete
        self.apply_to_process_deleted(filename)
        self.assert_cache_empty()
        self.assert_cache_not_existing(filename)
        self.apply_to_process_deleted(filename_unicode)
        self.assert_cache_empty()
        self.assert_cache_not_existing(filename_unicode)
        #  Attribs_change
        self.apply_to_process_attribs_change(filename)
        self.assert_cache_empty()
        self.assert_cache_not_existing(filename)
        self.apply_to_process_attribs_change(filename_unicode)
        self.assert_cache_empty()
        self.assert_cache_not_existing(filename_unicode)
        #  Moved
        self.apply_to_process_moved(filename_unicode, filename)
        self.assert_cache_empty()
        self.assert_cache_not_existing(filename)
        self.apply_to_process_moved(filename, filename_unicode)
        self.assert_cache_empty()
        self.assert_cache_not_existing(filename_unicode)

    # Test do not open an not valid file
    def test_any_file(self):
        filename = "/tmp/test_cache_processor/any"
        filename_unicode = u"/tmp/test_cache_processor/añy".encode(
            'utf8')
        with open(filename, "w") as f:
            f.write("File\n")
            self.cache_processor.process_updated(filename, False)
            self.assertTrue(self.cache_exists(filename))
            #self.assert_cache_empty()
            #  Attribs_change
            self.cache_processor.process_attribs_change(filename, False)
            self.assertTrue(self.cache_exists(filename))
            #  Moved not existing
            self.cache_processor.process_moved(
                filename_unicode, filename, False)
            self.assertTrue(self.cache_exists(filename))
            self.assertFalse(self.cache_exists(filename_unicode))
            #  Moved
            self.cache_processor.process_moved(
                filename, filename_unicode, False)
            self.assertFalse(self.cache_exists(filename))
            self.assertTrue(self.cache_exists(filename_unicode))
            #  Delete
            self.cache_processor.process_deleted(filename, False)
            self.assertFalse(self.cache_exists(filename))
            self.cache_processor.process_deleted(filename_unicode, False)
            self.assertFalse(self.cache_exists(filename_unicode))
            self.assert_cache_empty()

    def test_id(self):
        self.assertEquals(
            self.cache_processor.get_id(), "cache", "Id must be cache")

