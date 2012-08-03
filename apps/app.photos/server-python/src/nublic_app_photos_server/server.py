from flask import Flask, Request, request, abort, send_file
import logging
import random
import simplejson as json
from sqlalchemy.sql.expression import func

from nublic.filewatcher import init_watcher
from nublic.files_and_users import User
from nublic.resource import App
from nublic_server.places import get_cache_folder

from model import db, Photo, Album, PhotoAlbum, photo_as_json, album_as_json, photos_and_row_count_as_json
from photo_watcher import PhotoProcessor

# Init app
app = Flask(__name__)
app.debug = True

class RequestWithDelete(Request):
    @property
    def want_form_data_parsed(self):
        return self.environ['REQUEST_METHOD'] in ['DELETE', 'PUT', 'POST']

app.request_class = RequestWithDelete

# Set up logging handlers
handler = logging.FileHandler('/var/log/nublic/nublic-app-photos.python.log')
handler.setFormatter(logging.Formatter(
    '%(asctime)s %(levelname)s: %(message)s '
    '[in %(pathname)s:%(lineno)d]'
))
app.logger.addHandler(handler)

# Get resource information
res_app = App('nublic_app_photos')
res_key = res_app.get('db')
db_uri = 'postgresql://' + res_key.value('user') + ':' + res_key.value('pass') + \
         '@localhost:5432/' + res_key.value('database')

# Init DB
app.config['SQLALCHEMY_DATABASE_URI'] = db_uri
db.init_app(app)
db.create_all(app=app)

# Init watching
init_watcher('Photos', [lambda w: PhotoProcessor.start(app.logger, w)], app.logger)

app.logger.error('Starting photos app')

def split_reasonable(s, separator):
    if s == None:
        return []
    else:
        return filter(lambda st: st != '', s.split(separator))

#@app.route('/')
#def hello_world():
#    auth = request.authorization
#    user = User(auth.username)
#    return 'Hello ' + user.get_shown_name()

def require_user():
    auth = request.authorization
    user = User(auth.username)
    if not user.exists():
        abort(401)
    return user

@app.route('/albums', methods=['GET', 'PUT', 'DELETE'])
def albums():
    require_user()
    if request.method == 'GET':
        return albums_get()
    elif request.method == 'PUT':
        return albums_put()
    elif request.method == 'DELETE':
        return albums_delete()

def albums_get():
    albums = Album.query.order_by(func.lower(Album.name)).all()
    return json.dumps(albums, default=album_as_json)

def albums_put():
    name = request.form.get('name', None)
    new_album = Album(name)
    db.session.add(new_album)
    db.session.commit()
    return str(new_album.id)

def albums_delete():
    id_ = request.form.get('id', None)
    id_as_int = int(id_)
    album = Album.query.get(id_as_int)
    if album != None:
        PhotoAlbum.query.filter_by(albumId=id_as_int).delete()
        db.session.delete(album)
        db.session.commit()
    return 'ok'

@app.route('/album/<int:album_id>', methods=['PUT', 'DELETE'])
def album(album_id):
    if request.method == 'PUT':
        return one_album_put(album_id)
    elif request.method == 'DELETE':
        return one_album_delete(album_id)

def one_album_put(album_id):
    ids = split_reasonable(request.form.get('photos', None), ',')
    ids_as_ints = map(lambda s: int(s), ids)
    for id_as_int in ids_as_ints:
        #app.logger.error('Trying to add %s to album %s', str(id_as_int), str(album_id))
        relation = PhotoAlbum.query.filter_by(albumId=album_id, photoId=id_as_int).first()
        if relation == None:
            relation = PhotoAlbum(album_id, id_as_int)
            db.session.add(relation)
            db.session.commit()
    return 'ok'

def one_album_delete(album_id):
    ids = split_reasonable(request.form.get('photos', None), ',')
    ids_as_ints = map(lambda s: int(s), ids)
    for id_as_int in ids_as_ints:
        PhotoAlbum.query.filter_by(albumId=album_id, photoId=id_as_int).delete()
    db.session.commit()
    return 'ok'

@app.route('/photos')
def photos():
    require_user()
    return photos_get('title', 'asc', 0, 20, [])
    
@app.route('/photos/')
def photos_():
    require_user()
    return photos_get('title', 'asc', 0, 20, [])
    
@app.route('/photos/<order>/<asc>/<int:start>/<int:length>')
def photos_without_albums(order, asc, start, length):
    require_user()
    return photos_get(order, asc, start, length, [])

@app.route('/photos/<order>/<asc>/<int:start>/<int:length>/')
def photos_without_albums_(order, asc, start, length):
    require_user()
    return photos_get(order, asc, start, length, [])

@app.route('/photos/<order>/<asc>/<int:start>/<int:length>/<path:album_ids>')
def photos_with_albums(order, asc, start, length, album_ids):
    require_user
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
        order_by = lambda s: s.order_by(asc_desc(Photo.date), asc_desc(Photo.title))
    # Get query
    if not album_ids:
        query = order_by(Photo.query)
    else:
        query = order_by(Photo.query.join(PhotoAlbum).filter(PhotoAlbum.albumId.in_(album_ids)))
    # Paginate the results
    query_offset = query.offset(start).limit(length)
    return json.dumps(photos_and_row_count_as_json(query.count(), query_offset.all()))

@app.route('/photo-info/<int:photo_id>')
def photo_info(photo_id):
    require_user()
    photo = Photo.query.get_or_404(photo_id)
    return json.dumps(photo, default=photo_as_json)

@app.route('/photo-title/<int:photo_id>', methods=['POST'])
def photo_title(photo_id):
    require_user()
    photo = Photo.query.get_or_404(photo_id)
    photo.title = request.form['title']
    db.session.commit()
    return 'ok'

@app.route('/raw/<int:photo_id>')
def photo_raw(photo_id):
    require_user()
    photo = Photo.query.get_or_404(photo_id)
    return send_file(photo.file)

THUMBNAIL_FILENAME = 'thumbnail.png'
IMAGE_FILENAME = 'image.png'

@app.route('/view/<int:photo_id>.png')
def photo_view(photo_id):
    require_user()
    photo = Photo.query.get_or_404(photo_id)
    return send_file(get_cache_folder(photo.file) + '/' + IMAGE_FILENAME)

@app.route('/thumbnail/<int:photo_id>.png')
def photo_thumbnail(photo_id):
    require_user()
    photo = Photo.query.get_or_404(photo_id)
    return send_file(get_cache_folder(photo.file) + '/' + THUMBNAIL_FILENAME)

@app.route('/random/<time>/<int:album_id>.png')
def random_album_thumbnail(time, album_id):
    require_user()
    number_photos = PhotoAlbum.query.filter_by(albumId=album_id).count()
    if number_photos > 0:
        n = random.randint(0, number_photos - 1)
        relation = PhotoAlbum.query.filter_by(albumId=album_id).offset(n).first()
        photo = Photo.query.get(relation.photoId)
        return send_file(get_cache_folder(photo.file) + '/' + THUMBNAIL_FILENAME)
    else:
        abort(404)

@app.route('/random/<int:album_id>.png')
def random_album_thumbnail_(album_id):
    return random_album_thumbnail(0, album_id)

if __name__ == '__main__':
    app.run()