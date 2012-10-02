import sys, errno
import os
import stat
import os.path
from flask import Request, request, abort
import logging
from nublic.files_and_users import User
from nublic.filewatcher import init_socket_watcher
from nublic.resource import App


class RequestWithDelete(Request):
    @property
    def want_form_data_parsed(self):
        return self.environ['REQUEST_METHOD'] in ['DELETE', 'PUT', 'POST']

def init_nublic_server(app, log_file, resource_app, db, filewatcher_app_name, processors):
    app.request_class = RequestWithDelete
    # Set up logging handlers
    handler = logging.FileHandler(log_file)
    handler.setFormatter(logging.Formatter(
        '%(asctime)s %(levelname)s: %(message)s '
        '[in %(pathname)s:%(lineno)d]'
    ))
    app.logger.addHandler(handler)
    # Get resource information
    res_app = App(resource_app)
    res_key = res_app.get('db')
    db_uri = 'postgresql://' + res_key.value('user') + ':' + res_key.value('pass') + \
             '@localhost:5432/' + res_key.value('database')
    # Init DB
    app.config['SQLALCHEMY_DATABASE_URI'] = db_uri
    db.app = app
    db.init_app(app)
    db.create_all(app=app)
    # Init watching
    init_socket_watcher(filewatcher_app_name, processors, app.logger)
    
def init_bare_nublic_server(app, log_file):
    app.request_class = RequestWithDelete
    # Set up logging handlers
    handler = logging.FileHandler(log_file)
    handler.setFormatter(logging.Formatter(
        '%(asctime)s %(levelname)s: %(message)s '
        '[in %(pathname)s:%(lineno)d]'
    ))
    app.logger.addHandler(handler)

def split_reasonable(s, separator):
    if s == None:
        return []
    else:
        return filter(lambda st: st != '', s.split(separator))

def require_user():
    auth = request.authorization
    user = User(auth.username)
    if not user.exists():
        abort(401)
    return user

def permissionRead(path, uid, f_stat = None):
    # TODO UnitTest!
    if not f_stat:
        f_stat = os.stat(path)
    return (f_stat.st_uid == uid) and bool(f_stat.st_mode & stat.S_IRUSR)

def permissionWrite(path, uid, f_stat = None):
    # TODO UnitTest!
    if not f_stat:
        f_stat = os.stat(path)
    return (f_stat.st_uid == uid) and bool(f_stat.st_mode & stat.S_IWUSR)

def tryWrite(path, uid, f_stat = None):
    if not permissionWrite(path, uid, f_stat):
        raise PermissionError(uid, path, "Write")

def tryRead(path, uid, f_stat = None):
    if not permissionRead(path, uid, f_stat):
        raise PermissionError(uid, path, "Read")
    

class PermissionError(Exception):
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
    