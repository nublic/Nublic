import os
os.environ['APP_PHOTOS_SETTINGS'] = '/etc/nublic/nublic_app_photos.cfg:/etc/nublic/nublic_app_photos_db.cfg'
from nublic_app_photos_server.server import app as application
