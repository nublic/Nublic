from flask import Flask, request, abort, send_file
import random
import simplejson as json
from sqlalchemy.sql.expression import func

from nublic.files_and_users import User
from nublic_server.helpers import init_nublic_server, split_reasonable, require_user
from nublic_server.places import get_cache_folder

from model import db, Album, Artist, Collection, Playlist, Song, SongCollection, SongPlaylist, \
    collection_or_playlist_as_json, album_as_json, artist_as_json
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
        if relation == None:
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
    if request.method == 'PUT':
        return one_playlist_put(playlist_id)
    elif request.method == 'DELETE':
        return one_playlist_delete(playlist_id)

def one_playlist_put(playlist_id):
    ids = split_reasonable(request.form.get('songs', None), ',')
    ids_as_ints = map(lambda s: int(s), ids)
    for id_as_int in ids_as_ints:
        #app.logger.error('Trying to add %s to album %s', str(id_as_int), str(album_id))
        relation = SongCollection.query.filter_by(collectionId=collection_id, songId=id_as_int).first()
        if relation == None:
            relation = SongCollection(collection_id, id_as_int)
            db.session.add(relation)
            db.session.commit()
    return 'ok'

def one_playlist_delete(playlist_id):
    ids = split_reasonable(request.form.get('songs', None), ',')
    ids_as_ints = map(lambda s: int(s), ids)
    for id_as_int in ids_as_ints:
        SongCollection.query.filter_by(collectionId=collection_id, songId=id_as_int).delete()
    db.session.commit()
    return 'ok'

if __name__ == '__main__':
    app.run()