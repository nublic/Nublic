import os
import os.path
from pgmagick.api import Image
import re
import requests
import shutil
import json

from pyechonest import config
import pyechonest.artist

# Get the cache path from environment
try:
    ROOT_CACHE_MUSIC_FOLDER = os.environ["MUSIC_CACHE_FOLDER"]
except KeyError:
    ROOT_CACHE_MUSIC_FOLDER = "/var/nublic/cache/music"

ARTISTS_FOLDER = os.path.join(ROOT_CACHE_MUSIC_FOLDER, "artists")
ALBUMS_FOLDER = os.path.join(ROOT_CACHE_MUSIC_FOLDER, "albums")
ORIGINAL_FILENAME = "orig"
THUMBNAIL_FILENAME = "thumbnail.png"
THUMBNAIL_SIZE = 96

import logging
log = logging.getLogger(__name__)


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


def request_several_times(url, times=3, timeout=5):
    for i in range(0, times):
        try:
            r = requests.get(url, timeout=timeout)
            return r
        except requests.Timeout:
            log.info("Timeout achieved calling %s", url)
    log.info("Requesting %d times failed", times)
    return None


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
                r = request_several_times(image_url)
                if r is not None and r.status_code == 200:
                    f = open(place, 'w')
                    f.write(r.content)
                    f.close()
                    return True
    except:
        log.exception("Exception detected while getting an artist image")
    return False


def ensure_album_image(id_, file_, album_name, artist_name):
    _ensure(id_, get_album_folder, lambda path: get_album_image(id_,
            file_, album_name, artist_name, path))


def get_album_image(id_, file_, album_name, artist_name, place):
    """
    file_ must be a byte string in utf8 encoding
    """
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
            search = album_name + u' ' + artist_name
        discogs_params = {'f': 'json',
                          'type': 'releases', 'q': search}
        r = requests.get(
            'http://api.discogs.com/search', params=discogs_params)
        log.debug("Gets request %s", r)
        response = json.loads(r.content)
        if response is not None and response['resp']['status']:
            img_results = response['resp']['search']['searchresults']['results']
            log.debug("img_results %s", str(img_results))
            if img_results:
                image_url = img_results[0]['thumb']
                log.debug("img_url %s", str(image_url))
                r = request_several_times(image_url)
                if r is not None:
                    f = open(place, 'w')
                    f.write(r.content)
                    f.close()
                    return True
    except BaseException:
        log.exception("Exception detected getting album from api.discogs.com")
    return False
