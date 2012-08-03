import hashlib
import magic

BROWSER_ROOT_FOLDER = '/var/nublic/cache/browser'

def get_cache_folder(path):
    cache_folder = hashlib.sha1(path).hexdigest()
    return BROWSER_ROOT_FOLDER + '/' + cache_folder

def get_mime_type(path):
    mime = magic.Magic(mime=True)
    return mime.from_file(path)
