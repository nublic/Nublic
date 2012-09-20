from flask import Flask, request, abort, send_file
import os.path
#import random
import simplejson as json
from sqlalchemy.sql.expression import func

#from nublic.files_and_users import User
from nublic_server.helpers import init_nublic_server, split_reasonable, require_user
from nublic_server.places import get_cache_folder

from model import db, Album, Artist, Collection, Playlist, Song, SongCollection, SongPlaylist, \
    collection_or_playlist_as_json, album_as_json, artist_as_json,\
    artists_and_row_count_as_json, albums_and_row_count_as_json,\
    song_as_json, songs_and_row_count_as_json
from music_watcher import MusicProcessor
from images import get_artist_folder, get_album_folder, THUMBNAIL_FILENAME

# Init app
app = Flask(__name__)
app.debug = True
init_nublic_server(app, '/var/log/nublic/nublic-app-music.python.log', 'nublic_app_music',
                   db, 'music', [lambda w: MusicProcessor.start(app.logger, w)])
app.logger.error('Starting music app')

# COLLECTIONS HANDLING
# ====================

@app.route('/collections', methods=['GET', 'PUT', 'DELETE'])
def collections():
    require_user()
    if request.method == 'GET':
        return collections_get()
    elif request.method == 'PUT':
        return collections_put()
    elif request.method == 'DELETE':
        return collections_delete()

def collections_get():
    colls = Collection.query.order_by(func.lower(Collection.name)).all()
    return json.dumps(colls, default=collection_or_playlist_as_json)

def collections_put():
    name = request.form.get('name', None)
    new_coll = Collection(name)
    db.session.add(new_coll)
    db.session.commit()
    return str(new_coll.id)

def collections_delete():
    id_ = request.form.get('id', None)
    id_as_int = int(id_)
    coll = Collection.query.get(id_as_int)
    if coll != None:
        SongCollection.query.filter_by(collectionId=id_as_int).delete()
        db.session.delete(coll)
        db.session.commit()
    return 'ok'

@app.route('/collection/<int:collection_id>', methods=['PUT', 'DELETE'])
def collection(collection_id):
    require_user()
    collection = Collection.query.get_or_404(collection_id)
    if request.method == 'PUT':
        return one_collection_put(collection_id)
    elif request.method == 'DELETE':
        return one_collection_delete(collection_id)

def one_collection_put(collection_id):
    ids = split_reasonable(request.form.get('songs', None), ',')
    ids_as_ints = map(lambda s: int(s), ids)
    for id_as_int in ids_as_ints:
        #app.logger.error('Trying to add %s to album %s', str(id_as_int), str(album_id))
        relation = SongCollection.query.filter_by(collectionId=collection_id, songId=id_as_int).first()
        song = Song.query.get(id_as_int)
        if relation == None and song != None:
            relation = SongCollection(collection_id, id_as_int)
            db.session.add(relation)
            db.session.commit()
    return 'ok'

def one_collection_delete(collection_id):
    ids = split_reasonable(request.form.get('songs', None), ',')
    ids_as_ints = map(lambda s: int(s), ids)
    for id_as_int in ids_as_ints:
        SongCollection.query.filter_by(collectionId=collection_id, songId=id_as_int).delete()
    db.session.commit()
    return 'ok'

# PLAYLISTS HANDLING
# ==================

@app.route('/playlists', methods=['GET', 'PUT', 'DELETE'])
def playlists():
    require_user()
    if request.method == 'GET':
        return playlists_get()
    elif request.method == 'PUT':
        return playlists_put()
    elif request.method == 'DELETE':
        return playlists_delete()

def playlists_get():
    ps = Playlist.query.order_by(func.lower(Playlist.name)).all()
    return json.dumps(ps, default=collection_or_playlist_as_json)

def playlists_put():
    name = request.form.get('name', None)
    new_ps = Playlist(name)
    db.session.add(new_ps)
    db.session.commit()
    one_playlist_put(new_ps.id)
    return str(new_ps.id)

def playlists_delete():
    id_ = request.form.get('id', None)
    id_as_int = int(id_)
    ps = Playlist.query.get(id_as_int)
    if ps != None:
        SongPlaylist.query.filter_by(playlistId=id_as_int).delete()
        db.session.delete(ps)
        db.session.commit()
    return 'ok'

@app.route('/playlist/<int:playlist_id>', methods=['PUT', 'DELETE'])
def playlist(playlist_id):
    require_user()
    ps = Playlist.query.get_or_404(playlist_id)
    if request.method == 'PUT':
        return one_playlist_put(playlist_id)
    elif request.method == 'DELETE':
        return one_playlist_delete(playlist_id)

def one_playlist_put(playlist_id):
    ids = split_reasonable(request.form.get('songs', None), ',')
    ids_as_ints = map(lambda s: int(s), ids)
    ps_count = SongPlaylist.query.filter_by(playlistId=playlist_id).count()
    for id_as_int in ids_as_ints:
        song = Song.query.get(id_as_int)
        if song != None:
            relation = SongPlaylist(playlist_id, id_as_int, ps_count)
            db.session.add(relation)
            ps_count += 1
    db.session.commit()
    return 'ok'

def one_playlist_delete(playlist_id):
    position = int(request.form.get('position'))
    relation = SongPlaylist.query.filter_by(playlistId=playlist_id, position=position).first()
    relation.delete()
    if relation != None:
        rest = SongPlaylist.query.filter_by(playlistId=playlist_id).filter(SongPlaylist.position > position).all()
        for r in rest:
            r.position -= 1
    db.session.commit()
    return 'ok'

@app.route('/playlist/order/<int:playlist_id>', methods=['POST'])
def playlist_order(playlist_id):
    require_user()
    ps = Playlist.query.get_or_404(playlist_id)
    from_ = int(request.form.get('from'))
    to_ = int(request.form.get('to'))
    # Check is inside
    ps_count = SongPlaylist.query.filter_by(playlistId=playlist_id).count()
    if from_ < 0 or to_ < 0 or from_ >= ps_count or to_ > ps_count:
        abort(500)
    # Change relations
    relation = SongPlaylist.query.filter_by(playlistId=playlist_id, position=from_).first()
    if relation != None:
        if from_ < to_:
            rest = SongPlaylist.query.filter_by(playlistId=playlist_id).filter(SongPlaylist.position > from_).filter(SongPlaylist.position < to_).all()
            for r in rest:
                if r.id != relation.id:
                    r.position -= 1
            relation.position = to_ - 1
        elif from_ > to_:
            rest = SongPlaylist.query.filter_by(playlistId=playlist_id).filter(SongPlaylist.position >= to_).filter(SongPlaylist.position < from_).all()
            for r in rest:
                if r.id != relation.id:
                    r.position += 1
            relation.position = to_
        db.session.commit()
    return 'ok'

# ARTISTS
# =======

@app.route('/artists')
def artists():
    require_user()
    return artists_get('asc', 0, 20, [])
    
@app.route('/artists/<asc>/<int:start>/<int:length>')
def artists_(asc, start, length):
    require_user()
    return artists_get(asc, start, length, [])

@app.route('/artists/<asc>/<int:start>/<int:length>/')
def artists__(asc, start, length):
    require_user()
    return artists_get(asc, start, length, [])

@app.route('/artists/<asc>/<int:start>/<int:length>/<path:collection_ids>')
def artists_with_colls(asc, start, length, collection_ids):
    require_user()
    ids = split_reasonable(collection_ids, '/')
    ids_as_ints = map(lambda s: int(s), ids)
    return artists_get(asc, start, length, ids_as_ints)

def artists_get(asc, start, length, collection_ids):
    # Get order
    if asc == 'asc':
        asc_desc = lambda x: x
    elif asc == 'desc':
        asc_desc = lambda x: x.desc()
    # Get query
    if not collection_ids:
        query = Artist.query.order_by(asc_desc(Artist.name))
    else:
        query_ = Artist.query.order_by(asc_desc(Artist.name)).filter(Song.artistId==Artist.id).filter(SongCollection.songId==Song.id)
        query = query_.filter(SongCollection.collectionId.in_(collection_ids)).distinct()
    # Get count and offset
    count = query.count()
    q_offset = query.offset(start).limit(length)
    # Generate information about artist
    result = []
    for a in q_offset.all():
        artistInfo = get_artist_info(a)
        if artistInfo != None:
            result.append(artistInfo)
    return json.dumps(artists_and_row_count_as_json(count, result))

@app.route('/artist-info/<int:id>')
def artist_info(id):
    require_user()
    a = Artist.query.get_or_404(id)
    return json.dumps(artist_as_json(get_artist_info(a)))

def get_artist_info(a):
    a_songs = Song.query.filter_by(artistId=a.id).count()
    if a_songs != 0:
        a_albums = Album.query.filter(Song.albumId==Album.id).filter(Song.artistId==a.id).distinct().count()
        return (a.id, a.name, a_songs, a_albums)
    else:
        return None

# ALBUMS
# ======

ALL_OF_SOMETHING = 'all'

@app.route('/albums')
def albums():
    require_user()
    return albums_get(ALL_OF_SOMETHING, 'asc', 0, 20, [])

@app.route('/albums/<artist_id>')
def albums_artist(artist_id):
    require_user()
    return albums_get(artist_id, 'asc', 0, 20, [])

@app.route('/albums/<artist_id>/')
def albums_artist_(artist_id):
    require_user()
    return albums_get(artist_id, 'asc', 0, 20, [])

@app.route('/albums/<artist_id>/<asc>/<int:start>/<int:length>/')
def albums_without_colls(artist_id, asc, start, length):
    require_user()
    return albums_get(artist_id, asc, start, length, [])

@app.route('/albums/<artist_id>/<asc>/<int:start>/<int:length>/<path:collection_ids>')
def albums_with_colls(artist_id, asc, start, length, collection_ids):
    require_user()
    ids = split_reasonable(collection_ids, '/')
    ids_as_ints = map(lambda s: int(s), ids)
    return albums_get(artist_id, asc, start, length, ids_as_ints)

def albums_get(artist, asc, start, length, collection_ids):
    # Get order
    if asc == 'asc':
        asc_desc = lambda x: x
    elif asc == 'desc':
        asc_desc = lambda x: x.desc()
    # Get query
    if artist == ALL_OF_SOMETHING:
        query = Album.query.order_by(asc_desc(Album.name))
    else:
        query = Album.query.order_by(asc_desc(Album.name)).filter(Song.albumId==Album.id).filter(Song.artistId==int(artist))
    if collection_ids:
        query = query.filter(Song.albumId==Album.id).filter(SongCollection.songId==Song.id).filter(SongCollection.collectionId.in_(collection_ids)).distinct()
    # Get count and offset
    count = query.count()
    q_offset = query.offset(start).limit(length)
    # Generate information about artist
    result = []
    for a in q_offset.all():
        albumInfo = get_album_info(a)
        if albumInfo != None:
            result.append(albumInfo)
    return json.dumps(albums_and_row_count_as_json(count, result))

@app.route('/album-info/<int:id>')
def album_info(id):
    require_user()
    a = Album.query.get_or_404(id)
    return json.dumps(album_as_json(get_album_info(a)))

def get_album_info(a):
    a_songs = Song.query.filter_by(albumId=a.id).count()
    if a_songs != 0:
        a_artists = Artist.query.filter(Song.artistId==Artist.id).filter(Song.albumId==a.id).distinct().all()
        return (a.id, a.name, a_songs, map(lambda artist: artist.id, a_artists))
    else:
        return None

# SONGS
# =====

@app.route('/songs')
def songs():
    require_user()
    return songs_get(ALL_OF_SOMETHING, ALL_OF_SOMETHING, 'alpha', 'asc', 0, 20, [])

@app.route('/songs/<artist_id>')
def songs_artist(artist_id):
    require_user()
    return songs_get(artist_id, ALL_OF_SOMETHING, 'alpha', 'asc', 0, 20, [])

@app.route('/songs/<artist_id>/<album_id>')
def songs_artist_album(artist_id, album_id):
    require_user()
    return songs_get(artist_id, album_id, 'alpha', 'asc', 0, 20, [])

@app.route('/songs/<artist_id>/<album_id>/')
def songs_artist_album_(artist_id, album_id):
    require_user()
    return songs_get(artist_id, album_id, 'alpha', 'asc', 0, 20, [])

@app.route('/songs/<artist_id>/<album_id>/<order>/<asc>/<int:start>/<int:length>')
def songs_almost_all(artist_id, album_id, order, asc, start, length):
    require_user()
    return songs_get(artist_id, album_id, order, asc, start, length, [])

@app.route('/songs/<artist_id>/<album_id>/<order>/<asc>/<int:start>/<int:length>/')
def songs_almost_all_(artist_id, album_id, order, asc, start, length):
    require_user()
    return songs_get(artist_id, album_id, order, asc, start, length, [])

@app.route('/songs/<artist_id>/<album_id>/<order>/<asc>/<int:start>/<int:length>/<path:collection_ids>')
def songs_with_collections(artist_id, album_id, order, asc, start, length, collection_ids):
    require_user()
    ids = split_reasonable(collection_ids, '/')
    ids_as_ints = map(lambda s: int(s), ids)
    return songs_get(artist_id, album_id, order, asc, start, length, ids_as_ints)

def songs_get(artist_id, album_id, order, asc, start, length, collections):
    # Get order
    if asc == 'asc':
        asc_desc = lambda x: x
    elif asc == 'desc':
        asc_desc = lambda x: x.desc()
    # Different orders
    if order == 'alpha':
        order_q = lambda q: q.order_by(asc_desc(Song.title))
    elif order == 'album':
        order_q = lambda q: q.order_by(asc_desc(Album.name)).order_by(asc_desc(Song.disc_no)).order_by(asc_desc(Song.track))
    elif order == 'artist_alpha':
        order_q = lambda q: q.order_by(asc_desc(Artist.name)).order_by(asc_desc(Song.title))
    elif order == 'artist_album':
        order_q = lambda q: q.order_by(asc_desc(Artist.name)).order_by(asc_desc(Album.name)).order_by(asc_desc(Song.disc_no)).order_by(asc_desc(Song.track))
    # Create initial query
    query = Song.query.join(Album).join(Artist)
    if collections:
        query = query.join(SongCollection).filter(SongCollection.collectionId.in_(collections))
    # Apply artist and album
    if artist_id != ALL_OF_SOMETHING:
        query = query.filter(Song.artistId==int(artist_id))
    if album_id != ALL_OF_SOMETHING:
        query = query.filter(Song.albumId==int(album_id))
    # Finally, apply order
    query = order_q(query).distinct()
    # Get results
    count = query.count()
    songs = query.offset(start).limit(length).all()
    return json.dumps(songs_and_row_count_as_json(count, songs))

@app.route('/song-info/<int:song_id>')
def song_info(song_id):
    require_user()
    song = Song.query.get_or_404(song_id)
    return json.dumps(song_as_json(song))

@app.route('/playlist/<int:ps_id>/<order>/<asc>/<int:start>/<int:length>')
def playlist_songs(ps_id, order, asc, start, length):
    require_user()
    # Get order
    if asc == 'asc':
        asc_desc = lambda x: x
    elif asc == 'desc':
        asc_desc = lambda x: x.desc()
    # Different orders
    if order == 'playlist':
        order_q = lambda q: q.order_by(asc_desc(SongPlaylist.position))
    elif order == 'alpha':
        order_q = lambda q: q.order_by(asc_desc(Song.title))
    elif order == 'album':
        order_q = lambda q: q.order_by(asc_desc(Album.name)).order_by(asc_desc(Song.disc_no)).order_by(asc_desc(Song.track))
    elif order == 'artist_alpha':
        order_q = lambda q: q.order_by(asc_desc(Artist.name)).order_by(asc_desc(Song.title))
    elif order == 'artist_album':
        order_q = lambda q: q.order_by(asc_desc(Artist.name)).order_by(asc_desc(Album.name)).order_by(asc_desc(Song.disc_no)).order_by(asc_desc(Song.track))
    # Create initial query
    query = SongPlaylist.query.join(Song).join(Album).join(Artist).filter(SongPlaylist.playlistId==ps_id)
    # Finally, apply order
    query = order_q(query)
    # Get results
    count = query.count()
    songs = map(lambda sr: sr.song, query.offset(start).limit(length).all())
    return json.dumps(songs_and_row_count_as_json(count, songs))

# IMAGES
# ======

@app.route('/artist-art/<int:artist_id>.png')
def artist_art(artist_id):
    require_user()
    path = os.path.join(get_artist_folder(artist_id), THUMBNAIL_FILENAME)
    if os.path.exists(path):
        return send_file(path)
    else:
        abort(404)

@app.route('/album-art/<int:album_id>.png')
def album_art(album_id):
    require_user()
    path = os.path.join(get_album_folder(album_id), THUMBNAIL_FILENAME)
    if os.path.exists(path):
        return send_file(path)
    else:
        abort(404)

# SONG FILES
# ==========

@app.route('/raw/<int:song_id>.mp3')
def raw_song(song_id):
    require_user()
    song = Song.query.get_or_404(song_id)
    return send_file(song.file)

@app.route('/view/<int:song_id>.mp3')
def view_song(song_id):
    require_user()
    app.logger.error('Getting song %i', song_id)
    song = Song.query.get_or_404(song_id)
    mp3_file = os.path.join(get_cache_folder(song.file), 'audio.mp3')
    app.logger.error('Getting file %s', mp3_file)
    if os.path.exists(mp3_file):
        return send_file(mp3_file)
    else:
        abort(404)

if __name__ == '__main__':
    app.run()

