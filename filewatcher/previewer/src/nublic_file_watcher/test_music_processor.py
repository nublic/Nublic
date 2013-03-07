# -*- coding: utf-8 -*-
import os
import shutil
import glob
#from os.path import join
# Configure library, it works at import time
os.environ.update({"BROWSER_CACHE_FOLDER":
                   "/tmp/test_music_processor/cache/browser",
                   "MUSIC_CACHE_FOLDER":
                   "/tmp/test_music_processor/cache/music",
                   "APP_MUSIC_SETTINGS": "/tmp/test_music_processor/config.cfg"})

if not os.path.exists("/tmp/test_music_processor/cache"):
    os.makedirs("/tmp/test_music_processor/cache")
if not os.path.exists("/tmp/test_music_processor/test_dir"):
    os.makedirs("/tmp/test_music_processor/test_dir")

with open("/tmp/test_music_processor/config.cfg", "w") as f:
    config_file = """
[nublic_app_photos_db]
nublic_app_photos_db_uri = sqlite:////tmp/test_music_processor/database.sqlite
SQLALCHEMY_DATABASE_URI = sqlite:////tmp/test_music_processor/database.sqlite
LOG_FILE = /tmp/test_music_processor/log_app.file
"""
    f.write(config_file)
# Remove database
if os.path.exists("/tmp/test_music_processor/database.sqlite"):
    os.remove("/tmp/test_music_processor/database.sqlite")


from nublic_file_watcher.music_processor import MusicProcessor
from nublic_server.places import get_cache_folder
import unittest

TAGGED_MIME_TYPES = [
    # MP4
    "audio/mp4",
    # MP3
    "audio/mpeg", "audio/x-mpeg", "audio/mp3",
    "audio/x-mp3", "audio/mpeg3", "audio/x-mpeg3",
    "audio/mpg", "audio/x-mpg", "audio/x-mpegaudio",
    # OGG
    "audio/ogg", "application/ogg", "audio/x-ogg",
    "application/x-ogg",
    # ASF
    "audio/asf",
    # WMA
    "audio/x-ms-wma",
    # Real
    "audio/rmf", "audio/x-rmf",
    # FLAC
    "audio/flac"
]
TAGGED_EXTENSIONS = [".mp3", ".mp4", ".ogg", ".flac", ".wma", ".rm"]

SUPPORTED_MIME_TYPES = TAGGED_MIME_TYPES + [
    # AAC
    "audio/aac", "audio/x-aac",
    # AC3
    "audio/ac3",
    # AIFF
    "audio/aiff", "audio/x-aiff", "sound/aiff",
    "audio/x-pn-aiff",
    # MIDI
    "audio/mid", "audio/x-midi",
    # AU
    "audio/basic", "audio/x-basic", "audio/au",
    "audio/x-au", "audio/x-pn-au", "audio/x-ulaw",
    # PCM
    "application/x-pcm",
    # WAV
    "audio/wav", "audio/x-wav", "audio/wave",
    "audio/x-pn-wav",
    # Various
    "audio/vnd.qcelp", "audio/x-gsm", "audio/snd"
]
SUPPORTED_EXTENSIONS = TAGGED_EXTENSIONS + [".wav", ".aac", ".ac3", ".aiff",
                                            ".mid", ".midi", ".au", ".pcm"]


import logging
logging.basicConfig(level=logging.INFO)

from nublic_app_music_server.model import (db, Album, Artist,
                                           Song, SongCollection, SongPlaylist)


#os.system("rm -rf /tmp/test_music_processor/cache/")


class MusicProcessorTest(unittest.TestCase):
    def setUp(self):
        self.music_processor = MusicProcessor()
        db.create_all()
        os.system("rm -rf /tmp/test_music_processor/cache/")
        os.system("rm -rf /tmp/test_music_processor/test_dir/")
        shutil.copytree("test_files", "/tmp/test_music_processor/test_dir")

    def tearDown(self):
        db.drop_all()
        #os.system("rm /tmp/test_music_processor/database.sqlite")

    def apply_to_process_updated(self, filename):
        self.music_processor.process_updated(filename, is_dir=True)
        self.music_processor.process_updated(filename, is_dir=False)

    def apply_to_process_deleted(self, filename):
        self.music_processor.process_deleted(filename, is_dir=True)
        self.music_processor.process_deleted(filename, is_dir=False)

    def apply_to_process_attribs_change(self, filename):
        self.music_processor.process_attribs_change(filename, is_dir=True)
        self.music_processor.process_attribs_change(filename, is_dir=False)

    def apply_to_process_moved(self, from_, to_):
        self.music_processor.process_moved(from_, to_, is_dir=True)
        self.music_processor.process_moved(from_, to_, is_dir=False)

    def assert_db_empty(self, message="db is not empty"):
        self.assertIs(Album.query.first(), None, message)
        self.assertIs(SongCollection.query.first(), None, message)
        self.assertIs(Artist.query.first(), None, message)
        self.assertIs(Song.query.first(), None, message)
        self.assertIs(SongPlaylist.query.first(), None, message)

    def assert_cache_empty(self, filename=None):
        if filename is None:
            if os.path.exists("/tmp/test_music_processor/cache/browser"):
                self.assertEquals(
                    os.listdir("/tmp/test_music_processor/cache/browser"), [],
                    "Cache must be empty but found %s" %
                    (os.listdir("/tmp/test_music_processor/cache/browser"),))
        else:
            cache = get_cache_folder(filename)
            views = glob.glob(os.path.join(cache, 'view.*'))
            self.assertEquals(len(views), 0, "Views found %s" % (views,))
        if os.path.exists("/tmp/test_music_processor/cache/music"):
            if os.path.exists("/tmp/test_music_processor/cache/music/albums"):
                self.assertEquals(
                    os.listdir(
                        "/tmp/test_music_processor/cache/music/albums"), [],
                    "Cache must be empty but found %s" %
                    (os.listdir("/tmp/test_music_processor/cache/music/albums"),))
            if os.path.exists("/tmp/test_music_processor/cache/music/artists"):
                self.assertEquals(
                    os.listdir(
                        "/tmp/test_music_processor/cache/music/artists"), [],
                    "Cache must be empty but found %s" %
                    (os.listdir("/tmp/test_music_processor/cache/music/artists"),))

    # Test do not crash on non existing file
    def test_non_existing_file_updated(self):
        filename = "/tmp/test_music_processor/not_existing"
        filename_unicode = u"/tmp/test_music_processor/con_eñe".encode('utf8')
        #  Updated
        self.apply_to_process_updated(filename)
        self.assert_db_empty(
            "Database must be empty adding a not existing file")
        self.assert_cache_empty()
        self.apply_to_process_updated(filename_unicode)
        self.assert_db_empty(
            "Database must be empty adding a not existing unicode path file")
        self.assert_cache_empty()
        #  Delete
        self.apply_to_process_deleted(filename)
        self.assert_db_empty(
            "Database must be empty adding a not existing file")
        self.assert_cache_empty()
        self.apply_to_process_deleted(filename_unicode)
        self.assert_db_empty(
            "Database must be empty adding a not existing unicode path file")
        self.assert_cache_empty()
        #  Attribs_change
        self.apply_to_process_attribs_change(filename)
        self.assert_db_empty(
            "Database must be empty adding a not existing file")
        self.assert_cache_empty()
        self.apply_to_process_attribs_change(filename_unicode)
        self.assert_db_empty(
            "Database must be empty adding a not existing unicode path file")
        self.assert_cache_empty()
        #  Moved
        self.apply_to_process_moved(filename_unicode, filename)
        self.assert_db_empty(
            "Database must be empty adding a not existing file")
        self.assert_cache_empty()
        self.apply_to_process_moved(filename, filename_unicode)
        self.assert_db_empty(
            "Database must be empty adding a not existing unicode path file")
        self.assert_cache_empty()

    # Test do not open an not valid file
    def test_non_valid_file(self):
        filename = "/tmp/test_music_processor/not_valid.mp3"
        filename_unicode = u"/tmp/test_music_processor/con_eñe.mp3".encode(
            'utf8')
        with open(filename, "w") as f:
            f.write("NOT AN AUDIO FILE!!")
            self.music_processor.process_updated(filename, False)
            self.assert_db_empty(
                "Database must be empty adding a not existing file")
            self.assert_cache_empty()
            #  Delete
            self.music_processor.process_deleted(filename, False)
            self.assert_db_empty(
                "Database must be empty adding a not existing file")
            self.assert_cache_empty()
            #  Attribs_change
            self.music_processor.process_attribs_change(filename, False)
            self.assert_db_empty(
                "Database must be empty adding a not existing file")
            self.assert_cache_empty()
            #  Moved
            self.music_processor.process_moved(
                filename_unicode, filename, False)
            self.assert_db_empty(
                "Database must be empty adding a not existing file")
            self.assert_cache_empty()
        with open(filename_unicode, "w") as f:
            f.write(u"NOT AN AUDIO ññññ FILE!!".encode('utf8'))
            #  Updated
            self.music_processor.process_updated(filename_unicode, False)
            self.assert_db_empty(
                "Database must be empty adding a not existing unicode path file")
            self.assert_cache_empty()
            #  Delete
            self.music_processor.process_deleted(filename_unicode, False)
            self.assert_db_empty(
                "Database must be empty adding a not existing unicode path file")
            self.assert_cache_empty()
            #  Attribs_change
            self.music_processor.process_attribs_change(
                filename_unicode, False)
            self.assert_db_empty(
                "Database must be empty adding a not existing unicode path file")
            self.assert_cache_empty()
            #  Moved
            self.music_processor.process_moved(
                filename_unicode, filename, False)
            self.assert_db_empty(
                "Database must be empty adding a not existing file")
            self.assert_cache_empty()
            self.music_processor.process_moved(
                filename, filename_unicode, False)
            self.assert_db_empty(
                "Database must be empty adding a not existing unicode path file")
            self.assert_cache_empty()

    def get_dirs_in_browser_cache(self):
        return [os.path.join("/tmp/test_music_processor/cache/browser/", name)
                for name in os.listdir("/tmp/test_music_processor/cache/browser/")
                if os.path.exists("/tmp/test_music_processor/cache/browser/")]

    def assert_view_exists(self, filepath):
        cache_folder = get_cache_folder(filepath)
        self.assertTrue(os.path.exists(cache_folder), "Cache view must exist")
        cache_view = os.path.join(cache_folder, "view.mp3")
        self.assertTrue(os.path.exists(cache_view), "Cache view must exist")
        self.assertTrue(os.path.islink(cache_view), "Cache view must be a link")

    def assert_file_is_added(self, filepath, artist_name, album_name, song_title):
        self.assertTrue(os.path.exists(filepath), "File does not exist")
        self.music_processor.process_updated(filepath, False)
        albums = Album.query.filter_by(name=album_name).all()
        self.assertNotEqual(albums, [], "Album must have been created")
        self.assertEqual(len(albums), 1, "Album must have been created")
        album_id = albums[0].id
        #self.assertEquals(albums.name, "Help!", "Album must be Help!")
        self.assertIs(SongCollection.query.first(), None,
                      "No song collection should have been created")
        artists = Artist.query.filter_by(name=artist_name).all()
        self.assertNotEqual(artists, [], "Artist must have been created")
        #self.assertEquals(artists.name, "The Beatles",
                          #"Artist must be the Beatles instead of %s" % artists.name)
        songs = Song.query.filter_by(title=song_title).all()
        self.assertNotEqual(songs, [], "Song must have been created")
        #song_id = songs[0].id
        #self.assertEquals(songs.title, "Help!",
                          #"Song must be the Help! instead of %s" % songs[0].title)
        self.assertIs(SongPlaylist.query.first(
        ), None, "No playlist must have been created")
        self.assert_view_exists(filepath)
        album_dir = os.path.join(
            "/tmp/test_music_processor/cache/music/albums", str(album_id))
        self.assertTrue(os.path.exists(
            os.path.join(album_dir, "orig")), "orig album cover must exist")
        self.assertTrue(
            os.path.exists(os.path.join(album_dir, "thumbnail.png")), "")

    # Test work with audio song with id3
    def test_audio_song(self):
        filepath = "/tmp/test_music_processor/test_dir/01 Help!.mp3"
        self.assertTrue(os.path.exists(filepath), "File does not exist")
        self.music_processor.process_updated(filepath, False)
        self.assert_file_is_added(filepath, "The Beatles", "Help!", "Help!")
        songs = Song.query.all()
        self.assertEquals(len(songs), 1, "Song must have been created")
        self.assertEquals(songs[0].title, "Help!",
                          "Song must be the Help! instead of %s" % songs[0].title)
        albums = Album.query.all()
        self.assertEquals(len(albums), 1, "Album must have been created")
        self.assertEquals(albums[0].name, "Help!", "Album must be Help!")
        self.assertIs(SongCollection.query.first(), None,
                      "No song collection should have been created")
        artists = Artist.query.all()
        self.assertEquals(len(artists), 1, "Artist must have been created")
        self.assertEquals(artists[0].name, "The Beatles",
                          "Artist must be the Beatles instead of %s" % artists[0].name)
        self.assertIs(SongPlaylist.query.first(
        ), None, "No playlist must have been created")
        dirs = self.get_dirs_in_browser_cache()
        self.assertEquals(len(dirs), 1, "Must be only one cache file")
        cache_view = os.path.join(dirs[0], "view.mp3")
        self.assertTrue(os.path.exists(cache_view), "Cache view must exist")
        self.assertTrue(
            os.path.islink(cache_view), "Cache view must be a link")
        album_dir = os.path.join(
            "/tmp/test_music_processor/cache/music/albums", "1")
        self.assertTrue(os.path.exists(
            os.path.join(album_dir, "orig")), "orig album cover must exist")
        self.assertTrue(
            os.path.exists(os.path.join(album_dir, "thumbnail.png")), "")
        # Now move the file
        old_filepath = filepath
        new_dir = u"/tmp/test_music_processor/test_dir/Música/".encode('utf8')
        if not os.path.exists(new_dir):
            os.mkdir(new_dir)
        filepath = u"/tmp/test_music_processor/test_dir/Música/01 Help!.mp3".encode('utf8')
        os.system("mv '%s' '%s'" % (old_filepath, new_dir))
        self.music_processor.process_moved(old_filepath, filepath, False)
        songs = Song.query.all()
        self.assertEquals(len(songs), 1, "Move should not create an extra song")
        song = songs[0]
        self.assertEquals(song.file, unicode(filepath, 'utf8'), "Path should be the right one")
        albums = Album.query.all()
        self.assertEquals(len(albums), 1, "Only one album should be created")
        artists = Artist.query.all()
        self.assertEquals(len(artists), 1, "Only one artists should be created")
        self.assert_view_exists(filepath)
        # Now reenter the file
        self.music_processor.process_updated(filepath, False)
        songs = Song.query.all()
        self.assertEquals(len(songs), 1, "Song must have been created")
        self.assertEquals(songs[0].title, "Help!",
                          "Song must be the Help! instead of %s" % songs[0].title)
        albums = Album.query.all()
        self.assertEquals(len(albums), 1, "Album must not have been created")
        self.assertEquals(albums[0].name, "Help!", "Album must be Help!")
        self.assertIs(SongCollection.query.first(), None,
                      "No song collection should have been created")
        artists = Artist.query.all()
        self.assertEquals(len(artists), 1, "Artist must not have been created")
        self.assertEquals(artists[0].name, "The Beatles",
                          "Artist must be the Beatles instead of %s" % artists[0].name)
        self.assertIs(SongPlaylist.query.first(),
                      None, "No playlist must have been created")
        # Now reenter the file slightly different
        filepath2 = filepath.replace(".mp3", "2.mp3")
        os.system("cp '%s' '%s'" % (filepath, filepath2))
        self.music_processor.process_updated(filepath2, False)
        songs = Song.query.all()
        self.assertEquals(len(songs), 2, "Song must have been created")
        self.assertEquals(songs[0].title, "Help!",
                          "Song must be the Help! instead of %s" % songs[0].title)
        albums = Album.query.all()
        self.assertEquals(len(albums), 1, "Album must not have been created")
        self.assertEquals(albums[0].name, "Help!", "Album must be Help!")
        self.assertIs(SongCollection.query.first(), None,
                      "No song collection should have been created")
        artists = Artist.query.all()
        self.assertEquals(len(artists), 1, "Artist must not have been created")
        self.assertEquals(artists[0].name, "The Beatles",
                          "Artist must be the Beatles instead of %s" % artists[0].name)
        self.assertIs(SongPlaylist.query.first(),
                      None, "No playlist must have been created")
        # Now delete the file
        os.remove(filepath)
        os.remove(filepath2)
        self.music_processor.process_deleted(filepath, False)
        self.music_processor.process_deleted(filepath2, False)
        self.assert_cache_empty(filepath)
        self.assert_cache_empty(filepath2)
        self.assert_db_empty()
        # Delete a file two times
        self.music_processor.process_deleted(filepath, False)
        self.assert_cache_empty(filepath)
        self.assert_db_empty()

    # Test get metadata from internet
    # Test work with audio song with id3
    @unittest.skip("Echonest not working")
    def test_audio_song_no_id3(self):
        filepath = "/tmp/test_music_processor/test_dir/01 Help!_no_id3.mp3"
        self.assertTrue(os.path.exists(filepath), "File does not exist")
        self.music_processor.process_updated(filepath, False)
        songs = Song.query.all()
        self.assertEquals(len(songs), 1, "Song must have been created")
        self.assertEquals(songs[0].title, "Help!",
                          "Song must be the Help! instead of %s" % songs[0].title)
        albums = Album.query.all()
        self.assertEquals(len(albums), 1, "Album must have been created")
        self.assertEquals(albums[0].name, "Help!", "Album must be Help!")
        self.assertIs(SongCollection.query.first(), None,
                      "No song collection should have been created")
        artists = Artist.query.all()
        self.assertEquals(len(artists), 1, "Artist must have been created")
        self.assertEquals(artists[0].name, "The Beatles",
                          "Artist must be the Beatles instead of %s" % artists[0].name)
        self.assertIs(SongPlaylist.query.first(
        ), None, "No playlist must have been created")
        dirs = self.get_dirs_in_browser_cache()
        self.assertEquals(len(dirs), 1, "Must be only one cache file")
        cache_view = os.path.join(dirs[0], "view.mp3")
        self.assertTrue(os.path.exists(cache_view), "Cache view must exist")
        self.assertTrue(
            os.path.islink(cache_view), "Cache view must be a link")
        album_dir = os.path.join(
            "/tmp/test_music_processor/cache/music/albums", "1")
        self.assertTrue(os.path.exists(
            os.path.join(album_dir, "orig")), "orig album cover must exist")
        self.assertTrue(
            os.path.exists(os.path.join(album_dir, "thumbnail.png")), "")
        # Now reenter the file
        self.music_processor.process_updated(filepath, False)
        songs = Song.query.all()
        self.assertEquals(len(songs), 1, "Song must have been created")
        self.assertEquals(songs[0].title, "Help!",
                          "Song must be the Help! instead of %s" % songs[0].title)
        albums = Album.query.all()
        self.assertEquals(len(albums), 1, "Album must not have been created")
        self.assertEquals(albums[0].name, "Help!", "Album must be Help!")
        self.assertIs(SongCollection.query.first(), None,
                      "No song collection should have been created")
        artists = Artist.query.all()
        self.assertEquals(len(artists), 1, "Artist must not have been created")
        self.assertEquals(artists[0].name, "The Beatles",
                          "Artist must be the Beatles instead of %s" % artists[0].name)
        self.assertIs(SongPlaylist.query.first(),
                      None, "No playlist must have been created")
        # Now delete the file
        os.remove(filepath)
        self.music_processor.process_deleted(filepath, False)
        self.assert_cache_empty(filepath)
        self.assert_db_empty()

    def test_id(self):
        self.assertEquals(self.music_processor.get_id(), "music", "Id must be music")

    #def process_updated(self, filename, is_dir, info=None):
    #def process_deleted(self, filename, is_dir, info=None):
    #def process_attribs_change(self, filename, is_dir, info):
    #def process_moved(self, filename, is_dir, info=None):
    #def process_updated_file(self, filename):

    # Test get metadata from file

    # Test get metadata from directory structure


    # Methods to test
    #def process_updated(self, filename, is_dir, info=None):
    #def process_deleted(self, filename, is_dir, info=None):
    #def process_attribs_change(self, filename, is_dir, info):
    #def process_moved(self, filename, is_dir, info=None):
    #def process_updated_file(self, filename):
    #def update_attribs(self, song):
    #def process_deleted_file(self, filename):
    #def process_moved_folder(self, from_, to):

    #def __init__(self, logger=None, watcher=''):
        #PreviewProcessor.__init__(self)
        #log.info('Music processor initialised')
        #db.init_app(app)
        ##self.ctx = app.test_request_context().push()
        ##app.preprocess_request()

    #def get_id(self):
        #return 'music'

    ## Inherited methods
    #def process_updated(self, filename, is_dir, info=None):
        #if not is_dir:
            #with app.test_request_context():
                #self.process_updated_file(filename)

    #def process_deleted(self, filename, is_dir, info=None):
        #if not is_dir:
            #with app.test_request_context():
                #self.process_deleted_file(filename)

    #def process_attribs_change(self, filename, is_dir, info):
        #if not is_dir:
            #with app.test_request_context():
                #filename_unicode = unicode(filename, 'utf8')
                #song = Song.query.filter_by(file=filename_unicode).first()
                #if song is not None:
                    #self.update_attribs(song)
                #else:
                    #self.process_updated_file(filename)

    #def process_moved(self, filename, is_dir, info=None):
        #with app.test_request_context():
            #if is_dir:
                #self.process_moved_dir(filename)
            #else:
                #self.process_moved_file(filename)

    #def is_hidden(self, path):
        #"""
        #path is an byte string in utf8
        #"""
        #filename = os.path.basename(path)
        #return filename.endswith('~') or filename.startswith('.')

    #def process_updated_file(self, filename):
        #''' Process an update of a file.
        #The file might exist before.
        #filename is an byte string in utf8
        #'''
        #filename_unicode = unicode(filename, 'utf8')
        #log.info('Updated file: %s', filename)
        #mime = get_mime_type(filename)  # @TODO To check for utf8
        #_, ext = os.path.splitext(filename)
        #if not self.is_hidden(filename):
            ## Process song
            #if mime in TAGGED_MIME_TYPES or ext in TAGGED_EXTENSIONS:
                #song_info = get_song_info(filename)  # @TODO To check for utf8
            #elif mime in SUPPORTED_MIME_TYPES or ext in SUPPORTED_EXTENSIONS:
                #song_info = extract_using_filename(
                    #filename).clean()  # @TODO To check for utf8
            #else:
                #song_info = None
            ## If we got some information
            #if song_info is not None:
                #s = Song.query.filter_by(file=filename_unicode).first()
                #if s is None:
                    #self.add_to_database(filename, song_info)
                #else:
                    #self.replace_in_database(filename, s, song_info)
                ## @TODO Do conversion if needed
                #log.warning("No compatibility check for audio file %s", filename)
                #cache_folder = ensure_cache_folder(filename)  # @TODO: Check for utf8
                #mp3_cache = os.path.join(cache_folder, 'view.mp3')
                #if os.path.exists(mp3_cache):
                    #log.warning("Cache existing for audio file %s, skipping", filename)
                #else:
                    #log.info("Creating cache for audio file %s", filename)
                    #os.symlink(filename, mp3_cache)

    #def update_attribs(self, song):
        #"""
        #Updates the attributes in the Song object in the database
        #song.file is an Unicode object
        #"""
        #song.owner = get_file_owner(song.file.encode('utf-8')).get_username()
        #song.shared = is_file_shared(song.file.encode('utf-8'))
        #db.session.commit()

    #def process_deleted_file(self, filename):
        #"""
        #Process a file deleted by removing the file from the previews
        #and the database
        #filename is a byte string in utf8
        #"""
        #filename_unicode = unicode(filename, 'utf8')
        #song = Song.query.filter_by(file=filename_unicode).first()
        #if song is not None:
            ## Delete from collections
            #SongCollection.query.filter_by(songId=song.id).delete()
            #db.session.commit()
            ## Delete from playlists
            #relation = SongPlaylist.query.filter_by(songId=song.id).first()
            #while relation is not None:
                #rest = SongPlaylist.query.filter_by(playlistId=relation.playlistId).filter(SongPlaylist.position > relation.position).all()
                #for other_song in rest:
                    #other_song.position -= 1
                #relation.delete()
                #db.session.commit()
                ## Try again
                #relation = SongPlaylist.query.filter_by(songId=song.id).first()
            ## Delete the song itself
            #self.delete_artist_if_no_assoc(song.artistId)
            #self.delete_album_if_no_assoc(song.albumId)
            #db.session.delete(song)
            #db.session.commit()
            ## @TODO HOW TO REMOVE THE RIGHT CACHE?
            #log.warning("No cache removed for audio file %s", filename)
            #mp3_cache = os.path.join(
                #ensure_cache_folder(filename), 'view.mp3')
            #if os.path.exists(mp3_cache):
                #log.info(
                    #"Cache removed for audio file %s", filename)
                #os.remove(mp3_cache)
            #else:
                #log.warning("Cache for audio file %s does not exist. Not removed",
                            #filename)

    #def process_moved_folder(self, from_, to):
        #"""
        #from_ and to are byte strings in utf8
        #"""
        #for e in os.listdir(to):
            #file_from = os.path.join(from_, e)
            #file_to = os.path.join(to, e)
            #if os.path.isdir(file_to) and not self.is_hidden(file_to):
                #self.process_moved_folder(file_from, file_to)
            #else:
                #self.process_moved_file(file_from, file_to)

    #def process_moved_file(self, from_, to):
        #"""
        #from_ and to are byte strings in utf8
        #"""
        #from_unicode = unicode(from_, 'utf8')
        #song = Song.query.filter_by(file=from_unicode).first()
        #if song is not None:
            #song.file = to
            #db.session.commit()
        #else:
            #self.process_updated_file(to)

    #def ensure_or_create_artist(self, artist_name):
        #"""
        #Returns an artist.
        #If the artist does not exists it is created.
        #This function tries to match close but not exact artists
        #"""
        #n_artist = unidecode(unicode(artist_name).lower())
        ## Try to find an artist
        #artist = Artist.query.filter_by(normalized=n_artist).first()
        #if artist is not None:
            #return artist
        #else:
            #new_artist = Artist(artist_name)
            #db.session.add(new_artist)
            #db.session.commit()
            #return new_artist

    #def ensure_or_create_album(self, file_, artist_name, album_name):
        #"""
        #Returns an album for the artist given.
        #If the album does not exist the album is created for the
        #given file_ before.
        #file_ is a byte string in utf8
        #"""
        #directory, _ = os.path.split(file_)
        ## Case 1. we have artist and album name
        #if artist_name is not None and album_name is not None:
            #ab = self.find_album_with_artist(artist_name, album_name)
            #if ab is not None:
                #return ab
            #else:
                #return self.find_album_by_directory(directory, album_name)
        #elif album_name is not None:
            #return self.find_album_by_directory(directory, album_name)
        #else:
            #if artist_name is None:
                #r_artist_name = ''
            #else:
                #r_artist_name = artist_name
            #if album_name is None:
                #r_album_name = ''
            #else:
                #r_album_name = artist_name
            #ab = self.find_album_with_artist(r_artist_name, r_album_name)
            #if ab is not None:
                #return ab
            #else:
                #new_album = Album(r_album_name)
                #db.session.add(new_album)
                #db.session.commit()
                #return new_album

    #def find_album_with_artist(self, artist_name, album_name):
        #n_artist = unidecode(unicode(artist_name).lower())
        #n_album = unidecode(unicode(album_name).lower())
        ## Try to get artist
        #artist = Artist.query.filter_by(normalized=n_artist).first()
        #if artist is None:
            #return None
        #else:
            #return Album.query.filter_by(normalized=n_album).filter(Song.albumId == Album.id).filter(Song.artistId == artist.id).first()

    #def find_album_by_directory(self, directory, album_name):
        #n_album = unidecode(unicode(album_name).lower())
        ## Try to find a song with name album name
        #ab = Album.query.filter_by(normalized=n_album).filter(Song.albumId == Album.id).filter(Song.file.like(directory + '/%')).first()
        #if ab is not None:
            #return ab
        #else:
            #new_album = Album(album_name)
            #db.session.add(new_album)
            #db.session.commit()
            #return new_album

    #def delete_artist_if_no_assoc(self, artist_id):
        #songs = Song.query.filter_by(artistId=artist_id).count()
        #if songs == 0:
            #Artist.query.filter_by(id=artist_id).delete()
            #db.session.commit()

    #def delete_album_if_no_assoc(self, album_id):
        #songs = Song.query.filter_by(albumId=album_id).count()
        #if songs == 0:
            #Album.query.filter_by(id=album_id).delete()
            #db.session.commit()

    #def add_to_database(self, filename, song_info):
        #''' Add the song to the database if required.
        #filename is a byte string in utf8
        #'''
        #filename_unicode = unicode(filename, 'utf8')
        ## Just in case anything is None
        #if song_info.title is None:
            #r_title = ''
        #else:
            #r_title = song_info.title
        #if song_info.artist is None:
            #r_artist_name = ''
        #else:
            #r_artist_name = song_info.artist
        #if song_info.album is None:
            #r_album_name = ''
        #else:
            #r_album_name = song_info.album
        #if song_info.length is None:
            #r_length = 0
        #else:
            #r_length = song_info.length
        #log.info('Adding to database: %s by %s in %s', r_title,
                 #r_artist_name, r_album_name)
        ## Ensure artist
        #artist = self.ensure_or_create_artist(r_artist_name)
        #ensure_artist_image(artist.id, r_artist_name)
        ## Ensure album
        #album = self.ensure_or_create_album(
            #filename, r_artist_name, r_album_name)
        #ensure_album_image(album.id, filename, r_album_name, r_artist_name)  # @ TODO Check for unicode problems
        ## Create song in database
        #song = Song(filename_unicode, r_title, artist.id, album.id, r_length,
                    #song_info.year, song_info.track, song_info.disc_no)
        #db.session.add(song)
        #db.session.commit()

    #def replace_in_database(self, filename, s, song_info):
        #"""
        #Replace a song that changed in the database
        #filename is a byte string in utf8
        #"""
        ## Just in case anything is None
        #if song_info.title is None:
            #r_title = ''
        #else:
            #r_title = song_info.title
        #if song_info.artist is None:
            #r_artist_name = ''
        #else:
            #r_artist_name = song_info.artist
        #if song_info.album is None:
            #r_album_name = ''
        #else:
            #r_album_name = song_info.album
        #if song_info.length is None:
            #r_length = 0
        #else:
            #r_length = song_info.length
        ## Save previous artist and album
        #prev_artist_id = s.artistId
        #prev_album_id = s.albumId
        ## Ensure artist
        #artist = self.ensure_or_create_artist(r_artist_name)
        #ensure_artist_image(artist.id, r_artist_name)
        ## Ensure album
        #album = self.ensure_or_create_album(
            #filename, r_artist_name, r_album_name)
        #ensure_album_image(album.id, filename, r_album_name, r_artist_name)
        ## Change song object
        #s.title = r_title
        #s.artistId = artist.id
        #s.albumId = album.id
        #s.length = r_length
        #s.year = song_info.year
        #s.track = song_info.track
        #s.disc_no = song_info.disc_no
        #db.session.commit()
        ## Delete previous artist and album if not needed
        #self.delete_artist_if_no_assoc(prev_artist_id)
        #self.delete_album_if_no_assoc(prev_album_id)