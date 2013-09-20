import os.path
import hashlib
import magic
#from fnmatch import fnmatch
import glob


def browser_root_folder():
    """ Get cache root folder from environment or default """
    return os.environ.get('BROWSER_CACHE_FOLDER',
                          '/var/nublic/cache/browser')

def get_cache_folder(path):
    """ Get cache folder for a given file """
    cache_folder = hashlib.sha1(path).hexdigest()
    return os.path.join(browser_root_folder(), cache_folder)


def ensure_cache_folder(path):
    cache_folder = get_cache_folder(path)
    if not os.path.exists(cache_folder):
        os.makedirs(cache_folder)
    return cache_folder


def get_cache_view(path, extension):
    ''' Returns the absolute path to the extension given.
        extension must be a secure string for accessing to the filesystem'''
    cache_folder = get_cache_folder(path)
    cache_file = os.path.join(cache_folder, 'view.' + extension)
    if os.path.exists(cache_file):
        return cache_file
    else:
        return None


def get_cache_views(path):
    ''' Returns the view filenames available
        They are relative to the get_cache_folder(path) directory
    '''
    cache_folder = get_cache_folder(path)
    views = []
    if os.path.isdir(cache_folder):
        views = glob.glob(os.path.join(cache_folder, 'view.*'))
        #for f in os.listdir(cache_folder):
            #if fnmatch(f, 'view.'):  # @TODO Possible security error
                #views.append(f)
    return views


_magic = magic.open(magic.MAGIC_MIME_TYPE)
_magic.load()


def get_mime_type(path):
    try:
        return _magic.file(path)
    except:
        return _magic.file(path.encode('utf-8'))
