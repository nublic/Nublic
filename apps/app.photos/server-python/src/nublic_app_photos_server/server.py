from nublic.filewatcher import init_watcher, Processor
from nublic.files_and_users import get_all_mirrors, User
from nublic.resource import App

from model import db, Photo

import logging
#import sys

# Init app
from flask import Flask, request
app = Flask(__name__)

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
db.app = app
db.init_app(app)
db.create_all()

# Set up processors
class NothingProcessor(Processor):
    def __init__(self, logger, watcher):
        Processor.__init__(self, 'nothing', watcher, False, logger)

    def process(self, change):
        app.logger.error('Nothing processor: %s', change)
        
init_watcher('Photos', [lambda w: NothingProcessor.start(app.logger, w)], app.logger)

app.logger.error('Starting photos app')

@app.route('/')
def hello_world():
    auth = request.authorization
    user = User(auth.username)
    return 'Hello ' + user.get_shown_name()

@app.route('/mirrors')
def mirrors():
    return str(get_all_mirrors())

@app.route('/test')
def test():
    auth = request.authorization
    user = User(auth.username)
    return str(user.can_read('/var/nublic/data/nublic-only/fotos'))

@app.route('/photos')
def photos():
    return repr(Photo.query.order_by(Photo.title).all())

if __name__ == '__main__':
    app.run()