import os
import os.path
from unidecode import unidecode
import shutil

#from nublic.filewatcher import FileChange
from nublic.files_and_users import get_file_owner, is_file_shared
from nublic_server.places import (get_mime_type, ensure_cache_folder,
                                  get_cache_folder)
from preview_processor import PreviewProcessor
from nublic_app_music_server.server import app
from nublic_app_music_server.model import (db, Album, Artist,
                                           Song, SongCollection, SongPlaylist)
from song_info import (get_song_info,
                       extract_using_filename)
from nublic_app_music_server.images import (get_artist_folder,
                                            get_album_folder,
                                            ensure_artist_image,
                                            ensure_album_image)

import logging
log = logging.getLogger(__name__)

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


# Set up processors
class MusicProcessor(PreviewProcessor):
    def __init__(self, logger=None, watcher=''):
        PreviewProcessor.__init__(self)
        log.info('Music processor initialised')
        db.init_app(app)

    def get_id(self):
        return 'music'

    # Inherited methods
    def process_updated(self, filename, is_dir, info=None):
        if not is_dir:
            with app.test_request_context():
                self.process_updated_file(filename)

    def process_deleted(self, filename, is_dir, info=None):
        if not is_dir:
            with app.test_request_context():
                self.process_deleted_file(filename)

    def process_attribs_change(self, filename, is_dir, info=None):
        if not is_dir:
            with app.test_request_context():
                filename_unicode = unicode(filename, 'utf8')
                song = Song.query.filter_by(file=filename_unicode).first()
                if song is not None:
                    self.update_attribs(song)
                else:
                    self.process_updated_file(filename)

    def process_moved(self, filename_from, filename_to, is_dir, info=None):
        with app.test_request_context():
            if is_dir:
                self.process_moved_dir(filename_from, filename_to)
            else:
                self.process_moved_file(filename_from, filename_to)

    def is_hidden(self, path):
        """
        path is an byte string in utf8
        """
        filename = os.path.basename(path)
        return filename.endswith('~') or filename.startswith('.')

    def create_view(self, filename):
        log.warning("No compatibility check for audio file %s", filename)
        cache_folder = ensure_cache_folder(filename)  # @TODO: Check for utf8
        mp3_cache = os.path.join(cache_folder, 'view.mp3')
        if os.path.exists(mp3_cache):
            # @TODO Check if it is a symlink and if it is not if the
            # view is newer than the cache
            log.warning("Cache existing for audio file %s, skipping", filename)
        else:
            log.info("Creating cache for audio file %s", filename)
            # @TODO Do conversion if needed
            os.symlink(filename, mp3_cache)

    def process_updated_file(self, filename):
        ''' Process an update of a file.
        The file might exist before.
        filename is an byte string in utf8
        '''
        if os.path.exists(filename):
            filename_unicode = unicode(filename, 'utf8')
            log.info('Updated file: %s', filename)
            mime = get_mime_type(filename)
            #_, ext = os.path.splitext(filename)
            # Process song
            if mime in TAGGED_MIME_TYPES:  # or ext in TAGGED_EXTENSIONS:
                song_info = get_song_info(filename)
            elif mime in SUPPORTED_MIME_TYPES:  # or ext in SUPPORTED_EXTENSIONS:
                song_info = extract_using_filename(
                    filename).clean()
            else:
                song_info = None
            # If we got some information
            if song_info is not None:
                self.create_view(filename)
                # Add to database now
                s = Song.query.filter_by(file=filename_unicode).first()
                if s is None:
                    self.add_to_database(filename, song_info)
                else:
                    self.replace_in_database(filename, s, song_info)

    def update_attribs(self, song):
        """
        Updates the attributes in the Song object in the database
        song.file is an Unicode object
        """
        song.owner = get_file_owner(song.file.encode('utf-8')).get_username()
        song.shared = is_file_shared(song.file.encode('utf-8'))
        db.session.commit()

    def delete_song_from_collections(self, song_id):
        """ Delete from collections """
        SongCollection.query.filter_by(songId=song_id).delete()
        db.session.commit()

    def delete_song_from_playlists(self, song_id):
        " Delete from playlists "
        relation = SongPlaylist.query.filter_by(songId=song_id).first()
        while relation is not None:
            rest = SongPlaylist.query.filter_by(playlistId=relation.playlistId).filter(SongPlaylist.position > relation.position).all()
            for other_song in rest:
                other_song.position -= 1
            db.session.delete(relation)
            #relation.delete()
            db.session.commit()
            # Try again
            relation = SongPlaylist.query.filter_by(songId=song_id).first()

    def remove_view(self, filename):
        # exists return false for broken symlinks so we need to check is link
        # before exists to remove the broken symlink
        mp3_cache = os.path.join(get_cache_folder(filename), 'view.mp3')
        if os.path.islink(mp3_cache) or os.path.exists(mp3_cache):
            log.info(
                "Cache removed for audio file %s as %s", filename, mp3_cache)
            os.remove(mp3_cache)
        else:
            log.warning("Cache for audio file %s does not exist in %s. Not removed",
                        filename, mp3_cache)

    def process_deleted_file(self, filename):
        """
        Process a file deleted by removing the file from the previews
        and the database
        filename is a byte string in utf8
        """
        filename_unicode = unicode(filename, 'utf8')
        song = Song.query.filter_by(file=filename_unicode).first()
        if song is not None:
            self.delete_song_from_collections(song.id)
            self.delete_song_from_playlists(song.id)
            # Delete the song itself
            artistId = song.artistId
            albumId = song.albumId
            db.session.delete(song)
            db.session.commit()
            self.delete_artist_if_no_assoc(artistId)
            self.delete_album_if_no_assoc(albumId)
            self.remove_view(filename)

    def process_moved_dir(self, from_, to):
        """
        from_ and to are byte strings in utf8
        """
        if os.path.exists(to):  # The dir was removed before this. Nothing to do
            for e in os.listdir(to):
                file_from = os.path.join(from_, e)
                file_to = os.path.join(to, e)
                if os.path.isdir(file_to) and not self.is_hidden(file_to):
                    self.process_moved_dir(file_from, file_to)
                else:
                    self.process_moved_file(file_from, file_to)

    def process_moved_file(self, from_, to):
        """
        from_ and to are byte strings in utf8
        """
        from_unicode = unicode(from_, 'utf8')
        song = Song.query.filter_by(file=from_unicode).first()
        if song is not None:
            song.file = unicode(to, 'utf8')
            db.session.commit()
            # @TODO Do not recreate view if not needed
            self.remove_view(from_)
            self.create_view(to)
        else:
            self.process_updated_file(to)

    def ensure_or_create_artist(self, artist_name):
        """
        Returns an artist.
        If the artist does not exists it is created.
        This function tries to match close but not exact artists
        """
        n_artist = unidecode(unicode(artist_name).lower())
        # Try to find an artist
        artist = Artist.query.filter_by(normalized=n_artist).first()
        if artist is not None:
            return artist
        else:
            new_artist = Artist(artist_name)
            db.session.add(new_artist)
            db.session.commit()
            return new_artist

    def ensure_or_create_album(self, file_, artist_name, album_name):
        """
        Returns an album for the artist given.
        If the album does not exist the album is created for the
        given file_ before.
        file_ is a byte string in utf8
        """
        directory, _ = os.path.split(file_)
        # Case 1. we have artist and album name
        if artist_name is not None and album_name is not None:
            ab = self.find_album_with_artist(artist_name, album_name)
            if ab is not None:
                return ab
            else:
                return self.find_album_by_directory(directory, album_name)
        elif album_name is not None:
            return self.find_album_by_directory(directory, album_name)
        else:
            if artist_name is None:
                r_artist_name = ''
            else:
                r_artist_name = artist_name
            if album_name is None:
                r_album_name = ''
            else:
                r_album_name = artist_name
            ab = self.find_album_with_artist(r_artist_name, r_album_name)
            if ab is not None:
                return ab
            else:
                new_album = Album(r_album_name)
                db.session.add(new_album)
                db.session.commit()
                return new_album

    def find_album_with_artist(self, artist_name, album_name):
        n_artist = unidecode(unicode(artist_name).lower())
        n_album = unidecode(unicode(album_name).lower())
        # Try to get artist
        artist = Artist.query.filter_by(normalized=n_artist).first()
        if artist is None:
            return None
        else:
            return Album.query.filter_by(normalized=n_album).filter(Song.albumId == Album.id).filter(Song.artistId == artist.id).first()

    def find_album_by_directory(self, directory, album_name):
        n_album = unidecode(unicode(album_name).lower())
        # Try to find a song with name album name
        ab = Album.query.filter_by(normalized=n_album).filter(Song.albumId == Album.id).filter(Song.file.like(directory + '/%')).first()
        if ab is not None:
            return ab
        else:
            new_album = Album(album_name)
            db.session.add(new_album)
            db.session.commit()
            return new_album

    def delete_artist_if_no_assoc(self, artist_id):
        songs = Song.query.filter_by(artistId=artist_id).count()
        if songs == 0:
            Artist.query.filter_by(id=artist_id).delete()
            db.session.commit()
            shutil.rmtree(get_artist_folder(artist_id))

    def delete_album_if_no_assoc(self, album_id):
        songs = Song.query.filter_by(albumId=album_id).count()
        if songs == 0:
            Album.query.filter_by(id=album_id).delete()
            db.session.commit()
            shutil.rmtree(get_album_folder(album_id))

    def add_to_database(self, filename, song_info):
        ''' Add the song to the database if required.
        filename is a byte string in utf8
        '''
        filename_unicode = unicode(filename, 'utf8')
        # Just in case anything is None
        if song_info.title is None:
            r_title = ''
        else:
            r_title = song_info.title
        if song_info.artist is None:
            r_artist_name = ''
        else:
            r_artist_name = song_info.artist
        if song_info.album is None:
            r_album_name = ''
        else:
            r_album_name = song_info.album
        if song_info.length is None:
            r_length = 0
        else:
            r_length = song_info.length
        log.info('Adding to database: %s by %s in %s', r_title,
                 r_artist_name, r_album_name)
        # Ensure artist
        artist = self.ensure_or_create_artist(r_artist_name)
        ensure_artist_image(artist.id, r_artist_name)
        # Ensure album
        album = self.ensure_or_create_album(
            filename, r_artist_name, r_album_name)
        ensure_album_image(album.id, filename, r_album_name, r_artist_name)
        # Create song in database
        song = Song(filename_unicode, r_title, artist.id, album.id, r_length,
                    song_info.year, song_info.track, song_info.disc_no)
        db.session.add(song)
        db.session.commit()

    def replace_in_database(self, filename, s, song_info):
        """
        Replace a song that changed in the database
        filename is a byte string in utf8
        """
        # Just in case anything is None
        if song_info.title is None:
            r_title = ''
        else:
            r_title = song_info.title
        if song_info.artist is None:
            r_artist_name = ''
        else:
            r_artist_name = song_info.artist
        if song_info.album is None:
            r_album_name = ''
        else:
            r_album_name = song_info.album
        if song_info.length is None:
            r_length = 0
        else:
            r_length = song_info.length
        # Save previous artist and album
        prev_artist_id = s.artistId
        prev_album_id = s.albumId
        # Ensure artist
        artist = self.ensure_or_create_artist(r_artist_name)
        ensure_artist_image(artist.id, r_artist_name)
        # Ensure album
        album = self.ensure_or_create_album(
            filename, r_artist_name, r_album_name)
        ensure_album_image(album.id, filename, r_album_name, r_artist_name)
        # Change song object
        s.title = r_title
        s.artistId = artist.id
        s.albumId = album.id
        s.length = r_length
        s.year = song_info.year
        s.track = song_info.track
        s.disc_no = song_info.disc_no
        db.session.commit()
        # Delete previous artist and album if not needed
        self.delete_artist_if_no_assoc(prev_artist_id)
        self.delete_album_if_no_assoc(prev_album_id)
