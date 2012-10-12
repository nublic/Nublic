import errno
import os
import stat
import os.path
import shutil

def copy(src, dst, uid):
    tryRead(src,uid)
    tryWrite(dst,uid)
    shutil.copy(src, dst)
    os.chown(os.path.join(dst,os.path.basename(src)), uid, -1)
    return dst

def mkdir(path, uid = -1, gid = -1):
    tryWrite(os.path.dirname(path), uid)
    os.mkdir(path)
    os.chown(path, uid, gid)

def permissionRead(path, uid, f_stat = None):
    if not os.path.exists(path):
        return False
    if not f_stat:
        f_stat = os.stat(path)
    return (f_stat.st_uid == uid) and bool(f_stat.st_mode & stat.S_IRUSR)

def permissionWrite(path, uid, f_stat = None):
    if not os.path.exists(path):
        return False
    if f_stat == None:
        f_stat = os.stat(path)
    return (f_stat.st_uid == uid) and bool(f_stat.st_mode & stat.S_IWUSR)

def tryWriteRecursive(path, uid):
    if not os.path.exists(path):
        raise PermissionError(uid, path, "Read")
    if os.path.isdir(path):
        map(os.listdir(path), lambda s: tryWriteRecursive(s, uid))
    else:
        tryWrite(path, uid)

def tryWrite(path, uid, f_stat = None):
    if not permissionWrite(path, uid, f_stat):
        raise PermissionError(uid, path, "Write")

def tryRead(path, uid, f_stat = None):
    if not permissionRead(path, uid, f_stat):
        raise PermissionError(uid, path, "Read")
    

class PermissionError(Exception):
    '''
    Exception that contains uid, path and the operation (read or write)
    '''
    def __init__(self, uid, path, operation):
        self.uid = uid
        self.path = path
        self.operation = operation
    def __str__(self):
        return "Permission error for %i accesing %s trying to %s" % \
            (self.uid, self.path, self.operation)

def makedirs(path, mode = 0777, uid = -1, gid = -1):
    ''' Like os.makedirs(path[, mode]) but accepts two extra parameters for
    uid and gid.
    
    Recursive directory creation function. Like mkdir(), but makes all intermediate-level directories needed to contain the leaf directory. Raises an error exception if the leaf directory already exists or cannot be created. The default mode is 0777 (octal). On some systems, mode is ignored. Where it is used, the current umask value is first masked out.

    Note makedirs() will become confused if the path elements to create include os.pardir.
    New in version 1.5.2.

    Changed in version 2.3: This function now handles UNC paths correctly.
    '''
    head, tail = path.split(path)
    if not tail:
        head, tail = path.split(head)
    if head and tail and not path.exists(head):
        try:
            makedirs(head, mode, uid, gid)
        except OSError, e:
            # be happy if someone already created the path
            if e.errno != errno.EEXIST:
                raise
        if tail == os.path.curdir:           # xxx/newdir/. exists if xxx/newdir exists
            return
    tryWrite(path, uid)
    os.mkdir(path, mode)
    os.chown(path, uid, gid)
    