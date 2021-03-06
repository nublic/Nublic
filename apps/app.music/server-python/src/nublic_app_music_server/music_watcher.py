import datetime
import EXIF
import os
import os.path
from unidecode import unidecode

from nublic.filewatcher import FileChange, Processor
from nublic.files_and_users import get_file_owner, is_file_shared
from nublic_server.places import get_mime_type
from model import db, Album, Artist, Collection, Playlist, Song, SongCollection, SongPlaylist
from song_info import get_song_info, extract_using_filename
from images import ensure_artist_image, ensure_album_image

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
SUPPORTED_EXTENSIONS = TAGGED_EXTENSIONS + [".wav", ".aac", ".ac3", ".aiff", ".mid", ".midi", ".au", ".pcm"]

# Set up processors
class MusicProcessor(Processor):
    def __init__(self, logger, watcher):
        Processor.__init__(self, 'music', watcher, False, logger)
        logger.error('Music processor initialised')

    def process(self, change):
        self.get_logger().error('Music processor file: %i %s, context: %s', change.kind, change.filename, change.context)
        if change.kind == FileChange.CREATED and not change.is_dir:
            self.process_updated_file(change.filename, change.context)
        elif change.kind == FileChange.MODIFIED and not change.is_dir:
            self.process_updated_file(change.filename, change.context)
        elif change.kind == FileChange.DELETED and not change.is_dir:
            self.process_deleted_file(change.filename)
        elif change.kind == FileChange.ATTRIBS_CHANGED and not change.is_dir:
            self.process_attribs_change(change.filename, change.context)
        elif change.kind == FileChange.MOVED:
            if change.is_dir:
                self.process_moved_folder(change.filename_from, change.filename_to, change.context)
            else:
                self.process_moved_file(change.filename_from, change.filename_to, change.context)
    
    def is_hidden(self, path):
        filename = os.path.basename(path)
        return filename.endswith('~') or filename.startswith('.')
    
    def process_updated_file(self, filename, context):
        self.get_logger().error('Updated file: %s', filename)
        mime = get_mime_type(filename)
        _, ext = os.path.splitext(filename)
        if not self.is_hidden(filename):
            # Process song
            if mime in TAGGED_MIME_TYPES or ext in TAGGED_EXTENSIONS:
                song_info = get_song_info(filename, context, self.get_logger())
            elif mime in SUPPORTED_MIME_TYPES or ext in SUPPORTED_EXTENSIONS:
                song_info = extract_using_filename(filename, context, self.get_logger()).clean()
            else:
                song_info = None
            # If we got some information
            if song_info != None:
                s = Song.query.filter_by(file=filename).first()
                if s == None:
                    self.add_to_database(filename, song_info)
                else:
                    self.replace_in_database(filename, s, song_info)
    
    def process_attribs_change(self, filename, context):
        song = Song.query.filter_by(file=filename).first()
        if song != None:
            self.update_attribs(song)
        else:
            self.process_updated_file(filename, context)
    
    def update_attribs(self, song):
        song.owner = get_file_owner(song.file.encode('utf-8')).get_username()
        song.shared = is_file_shared(song.file.encode('utf-8'))
        db.session.commit()
    
    def process_deleted_file(self, filename):
        song = Song.query.filter_by(file=filename).first()
        if song != None:
            # Delete from collections
            SongCollection.query.filter_by(songId=song.id).delete()
            db.session.commit()
            # Delete from playlists
            relation = SongPlaylist.query.filter_by(songId=song.id).first()
            while relation != None:
                rest = SongPlaylist.query.filter_by(playlistId=relation.playlistId).filter(SongPlaylist.position > relation.position).all()
                for other_song in rest:
                    other_song.position -= 1
                relation.delete()
                db.session.commit()
                # Try again
                relation = SongPlaylist.query.filter_by(songId=song.id).first()
            # Delete the song itself
            self.delete_artist_if_no_assoc(song.artistId)
            self.delete_album_if_no_assoc(song.albumId)
            db.session.delete(song)
            db.session.commit()
    
    def process_moved_folder(self, from_, to, context):
        for e in os.listdir(to):
            file_from = os.path.join(from_, e)
            file_to = os.path.join(to, e)
            if os.path.isdir(file_to) and not self.is_hidden(file_to):
                self.process_moved_folder(file_from, file_to, context)
            else:
                self.process_moved_file(file_from, file_to, context)
    
    def process_moved_file(self, from_, to, context):
        song = Song.query.filter_by(file=from_).first()
        if song != None:
            song.file = to
            db.session.commit()
        else:
            self.process_updated_file(to, context)
    
    def ensure_or_create_artist(self, artist_name):
        n_artist = unidecode(unicode(artist_name).lower())
        # Try to find an artist
        artist = Artist.query.filter_by(normalized=n_artist).first()
        if artist != None:
            return artist
        else:
            new_artist = Artist(artist_name)
            db.session.add(new_artist)
            db.session.commit()
            return new_artist
    
    def ensure_or_create_album(self, file_, artist_name, album_name):
        directory, _ = os.path.split(file_)
        # Case 1. we have artist and album name
        if artist_name != None and album_name != None:
            ab = self.find_album_with_artist(artist_name, album_name)
            if ab != None:
                return ab
            else:
                return self.find_album_by_directory(directory, album_name)
        elif album_name != None:
            return self.find_album_by_directory(directory, album_name)
        else:
            if artist_name == None:
                r_artist_name = ''
            else:
                r_artist_name = artist_name
            if album_name == None:
                r_album_name = ''
            else:
                r_album_name = artist_name
            ab = self.find_album_with_artist(r_artist_name, r_album_name)
            if ab != None:
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
        if artist == None:
            return None
        else:
            return Album.query.filter_by(normalized=n_album).filter(Song.albumId==Album.id).filter(Song.artistId==artist.id).first()
    
    def find_album_by_directory(self, directory, album_name):
        n_album = unidecode(unicode(album_name).lower())
        # Try to find a song with name album name
        ab = Album.query.filter_by(normalized=n_album).filter(Song.albumId==Album.id).filter(Song.file.like(directory + '/%')).first()
        if ab != None:
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
            
    def delete_album_if_no_assoc(self, album_id):
        songs = Song.query.filter_by(albumId=album_id).count()
        if songs == 0:
            Album.query.filter_by(id=album_id).delete()
            db.session.commit()
    
    def add_to_database(self, filename, song_info):
        # Just in case anything is None
        if song_info.title == None:
            r_title = ''
        else:
            r_title = song_info.title
        if song_info.artist == None:
            r_artist_name = ''
        else:
            r_artist_name = song_info.artist
        if song_info.album == None:
            r_album_name = ''
        else:
            r_album_name = song_info.album
        if song_info.length == None:
            r_length = 0
        else:
            r_length = song_info.length
        self.get_logger().error('Adding to database: %s by %s in %s', r_title, r_artist_name, r_album_name)
        # Ensure artist
        artist = self.ensure_or_create_artist(r_artist_name)
        ensure_artist_image(artist.id, r_artist_name)
        # Ensure album
        album = self.ensure_or_create_album(filename, r_artist_name, r_album_name)
        ensure_album_image(album.id, filename, r_album_name, r_artist_name)
        # Create song in database
        song = Song(filename, r_title, artist.id, album.id, r_length, song_info.year, song_info.track, song_info.disc_no)
        db.session.add(song)
        db.session.commit()
    
    def replace_in_database(self, filename, s, song_info):
        # Just in case anything is None
        if song_info.title == None:
            r_title = ''
        else:
            r_title = song_info.title
        if song_info.artist == None:
            r_artist_name = ''
        else:
            r_artist_name = song_info.artist
        if song_info.album == None:
            r_album_name = ''
        else:
            r_album_name = song_info.album
        if song_info.length == None:
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
        album = self.ensure_or_create_album(filename, r_artist_name, r_album_name)
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