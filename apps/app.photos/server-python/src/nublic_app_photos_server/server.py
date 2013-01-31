from flask import Flask, request, abort, send_file
import random
import simplejson as json
from sqlalchemy.sql.expression import func

#from nublic.files_and_users import User
from nublic_server.helpers import init_nublic_server, split_reasonable
from nublic_server.places import get_cache_folder

from model import db, Photo, Album, PhotoAlbum, photo_as_json, album_as_json, photos_and_row_count_as_json
from photo_watcher import PhotoProcessor

# Init app
app = Flask(__name__)
app.debug = True
init_nublic_server(
    app, '/var/log/nublic/nublic-app-photos.python.log', 'nublic_app_photos',
    db, 'photos', [lambda w: PhotoProcessor.start(app.logger, w)])
app.logger.error('Starting photos app')


@app.route('/albums', methods=['GET'])
def albums_get():
    albums = Album.query.order_by(func.lower(Album.name)).all()
    return json.dumps(albums, default=album_as_json)


@app.route('/albums', methods=['PUT'])
def albums_put():
    name = request.form.get('name', None)
    new_album = Album(name)
    db.session.add(new_album)
    db.session.commit()
    return str(new_album.id)


@app.route('/albums', methods=['DELETE'])
def albums_delete():
    id_ = request.form.get('id', None)
    id_as_int = int(id_)
    album = Album.query.get(id_as_int)
    if album is not None:
        PhotoAlbum.query.filter_by(albumId=id_as_int).delete()
        db.session.delete(album)
        db.session.commit()
    return 'ok'


@app.route('/albums/<int:photo_id>')
def photo_album(photo_id):
    albums = Album.query.join(PhotoAlbum).filter_by(
        photoId=photo_id).order_by(func.lower(Album.name)).all()
    return json.dumps(albums, default=album_as_json)


@app.route('/album/<int:album_id>', methods=['PUT', 'DELETE'])
def album(album_id):
    album = Album.query.filter_by(id=album_id).first()
    if album is None:
        abort(404)
    if request.method == 'PUT':
        return one_album_put(album_id)
    elif request.method == 'DELETE':
        return one_album_delete(album_id)


def one_album_put(album_id):
    ids = split_reasonable(request.form.get('photos', None), ',')
    ids_as_ints = map(lambda s: int(s), ids)
    for id_as_int in ids_as_ints:
        #app.logger.error('Trying to add %s to album %s', str(id_as_int), str(album_id))
        relation = PhotoAlbum.query.filter_by(
            albumId=album_id, photoId=id_as_int).first()
        if relation is None:
            relation = PhotoAlbum(album_id, id_as_int)
            db.session.add(relation)
            db.session.commit()
    return 'ok'


def one_album_delete(album_id):
    ids = split_reasonable(request.form.get('photos', None), ',')
    ids_as_ints = map(lambda s: int(s), ids)
    for id_as_int in ids_as_ints:
        PhotoAlbum.query.filter_by(
            albumId=album_id, photoId=id_as_int).delete()
    db.session.commit()
    return 'ok'


@app.route('/photos/')
def photos():
    return photos_get('title', 'asc', 0, 20, [])

# If a route has a trailing / then both with and without will work!
#@app.route('/photos')
#def photos_():
#    return photos_get('title', 'asc', 0, 20, [])


@app.route('/photos/<order>/<asc>/<int:start>/<int:length>')
def photos_without_albums(order, asc, start, length):
    return photos_get(order, asc, start, length, [])


@app.route('/photos/<order>/<asc>/<int:start>/<int:length>/')
def photos_without_albums_(order, asc, start, length):
    return photos_get(order, asc, start, length, [])


@app.route('/photos/<order>/<asc>/<int:start>/<int:length>/<path:album_ids>')
def photos_with_albums(order, asc, start, length, album_ids):
    ids = split_reasonable(album_ids, '/')
    ids_as_ints = map(lambda s: int(s), ids)
    return photos_get(order, asc, start, length, ids_as_ints)


def photos_get(order, asc, start, length, album_ids):
    # Get order
    if asc == 'asc':
        asc_desc = lambda x: x
    elif asc == 'desc':
        asc_desc = lambda x: x.desc()
    if order == 'title':
        order_by = lambda s: s.order_by(asc_desc(Photo.title))
    elif order == 'date':
        order_by = lambda s: s.order_by(
            asc_desc(Photo.date), asc_desc(Photo.title))
    # Get query
    if not album_ids:
        query = order_by(Photo.query)
    else:
        query = order_by(Photo.query.join(
            PhotoAlbum).filter(PhotoAlbum.albumId.in_(album_ids)))
    # Paginate the results
    query_offset = query.offset(start).limit(length)
    return json.dumps(photos_and_row_count_as_json(query.count(), query_offset.all()))


@app.route('/photo-info/<int:photo_id>')
def photo_info(photo_id):
    photo = Photo.query.get_or_404(photo_id)
    return json.dumps(photo, default=photo_as_json)


@app.route('/photo-title/<int:photo_id>', methods=['POST'])
def photo_title(photo_id):
    photo = Photo.query.get_or_404(photo_id)
    photo.title = request.form['title']
    db.session.commit()
    return 'ok'


@app.route('/raw/<int:photo_id>')
def photo_raw(photo_id):
    photo = Photo.query.get_or_404(photo_id)
    return send_file(photo.file)

THUMBNAIL_FILENAME = 'thumbnail.png'
IMAGE_FILENAME = 'image.png'


@app.route('/view/<int:photo_id>.png')
def photo_view(photo_id):
    photo = Photo.query.get_or_404(photo_id)
    return send_file(get_cache_folder(photo.file) + '/' + IMAGE_FILENAME)


@app.route('/thumbnail/<int:photo_id>.png')
def photo_thumbnail(photo_id):
    photo = Photo.query.get_or_404(photo_id)
    return send_file(get_cache_folder(photo.file) + '/' + THUMBNAIL_FILENAME)


@app.route('/random/<time>/<int:album_id>.png')
def random_album_thumbnail(time, album_id):
    number_photos = PhotoAlbum.query.filter_by(albumId=album_id).count()
    if number_photos > 0:
        n = random.randint(0, number_photos - 1)
        relation = PhotoAlbum.query.filter_by(
            albumId=album_id).offset(n).first()
        photo = Photo.query.get(relation.photoId)
        return send_file(get_cache_folder(photo.file) + '/' + THUMBNAIL_FILENAME)
    else:
        abort(404)


@app.route('/random/<int:album_id>.png')
def random_album_thumbnail_(album_id):
    return random_album_thumbnail(0, album_id)

if __name__ == '__main__':
    app.run()
