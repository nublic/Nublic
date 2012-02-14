#!/usr/bin/env python

from collections import defaultdict
from errno import ENOENT, EIO
from stat import S_IFDIR, S_IFREG
from time import time
from threading import Lock

from fuse import FUSE, FuseOSError, Operations, LoggingMixIn
from model import *

import os
import hashlib

def to_utf8(string):
    return unicode(string, 'utf-8')

def from_utf8(string):
    return string.encode('utf-8')

class Filesystem(LoggingMixIn, Operations):
    """Example memory filesystem. Supports only one level of files."""
    
    NUBLIC_BROWSER_ROOT = "/var/nublic/cache/browser"
    AUDIO_FILENAME = "audio.mp3"
    
    def __init__(self):
        setup_all()
        self.rwlock = Lock()
        
    def getattr(self, path, fh=None):
        elements = path.split("/")
        if path == "/": # /
            coll_count = Collection.query.count()
            return dict(st_mode=S_IFDIR, st_nlink=2 + 1 + coll_count,
                        st_size=0, st_ctime=0, st_mtime=0, st_atime=0)
        elif len(elements) == 2: # /tag
            coll = elements[1]
            artist_count = self.create_artist_query(coll).count()
            return dict(st_mode=S_IFDIR, st_nlink=2 + 1 + artist_count,
                        st_size=0, st_ctime=0, st_mtime=0, st_atime=0)
        elif len(elements) == 3: # /tag/artist
            coll = elements[1]
            artist = elements[2]
            album_count = self.create_album_query(coll, artist).count()
            return dict(st_mode=S_IFDIR, st_nlink=2 + 1 + album_count,
                        st_size=0, st_ctime=0, st_mtime=0, st_atime=0)
        elif len(elements) == 4: # /tag/artist/album
            coll = elements[1]
            artist = elements[2]
            album = elements[3]
            song_count = self.create_song_query(coll, artist, album).count()
            return dict(st_mode=S_IFDIR, st_nlink=2 + song_count,
                        st_size=0, st_ctime=0, st_mtime=0, st_atime=0)
        elif len(elements) == 5: # /tag/artist/album/song
            song_part = elements[4]
            song_id = long(song_part.split(".")[-2])
            song = Song.get_by(id=song_id)
            if not song is None:
                song_path = from_utf8(song.file)
                song_digest = hashlib.sha1(song_path).hexdigest()
                final_path = Filesystem.NUBLIC_BROWSER_ROOT + "/" + song_digest + "/" + Filesystem.AUDIO_FILENAME
                st = os.lstat(final_path)
                return dict((key, getattr(st, key)) for key in ('st_atime', 'st_mode', 'st_ctime', 'st_mtime', 'st_nlink', 'st_size'))
        else:
            raise FuseOSError(ENOENT)
    
    def getxattr(self, path, name, position=0):
        return ''
    
    def open(self, path, flags):
        elements = path.split("/")
        if len(elements) == 5:
            song_part = elements[4]
            song_id = long(song_part.split(".")[-2])
            song = Song.get_by(id=song_id)
            if not song is None:
                song_path = from_utf8(song.file)
                song_digest = hashlib.sha1(song_path).hexdigest()
                final_path = Filesystem.NUBLIC_BROWSER_ROOT + "/" + song_digest + "/" + Filesystem.AUDIO_FILENAME
                return os.open(final_path, flags)
        # If no file or not in read mode
        raise FuseOSError(EIO)
    
    def read(self, path, size, offset, fh):
        with self.rwlock:
            os.lseek(fh, offset, 0)
            return os.read(fh, size)
    
    def readdir(self, path, fh):
        elements = path.split("/")
        if path == "/": # /
            r = [".", "..", "_all"]
            for coll in Collection.query.all():
                r.append(from_utf8(coll.name))
            return r
        elif len(elements) == 2: # /tag
            r = [".", "..", "_all"]
            coll = elements[1]
            for artist in self.create_artist_query(coll).all():
                r.append(from_utf8(artist.name))
            return list(set(r))
        elif len(elements) == 3: # /tag/artist
            coll = elements[1]
            artist = elements[2]
            r = [".", "..", "_all"]
            for album in self.create_album_query(coll, artist).all():
                r.append(from_utf8(album.name))
            return list(set(r))
        elif len(elements) == 4: # /tag/artist/album
            coll = elements[1]
            artist = elements[2]
            album = elements[3]
            r = [".", ".."]
            for song in self.create_song_query(coll, artist, album).all():
                r.append(from_utf8(song.title) + "." + str(song.id) + ".mp3")
            return list(set(r))
        else:
            raise FuseOSError(ENOENT)
    
    def create_artist_query(self, coll):
        if coll == "_all":
            return Artist.query
        else:
            return Artist.query.filter(Artist.songs.any(Song.tags.any(Collection.name == coll)))
    
    def create_album_query(self, coll, artist):
        if coll == "_all" and artist == "_all":
            return Album.query
        elif coll == "_all":
            return Album.query.filter(Album.songs.any(Song.artist.has(name=artist)))
        elif artist == "_all":
            return Album.query.filter(Album.songs.any(Song.tags.any(Collection.name == coll)))
        else:
            return Album.query.filter(Album.songs.any(Song.tags.any(Collection.name == coll))).filter(Album.songs.any(Song.artist.has(name=artist)))
    
    def create_song_query(self, coll, artist, album):
        if coll == "_all" and artist == "_all" and album == "_all":
            return Song.query
        # One selected
        elif coll == "_all" and artist == "_all" and album != "_all":
            return Song.query.filter(Song.album.has(name=album))
        elif coll == "_all" and artist != "_all" and album == "_all":
            return Song.query.filter(Song.artist.has(name=artist))
        elif coll != "_all" and artist == "_all" and album == "_all":
            return Song.query.filter(Song.tags.any(Collection.name == coll))
        # Two selected
        elif coll != "_all" and artist != "_all" and album == "_all":
            return Song.query.filter(Song.tags.any(Collection.name == coll)).filter(Song.artist.has(name=artist))
        elif coll != "_all" and artist == "_all" and album != "_all":
            return Song.query.filter(Song.tags.any(Collection.name == coll)).filter(Song.album.has(name=album))
        elif coll == "_all" and artist != "_all" and album != "_all":
            return Song.query.filter(Song.artist.has(name=artist)).filter(Song.album.has(name=album))
        # All selected
        else:
            return Song.query.filter(Song.tags.any(Collection.name == coll)).filter(Song.artist.has(name=artist)).filter(Song.album.has(name=album))

if __name__ == "__main__":
    fuse = FUSE(Filesystem(), "/var/nublic/fs/music", foreground=True)
