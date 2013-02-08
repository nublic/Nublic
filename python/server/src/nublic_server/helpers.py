from flask import Request, request, abort
import logging
from nublic.files_and_users import User
#from nublic.filewatcher import init_socket_watcher
from nublic.resource import App


class RequestWithDelete(Request):
    @property
    def want_form_data_parsed(self):
        return self.environ['REQUEST_METHOD'] in ['DELETE', 'PUT', 'POST']


def init_nublic_server(app, log_file, resource_app, db,
                       filewatcher_app_name="", processors=None):
    init_bare_nublic_server(app, log_file)
    # Get resource information
    res_app = App(resource_app)
    res_key = res_app.get('db')
    db_uri = res_key.value('uri')
    # Init DB
    app.config['SQLALCHEMY_DATABASE_URI'] = db_uri
    db.app = app
    db.init_app(app)
    db.create_all(app=app)
    # Init watching
    #init_socket_watcher(filewatcher_app_name, processors)


def init_bare_nublic_server(app, log_file):
    ''' Inits a nublic server without database support '''
    app.request_class = RequestWithDelete
    # Set up logging handlers
    handler = logging.FileHandler(log_file)
    handler.setFormatter(logging.Formatter(
        '%(asctime)s %(levelname)s: %(message)s '
        '[in %(pathname)s:%(lineno)d]'
    ))
    #log_format = ('%(asctime)s %(levelname)s: %(message)s '
    #              '[in %(pathname)s:%(lineno)d]')
    #logging.basicConfig(filename=log_file, format=log_format)
    app.logger.addHandler(handler)


def split_reasonable(string, separator):
    '''It improves split with a [] instead of none and removes empty strings'''
    if string is None:
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
