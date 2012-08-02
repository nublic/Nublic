from nublic.filewatcher import init_watcher, Processor
from nublic.files_and_users import User
from nublic.resource import App

from model import db, Photo, Album, photoAlbums, photo_as_json, album_as_json

import logging
#import sys
import simplejson as json
from sqlalchemy.sql.expression import func

# Init app
from flask import Flask, Request, request, abort
app = Flask(__name__)
app.debug = True

class RequestWithDelete(Request):
    @property
    def want_form_data_parsed(self):
        return self.environ['REQUEST_METHOD'] == 'DELETE' or Request.want_form_data_parsed()

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
#db.app = app
db.init_app(app)
db.create_all(app=app)

# Set up processors
class NothingProcessor(Processor):
    def __init__(self, logger, watcher):
        Processor.__init__(self, 'nothing', watcher, False, logger)

    def process(self, change):
        app.logger.error('Nothing processor: %s', change)
        
init_watcher('Photos', [lambda w: NothingProcessor.start(app.logger, w)], app.logger)

app.logger.error('Starting photos app')

def split_reasonable(s):
    if s == None:
        return []
    else:
        return filter(lambda st: st != '', s.split(','))

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
        photoAlbums.delete(photoAlbums.c.albumId==id_as_int)
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
    ids = split_reasonable(request.form.get('id', None))
    ids_as_ints = map(lambda s: int(s), ids)

def one_album_delete(id_):

@app.route('/photos')
def photos():
    app.logger.error("%s", Photo.query.first())
    app.logger.error("%s", Photo.query.all())
    return json.dumps(Photo.query.all(), default=photo_as_json)

if __name__ == '__main__':
    app.run()