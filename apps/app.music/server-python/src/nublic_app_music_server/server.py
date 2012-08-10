from flask import Flask, request, abort, send_file
import random
import simplejson as json
from sqlalchemy.sql.expression import func

from nublic.files_and_users import User
from nublic_server.helpers import init_nublic_server, split_reasonable, require_user
from nublic_server.places import get_cache_folder

from model import db, Album, Artist, Collection, Playlist, Song, SongCollection, SongPlaylist, \
    collection_or_playlist_as_json, album_as_json, artist_as_json,\
    artists_and_row_count_as_json, albums_and_row_count_as_json
from music_watcher import MusicProcessor

# Init app
app = Flask(__name__)
app.debug = True
init_nublic_server(app, '/var/log/nublic/nublic-app-music.python.log', 'nublic_app_music',
                   db, 'photos', [lambda w: MusicProcessor.start(app.logger, w)])
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
    collection = Collection.query.filter_by(collectionId=collection_id).first()
    if collection == None:
        abort(404)
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
        song = Song.query.filter_by(songId=id_as_int).first()
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
    ps = Playlist.query.filter_by(playlistId=playlist_id).first()
    if ps == None:
        abort(404)
    if request.method == 'PUT':
        return one_playlist_put(playlist_id)
    elif request.method == 'DELETE':
        return one_playlist_delete(playlist_id)

def one_playlist_put(playlist_id):
    ids = split_reasonable(request.form.get('songs', None), ',')
    ids_as_ints = map(lambda s: int(s), ids)
    ps_count = SongPlaylist.query.filter_by(playlistId=playlist_id).count()
    for id_as_int in ids_as_ints:
        song = Song.query.filter_by(songId=id_as_int).first()
        if song != None:
            ps_count += 1
            relation = SongPlaylist(playlist_id, id_as_int, ps_count)
            db.session.add(relation)
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
    ps = Playlist.query.filter_by(playlistId=playlist_id).first()
    if ps == None:
        abort(404)
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
            rest = SongPlaylist.query.filter_by(playlistId=playlist_id).filter(SongPlaylist.position > from_, SongPlaylist.position < to_).all()
            for r in rest:
                r.position -= 1
            relation.position = to_ - 1
        elif from_ > to_:
            rest = SongPlaylist.query.filter_by(playlistId=playlist_id).filter(SongPlaylist.position >= to_, SongPlaylist.position < from_).all()
            for r in rest:
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
        result.append(get_artist_info(a))
    return json.dumps(artists_and_row_count_as_json(count, result))

@app.route('/artist-info/<int:id>')
def artist_info(id):
    require_user()
    a = Artist.query.get_or_404(id)
    return artist_as_json(get_artist_info(a))

def get_artist_info(a):
    a_songs = Song.query.filter_by(artistId=a.id).count()
    a_albums = Album.query.filter(Song.albumId==Album.id).filter(Song.artistId==a.id).distinct().count()
    return (a.id, a.name, a_songs, a_albums)

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

@app.route('/albums/<artist_id>/<asc>/<int:start>/<int:length>/<path:collection_ids>')
def albums_with_colls(asc, start, length, collection_ids):
    require_user()
    ids = split_reasonable(collection_ids, '/')
    ids_as_ints = map(lambda s: int(s), ids)
    return artists_get(asc, start, length, ids_as_ints)

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
        result.append(get_album_info(a))
    return json.dumps(albums_and_row_count_as_json(count, result))

@app.route('/album-info/<int:id>')
def album_info(id):
    require_user()
    a = Album.query.get_or_404(id)
    return album_as_json(get_album_info(a))

def get_album_info(a):
    a_songs = Song.query.filter_by(albumId=a.id).count()
    a_artists = Artist.query.filter(Song.artistId==Artist.id).filter(Song.albumId==a.id).distinct().all()
    return (a.id, a.name, a_songs, map(lambda artist: artist.id, a_artists))

if __name__ == '__main__':
    app.run()