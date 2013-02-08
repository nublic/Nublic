import os
import os.path
from pgmagick.api import Image
import re
import requests
import shutil
import simplejson

from pyechonest import config
import pyechonest.artist

ROOT_FOLDER = "/var/nublic/cache/music"
ARTISTS_FOLDER = os.path.join(ROOT_FOLDER, "artists")
ALBUMS_FOLDER = os.path.join(ROOT_FOLDER, "albums")
ORIGINAL_FILENAME = "orig"
THUMBNAIL_FILENAME = "thumb.png"
THUMBNAIL_SIZE = 96


def get_artist_folder(artist_id):
    return os.path.join(ARTISTS_FOLDER, unicode(artist_id).encode('utf8'))


def get_album_folder(artist_id):
    return os.path.join(ALBUMS_FOLDER, unicode(artist_id).encode('utf8'))


def _ensure(id_, folderer, getter):
    folder = folderer(id_)
    # Create folder if it does not exists
    if not os.path.exists(folder):
        os.makedirs(folder)
    # Download image
    original = os.path.join(folder, ORIGINAL_FILENAME)
    if not os.path.exists(original):
        getter(original)
    # Create thumbnail
    thumb = os.path.join(folder, THUMBNAIL_FILENAME)
    if os.path.exists(original) and not os.path.exists(thumb):
        img = Image(original)
        img.scale((THUMBNAIL_SIZE, THUMBNAIL_SIZE))
        img.write(thumb)


def ensure_artist_image(id_, name):
    _ensure(id_, get_artist_folder, lambda path: get_artist_image(id_,
            name, path))


def get_artist_image(id_, name, place):
    # Get from Echo Nest
    try:
        config.ECHO_NEST_API_KEY = 'UR4VKX7JXDXAULIWB'
        results = pyechonest.artist.search(name=name)
        if results:
            artist = results[0]
            images = artist.get_images(results=1)
            if images:
                image_url = images[0][u'url']
                r = requests.get(image_url)
                f = open(place, 'w')
                f.write(r.content)
                f.close()
                return True
    except:
        pass
    return False


def ensure_album_image(id_, file_, album_name, artist_name):
    _ensure(id_, get_album_folder, lambda path: get_album_image(id_,
            file_, album_name, artist_name, path))


def get_album_image(id_, file_, album_name, artist_name, place):
    # Try to get from filesystem
    parent_f, _ = os.path.split(file_)
    for f in os.listdir(parent_f):
        r = re.match(
            r"(cover|folder|front|album|albumart)\.(jpg|jpeg|png|bmp)", f)
        if r is not None:
            shutil.copyfile(os.path.join(parent_f, f), place)
            return True
    # Try to get from discogs
    try:
        if artist_name is None:
            search = album_name
        else:
            search = album_name + ' ' + artist_name
        discogs_params = {'f': 'json',
                          'type': 'releases', 'q': search}
        r = requests.get(
            'http://api.discogs.com/search', params=discogs_params)
        json = simplejson.loads(r.content)
        if json is not None:
            img_results = json['resp']['search']['searchresults']['results']
            if img_results:
                image_url = img_results[0]['thumb']
                r = requests.get(image_url)
                f = open(place, 'w')
                f.write(r.content)
                f.close()
                return True
    except:
        pass
    return False
