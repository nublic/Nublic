import os
import hashlib
import magic

BROWSER_ROOT_FOLDER = '/var/nublic/cache/browser'

def get_cache_folder(path):
    cache_folder = hashlib.sha1(path.encode('utf-8')).hexdigest()
    return os.path.join(BROWSER_ROOT_FOLDER, cache_folder)

_magic = magic.open(magic.MAGIC_MIME_TYPE)
_magic.load()

def get_mime_type(path):
    try:
        return _magic.file(path)
    except:
        return _magic.file(path.encode('utf-8'))
