from flask import Flask, request, abort, send_file
import os
import os.path
import simplejson as json

from nublic.files_and_users import get_all_users
from nublic_server.helpers import init_bare_nublic_server, require_user

# Init app
app = Flask(__name__)
app.debug = True
init_bare_nublic_server(app, '/var/log/nublic/nublic-app-manager.python.log')
app.logger.error('Starting manager app')

APP_INFO_ROOT = '/var/lib/nublic/apps'

def load_apps():
    apps = dict()
    for f in os.listdir(APP_INFO_ROOT):
        if f.endswith('.json'):
            fp = open(os.path.join(APP_INFO_ROOT, f))
            app = json.load(fp)
            fp.close()
            apps[app['id']] = app
    return apps

@app.route('/apps')
def apps():
    require_user()
    apps = load_apps()
    return_apps = []
    for app_id in apps:
        app = apps[app_id]
        if 'web' in app:
            return_app = { 'id': app['id'], 'name': app['name'],
                           'developer': app['developer'], 'web': app['web'] }
            return_apps.append(return_app)
    return json.dumps(return_apps)

@app.route('/app-image/<style>/<app_id>/<size>')
def app_image(style, app_id, size):
    require_user()
    apps = load_apps()
    if app_id in apps:
        app = apps[app_id]
        style_icon = style + '_icon'
        if style_icon in app:
            icon_info = app[style_icon]
        else:
            icon_info = app['color_icon']
        if size in icon_info:
            return send_file(icon_info[size])
    else:
        abort(404)

@app.route('/users')
def users():
    require_user()
    return_users = []
    for user in get_all_users():
        return_user = { 'username': user.get_username(),
                        'uid': user.get_id(),
                        'name': user.get_shown_name() }
        return_users.append(return_user)
    return json.dumps(return_users)

@app.route('/user-name')
def user_name():
    user = require_user()
    return user.get_shown_name()

@app.route('/user-info', methods=['GET', 'PUT'])
def user_info():
    user = require_user()
    if request.method == 'GET':
        return json.dumps({ 'username': user.get_username(),
                            'uid': user.get_id(),
                            'name': user.get_shown_name() })
    elif request.method == 'PUT':
        name = request.form.get('name', None)
        if name != None:
            user.change_shown_name(name)
            return 'ok'
        else:
            abort(500)

if __name__ == '__main__':
    app.run()
