from flask import Request, request, abort
import logging
from nublic.files_and_users import User
#from nublic.filewatcher import init_socket_watcher
from nublic.resource import App
import ConfigParser

log = logging.getLogger(__name__)


class RequestWithDelete(Request):
    @property
    def want_form_data_parsed(self):
        return self.environ['REQUEST_METHOD'] in ['DELETE', 'PUT', 'POST']


def config_to_obj(conf_paths, dictionary=None):
    """Reads a path to a configuration file and returns a
    flask-compatible dictionary of configurations
    """
    conf = ConfigParser.RawConfigParser()
    conf.read(conf_paths)
    if dictionary is None:
        dictionary = dict()
    for section in conf.sections():
        for (name, value) in conf.items(section):
            dictionary[name.upper()] = value
    return dictionary


def init_nublic_server(app, log_file, resource_app, db,
                       filewatcher_app_name="", processors=None,
                       configuration_paths=None):
    # Load configuration files
    init_bare_nublic_server(
        app, log_file, configuration_paths=configuration_paths)
    try:
        app.config['SQLALCHEMY_DATABASE_URI']
    except KeyError:
        # Use resource daemon to configure
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


def init_bare_nublic_server(app, log_file, configuration_paths=None):
    ''' Inits a nublic server without database support '''
    app.request_class = RequestWithDelete
    if configuration_paths:
        config_to_obj(configuration_paths, app.config)
        try:
            log_format = app.config['LOG_FORMAT']
        except KeyError:
            log_format = ('%(asctime)s %(levelname)s: %(message)s '
                          '[in %(pathname)s:%(lineno)d]')
        try:
            log_level = int(app.config['LOG_LEVEL'])
        except KeyError:
            log_level = logging.INFO
        try:
            log_file = app.config['LOG_FILE']
        except KeyError:
            pass  # File as parameter is fine
    else:
        log_format = ('%(asctime)s %(levelname)s: %(message)s '
                      '[in %(pathname)s:%(lineno)d]')
        log_level = logging.INFO
    # Set up logging handlers
    handler = logging.FileHandler(log_file)
    handler.setFormatter(logging.Formatter(log_format))
    #log_format = ('%(asctime)s %(levelname)s: %(message)s '
    #              '[in %(pathname)s:%(lineno)d]')
    #logging.basicConfig(filename=log_file, format=log_format)
    app.logger.addHandler(handler)
    app.logger.setLevel(log_level)


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
