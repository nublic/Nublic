

import stat
import os.path
import shutil
from nublic_server.places import get_mime_type
from hashlib import sha1 # pylint: disable=E0611

CACHE_ROOT_DIR = '/var/nublic/cache/browser/'

def copy(src, dst, uid):
    ''' copy file if you have permission or group permission allows you
        throw a PermissionError exception
    '''
    try_read(src, uid)
    try_write(dst, uid)
    shutil.copy(src, dst)
    os.chown(os.path.join(dst, os.path.basename(src)), uid, -1)
    return dst

def mkdir(path, uid = -1, gid = -1):
    ''' Version of mkdir that creates a directory in the name of uid and gid.
    It checks permission of uid user before with try_write'''
    try_write(os.path.dirname(path), uid)
    os.mkdir(path)
    os.chown(path, uid, gid)

def get_folders(depth, path, uid):
    ''' Get the all the subfolders of the given folder up to some depth.
    Minimum depth is 1'''
    if not permission_read(path, uid):
        return []
    subfolders = []
    if depth > 0:
        for folder in [os.path.join(path, f) for f in os.listdir(path)]:
            if os.path.isdir(folder) and permission_read(folder, uid):
                name = os.path.basename(folder)
                subfolders = subfolders + [{'name' : name,  
                            "subfolders": get_folders(depth-1, folder, uid), \
                            "writable" : permission_write(folder, uid) }]
    return subfolders 

def get_file_info(path, uid):
    ''' Gets some information about the given file in a dictionary. The fields
    are 'name', 'writable', 'last_update', 'size', 'hast_thumb', 'mime' and 
    'view' '''
    info = {}
    info['name'] = os.path.basename(path)
    info['writable'] = permission_write(path, uid)
    file_stat = os.stat(path)
    info['last_update'] = file_stat.st_mtime
    info['size'] = file_stat.st_size
    info['has_thumb'] = os.path.exists(os.path.join(get_cache_folder(path), \
                                                     "thumbnail.png"))
    if os.path.isdir(path):
        info['mime'] = 'application/x-directory'
    else:
        info['mime'] = get_mime_type(path)
    info['view'] = "" # @todo
    return info

def get_cache_folder(path):
    ''' Returns the full internal cache path for a file '''
    return os.path.join(CACHE_ROOT_DIR, sha1(path))

def permission_read(path, uid, f_stat = None):
    ''' Returns true if the user has permission to read or 
    groups have permission to read'''
    if not os.path.exists(path):
        return False
    if f_stat == None:
        f_stat = os.stat(path)
    user_check = (f_stat.st_uid == uid) and bool(f_stat.st_mode & stat.S_IRUSR)
    group_check = bool(f_stat.st_mode & stat.S_IRGRP)
    return user_check or group_check

def permission_write(path, uid, f_stat = None):
    ''' Returns true if the user has permission to write or 
    groups have permission to write'''
    if not os.path.exists(path):
        return False
    if f_stat == None:
        f_stat = os.stat(path)
    user_check = (f_stat.st_uid == uid) and bool(f_stat.st_mode & stat.S_IWUSR)
    group_check = bool(f_stat.st_mode & stat.S_IWGRP)
    return user_check or group_check 

def try_write_recursive(path, uid):
    ''' Throws PermissionError if ANY file under the file
    given does not have permission to be written'''
    if not os.path.exists(path):
        raise PermissionError(uid, path, "Read")
    if os.path.isdir(path):
        [try_write_recursive(s, uid) for s in os.listdir(path)]
    else:
        try_write(path, uid)

def try_read_recursive(path, uid):
    ''' Throws PermissionError if ANY file under the file
    given does not have permission to be written'''
    if not os.path.exists(path):
        raise PermissionError(uid, path, "Read")
    if os.path.isdir(path):
        [try_read_recursive(s, uid) for s in os.listdir(path)]
    else:
        try_read(path, uid)


def try_write(path, uid, f_stat = None):
    ''' Throws PermissionError if the file
    given does not have permission to be written'''
    if not permission_write(path, uid, f_stat):
        raise PermissionError(uid, path, "Write")

def try_read(path, uid, f_stat = None):
    ''' Throws PermissionError if the file
    given does not have permission to be read'''
    if not permission_read(path, uid, f_stat):
        raise PermissionError(uid, path, "Read")


class PermissionError(Exception):
    '''
    Exception that contains uid, path and the operation (read or write)
    '''
    def __init__(self, uid, path, operation):
        self.uid = uid
        self.path = path
        self.operation = operation
        super(PermissionError, self).__init__()
        
    def __str__(self):
        return "Permission error for %i accesing %s trying to %s" % \
            (self.uid, self.path, self.operation)
    