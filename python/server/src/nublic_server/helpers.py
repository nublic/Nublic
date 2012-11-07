from flask import Request, request, abort
import logging
from nublic.files_and_users import User
from nublic.filewatcher import init_socket_watcher
from nublic.resource import App
from nublic_files_and_users_client.dbus_client import get_user_uid


class RequestWithDelete(Request):
    @property
    def want_form_data_parsed(self):
        return self.environ['REQUEST_METHOD'] in ['DELETE', 'PUT', 'POST']


def init_nublic_server(app, log_file, resource_app, db,
                       filewatcher_app_name, processors):
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
    db_uri = 'postgresql://' + res_key.value('user') + ':' + \
                res_key.value('pass') + \
                '@localhost:5432/' + res_key.value('database')
    # Init DB
    app.config['SQLALCHEMY_DATABASE_URI'] = db_uri
    db.app = app
    db.init_app(app)
    db.create_all(app=app)
    # Init watching
    init_socket_watcher(filewatcher_app_name, processors, app.logger)


def init_bare_nublic_server(app, log_file):
    ''' Inits a nublic server without database support '''
    app.request_class = RequestWithDelete
    # Set up logging handlers
    handler = logging.FileHandler(log_file)
    handler.setFormatter(logging.Formatter(
        '%(asctime)s %(levelname)s: %(message)s '
        '[in %(pathname)s:%(lineno)d]'
            ))
    app.logger.addHandler(handler)


def split_reasonable(string, separator):
    '''It improves split with a [] instead of none and removes empty strings'''
    if string == None:
        return []
    else:
        return filter(lambda st: st != '', string.split(separator))


def require_user():
    ''' Check the authorization and return the username '''
    auth = request.authorization
    user = User(auth.username)
    if not user.exists():
        abort(401)
    return user


def require_uid():
    ''' Check the authorization and return the uid of the user '''
    user = require_user()
    return user.get_user_uid()
