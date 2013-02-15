from flask import Flask, request  # , abort, send_file
#import os.path
#import random
import simplejson as json
##from sqlalchemy.sql.expression import func

import requests

#from nublic.files_and_users import User
from nublic_server.helpers import init_bare_nublic_server
#from nublic_server.places import get_cache_folder
from nublic.files_and_users.user import User, get_all_users

#from model import db, Album, Artist, Collection, Playlist, Song, SongCollection, SongPlaylist, \
#    collection_or_playlist_as_json, album_as_json, artist_as_json,\
#    artists_and_row_count_as_json, albums_and_row_count_as_json,\
#    song_as_json, songs_and_row_count_as_json

# Init app
app = Flask(__name__)
app.debug = True
init_bare_nublic_server(app, '/var/log/nublic/nublic-app-init.python.log')
app.logger.error('Starting init app')


@app.route('/password/')
def master_password():
    return 'ThisIsAPasswordExample2'


@app.route('/checknublicname/<name>')
def check_name(name):
    # change to nublic url, encode <name>
    r = requests.get('http://127.0.0.1/' + name)
    # or return it directly...
    if not r.text == 'ok':
        return 'exists'
    else:
        return 'ok'

if __name__ == '__main__':
    app.run()


