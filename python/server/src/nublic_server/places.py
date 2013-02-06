import os
import hashlib
import magic
from fnmatch import fnmatch

BROWSER_ROOT_FOLDER = '/var/nublic/cache/browser'


def get_cache_folder(path):
    cache_folder = hashlib.sha1(path).hexdigest()
    return os.path.join(BROWSER_ROOT_FOLDER, cache_folder)


def get_cache_view(path, extension):
    ''' Returns the absolute path to the extension given'''
    cache_folder = get_cache_folder(path)
    if os.path.isdir(cache_folder):
        for f in os.listdir(cache_folder):
            if fnmatch(f, 'view.' + extension):  # @TODO Possible security error
                return os.path.join(cache_folder, f)
    return None


def get_cache_views(path):
    ''' Returns the view filenames available
        They are relative to the get_cache_folder(path) directory
    '''
    cache_folder = get_cache_folder(path)
    views = []
    if os.path.isdir(cache_folder):
        for f in os.listdir(cache_folder):
            if fnmatch(f, 'view.'):  # @TODO Possible security error
                views.append(f)
    return views


_magic = magic.open(magic.MAGIC_MIME_TYPE)
_magic.load()


def get_mime_type(path):
    try:
        return _magic.file(path)
    except:
        return _magic.file(path.encode('utf-8'))
