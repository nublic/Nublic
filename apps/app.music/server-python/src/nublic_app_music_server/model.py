from unidecode import unidecode
from nublic_server.sqlalchemyext import SQLAlchemy

# Create database to base the model
db = SQLAlchemy()

class Artist(db.Model):
    __tablename__ = 'Artist'
    
    id = db.Column(db.BigInteger, primary_key=True)
    name = db.Column(db.UnicodeText)
    normalized = db.Column(db.Text)
    
    def __init__(self, name):
        self.name = name
        self.normalized = unidecode.unidecode(unicode(name).lower())
    
    songs = db.relationship('Song', backref='artist', lazy='dynamic')

def artist_as_json((id_, name, songs, albums)):
    return { 'id': id_,
             'name': name,
             'songs': songs,
             'albums': albums
           }

def artists_and_row_count_as_json(row_count, artists):
    return { 'row_count': row_count, 'artists': map(artist_as_json, artists) }

def get_or_create_artist(artist_name):
    normalized_name = unidecode.unidecode(unicode(artist_name).lower())
    a = Artist.query.filter_by(normalized=normalized_name).first()
    if a == None:
        a = Artist(artist_name)
        db.session.add(a)
        db.session.commit()
    return a

class Album(db.Model):
    __tablename__ = 'Album'
    
    id = db.Column(db.BigInteger, primary_key=True)
    name = db.Column(db.UnicodeText)
    normalized = db.Column(db.Text)
    
    def __init__(self, name):
        self.name = name
        self.normalized = unidecode.unidecode(unicode(name).lower())
    
    songs = db.relationship('Song', backref='album', lazy='dynamic')

def album_as_json((id_, name, songs, artists)):
    return { 'id': id_,
             'name': name,
             'songs': songs,
             'artists': artists
        }

def albums_and_row_count_as_json(row_count, albums):
    return { 'row_count': row_count, 'albums': map(album_as_json, albums) }

def get_or_create_album(album_name):
    normalized_name = unidecode.unidecode(unicode(album_name).lower())
    a = Album.query.filter_by(normalized=normalized_name).first()
    if a == None:
        a = Album(album_name)
        db.session.add(a)
        db.session.commit()
    return a

class Song(db.Model):
    __tablename__ = 'Song'
    
    id = db.Column(db.BigInteger, primary_key=True)
    file = db.Column(db.Unicode)
    title = db.Column(db.UnicodeText)
    artistId = db.Column(db.BigInteger, db.ForeignKey('Artist.id'))
    albumId = db.Column(db.BigInteger, db.ForeignKey('Album.id'))
    length = db.Column(db.Integer)
    year = db.Column(db.Integer)
    track = db.Column(db.Integer)
    disc_no = db.Column(db.Integer)
    owner = db.Column(db.String(255))
    shared = db.Column(db.Boolean)
    
    def __init__(self, file_, title, artistId, albumId, length, year, track, disc_no):
        self.file = file_
        self.title = title
        self.artistId = artistId
        self.albumId = albumId
        self.length = length
        self.year = year
        self.track = track
        self.disc_no = disc_no

def song_by_filename(filename):
    Song.query.filter_by(file=filename).first()

def song_as_json(song):
    o = { 'id': song.id,
          'title': song.title,
          'artist_id': song.artistId,
          'album_id': song.albumId
        }
    if song.length != None:
        o['length'] = song.length
    if song.track != None:
        o['track'] = song.track
    if song.disc_no != None:
        o['disc_no'] = song.disc_no
    return o

def songs_and_row_count_as_json(row_count, songs):
    return { 'row_count': row_count, 'songs': map(song_as_json, songs) }

class Collection(db.Model):
    __tablename__ = 'Collection'
    
    id = db.Column(db.BigInteger, primary_key=True)
    name = db.Column(db.UnicodeText)
    
    def __init__(self, name):
        self.name = name

class SongCollection(db.Model):
    __tablename__ = 'SongCollection'
    
    songId = db.Column('songId', db.BigInteger, db.ForeignKey('Song.id'), primary_key=True)
    collectionId = db.Column('collectionId', db.BigInteger, db.ForeignKey('Collection.id'), primary_key=True)
    
    def __init__(self, collectionId, songId):
        self.songId = songId
        self.collectionId = collectionId
    
    song = db.relationship(Song, backref='collections')
    collection = db.relationship(Collection, backref='songs')

class Playlist(db.Model):
    __tablename__ = 'Playlist'
    
    id = db.Column(db.BigInteger, primary_key=True)
    name = db.Column(db.UnicodeText)
    
    def __init__(self, name):
        self.name = name

class SongPlaylist(db.Model):
    __tablename__ = 'SongPlaylist'
    
    id = db.Column(db.BigInteger, primary_key=True)
    songId = db.Column('songId', db.BigInteger, db.ForeignKey('Song.id'))
    playlistId = db.Column('playlistId', db.BigInteger, db.ForeignKey('Playlist.id'))
    position = db.Column(db.BigInteger)
    
    def __init__(self, playlistId, songId, position):
        self.songId = songId
        self.playlistId = playlistId
        self.position = position
    
    song = db.relationship(Song, backref='playlists')
    playlist = db.relationship(Playlist, backref='songs')

def collection_or_playlist_as_json(c):
    return { 'id': c.id,
             'name': c.name
        }
