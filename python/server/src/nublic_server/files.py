

import os.path
import shutil
from nublic_server.places import get_mime_type, get_cache_folder
from hashlib import sha1  # pylint: disable=E0611

CACHE_ROOT_DIR = '/var/nublic/cache/browser/'


def copy(src, dst, user):
    ''' copy file if you have permission or group permission allows you
        throw a PermissionError exception
    '''
    user.try_read(src)
    user.try_write(dst)
    shutil.copy(src, dst)
    user.assign_file(os.path.join(dst, os.path.basename(src)))
    return dst


def mkdir(path, user):
    ''' Version of mkdir that creates a directory in the name of user.
    It checks permission of user before with try_write'''
    user.try_write(os.path.dirname(path))
    os.mkdir(path)
    user.assign_file(path)


def get_folders(depth, path, user):
    ''' Get the all the subfolders of the given folder up to some depth.
    Minimum depth is 1'''
    if not user.can_read(path):
        return []
    subfolders = []
    if depth > 0:
        for folder in [os.path.join(path, f) for f in os.listdir(path)]:
            if os.path.isdir(folder) and user.can_read(folder):
                name = os.path.basename(folder)
                subfolders = subfolders + [{'name':name,
                            "subfolders":get_folders(depth - 1, folder, user),\
                            "writable": user.can_write(folder)}]
    return subfolders


def get_file_info(path, user):
    ''' Gets some information about the given file in a dictionary. The fields
    are 'name', 'writable', 'last_update', 'size', 'hast_thumb', 'mime' and
    'view' '''
    info = {}
    info['name'] = os.path.basename(path)
    info['writable'] = user.can_write(path)
    file_stat = os.stat(path)
    info['last_update'] = file_stat.st_mtime
    info['size'] = file_stat.st_size
    info['has_thumb'] = os.path.exists(os.path.join(get_cache_folder(path), \
                                                     "thumbnail.png"))
    if os.path.isdir(path):
        info['mime'] = 'application/x-directory'
    else:
        info['mime'] = get_mime_type(path)
    info['view'] = ""  # @todo
    return info


class PermissionError(Exception):
    '''
    Exception that contains username, path and the operation (read or write)
    '''
    def __init__(self, username, path, operation):
        self.username = username
        self.path = path
        self.operation = operation
        super(PermissionError, self).__init__()

    def __str__(self):
        return "Permission error for %i accesing %s trying to %s" % \
            (self.username, self.path, self.operation)
