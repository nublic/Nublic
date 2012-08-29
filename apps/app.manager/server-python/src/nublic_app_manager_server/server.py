from flask import Flask, request, abort, send_file
import os
import os.path
import simplejson as json

from nublic.files_and_users import get_all_users, Mirror, WorkFolder, create_mirror, create_work_folder
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

@app.route('/mirrors', methods=['GET', 'PUT', 'DELETE'])
def mirrors():
    user = require_user()
    # Get mirrors information
    if request.method == 'GET':
        return_mirrors = []
        for mirror in user.get_owned_mirrors():
            return_mirror = { 'id': mirror.get_id(), 'name': mirror.get_name() }
            return_mirrors.append(return_mirror)
        return_mirrors.sort(key=lambda m: m['name'])
        return json.dumps(return_mirrors)
    # Create a new mirror
    elif request.method == 'PUT':
        name = request.form.get('name', None)
        if name != None:
            mirror = create_mirror(name, user.get_username())
            return str(mirror.get_id())
        else:
            abort(500)
    # Delete a mirror
    elif request.method == 'DELETE':
        mid = int(request.form.get('id'))
        m = Mirror(mid)
        if m.exists() and m.get_owner() == user.get_username():
            m.delete(False)
            abort(200)
        else:
            abort(403)

@app.route('/mirror-name', methods=['PUT'])
def mirror_name():
    user = require_user()
    name = request.form.get('name', None)
    mid = int(request.form.get('id'))
    m = Mirror(mid)
    if name != None and m.exists() and m.get_owner() == user.get_username():
        # Change name
        m.change_name(name)
        abort(200)
    else:
        abort(403)

@app.route('/synceds', methods=['GET', 'PUT', 'DELETE'])
def synceds():
    user = require_user()
    # Get mirrors information
    if request.method == 'GET':
        return_synceds = []
        for synced in user.get_owned_work_folders():
            return_synced = { 'id': synced.get_id(), 'name': synced.get_name() }
            return_synceds.append(return_synced)
        return_synceds.sort(key=lambda m: m['name'])
        return json.dumps(return_synceds)
    # Create a new mirror
    elif request.method == 'PUT':
        name = request.form.get('name', None)
        if name != None:
            synced = create_work_folder(name, user.get_username())
            return str(synced.get_id())
        else:
            abort(500)
    # Delete a mirror
    elif request.method == 'DELETE':
        sid = int(request.form.get('id'))
        s = WorkFolder(sid)
        if s.exists() and s.get_owner() == user.get_username():
            s.delete(False)
            abort(200)
        else:
            abort(403)

@app.route('/synced-name', methods=['PUT'])
def synced_name():
    user = require_user()
    name = request.form.get('name', None)
    sid = int(request.form.get('id'))
    s = WorkFolder(sid)
    if name != None and s.exists() and s.get_owner() == user.get_username():
        # Change name
        s.change_name(name)
        abort(200)
    else:
        abort(403)

if __name__ == '__main__':
    app.run()
