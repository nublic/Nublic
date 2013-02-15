import os
os.environ['APP_MUSIC_SETTINGS'] = '/etc/nublic/nublic_app_music.cfg'
os.environ['APP_MUSIC_SETTINGS_DB'] = '/etc/nublic/nublic_app_music_db.cfg'
from nublic_app_music_server.server import app as application
