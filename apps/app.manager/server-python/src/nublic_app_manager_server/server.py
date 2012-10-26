import datetime
from flask import Flask, request, abort, send_file
import os
import os.path
import random
import simplejson as json
import string
import pexpect

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
            app_data = json.load(fp)
            fp.close()
            apps[app_data['id']] = app_data
    return apps

@app.route('/apps')
def apps():
    apps = load_apps()
    return_apps = []
    for app_id in apps:
        current_app = apps[app_id]
        if 'web' in current_app:
            return_app = { 'id': current_app['id'], 'name': current_app['name'],
                           'developer': current_app['developer'], 'web': current_app['web'] }
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
        if m.exists() and m.get_owner().get_username() == user.get_username():
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
    if name != None and m.exists() and m.get_owner().get_username() == user.get_username():
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
        if s.exists() and s.get_owner().get_username() == user.get_username():
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
    if name != None and s.exists() and s.get_owner().get_username() == user.get_username():
        # Change name
        s.change_name(name)
        abort(200)
    else:
        abort(403)

# Work on SSH keys
current_upload_keys = []

def prune_old_upload_keys():
    global current_upload_keys
    five_minutes = datetime.timedelta(minutes=5)
    five_minutes_before_now = datetime.datetime.now() - five_minutes
    current_upload_keys = filter(lambda x: x['time'] > five_minutes_before_now, current_upload_keys)

@app.route('/synced-generate-invite/<int:sid>')
def synced_generate_invite(sid):
    user = require_user()
    s = WorkFolder(sid)
    #app.logger.error('User: %s, existance: %s, owner: %s', user.get_username(), str(s.exists()), s.get_owner().get_username())
    if s.exists() and s.get_owner().get_username() == user.get_username():
        random_id = random.randint(100, 2000000)
        current_upload_keys.append({ 'id': random_id, 'time': datetime.datetime.now(), 'synced_id': sid, 'user': user })
        return str(random_id)
    else:
        abort(403)

def get_fingerprint():
    f = open('/var/nublic/cache/fingerprint')
    return f.read().replace("\n","")

@app.route('/synced-invite/<int:iid>')
def synced_invite(iid):
    prune_old_upload_keys()
    invite = [i for i in current_upload_keys if i['id'] == iid]
    if invite:
        r_invite = invite[0]
        s = WorkFolder(r_invite['synced_id'])
        user = r_invite['user']
        if s.exists() and s.get_owner().get_username() == user.get_username():
            fingerprint = get_fingerprint() 
            manager_path, _ = os.path.split(request.url_root[:-1])
            server = os.path.split(manager_path)[0].replace('http://', '')
            server = server.replace('https://','')
            xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + \
                  "<sparkleshare>\n" + \
                  "  <invite>\n" + \
                  "    <address>ssh://" + user.get_username() + "@" + server + "/</address>\n" + \
                  "    <remote_path>/var/nublic/work-folders/" + str(s.get_id()) + "</remote_path>\n" + \
                  "    <fingerprint>" + str(fingerprint) + "</fingerprint>\n" + \
                  "    <accept_url>https://" + server + "/manager/server/synced-upload-key/" + str(iid) + "</accept_url>\n" + \
                  "  </invite>\n" + \
                  "</sparkleshare>"
            return xml
    else:
        abort(403)

@app.route('/synced-upload-key/<int:iid>', methods=['POST'])
def synced_upload_key(iid):
    prune_old_upload_keys()
    invite = [i for i in current_upload_keys if i['id'] == iid]
    if invite:
        # Get user
        r_invite = invite[0]
        user = r_invite['user']
        # Work on public key to make it in correct format
        pubkey = request.form.get('pubkey', None)
        token_list = pubkey.split(' ')
        ssh_initial = token_list[0]
        ssh_name = token_list[-1]
        ssh_rest = token_list[1:-1]
        real_key = ssh_initial + ' ' + string.join(ssh_rest, '+') + ' ' + ssh_name
        # Finally add key
        user.add_public_key(real_key)
    else:
        abort(403)

if __name__ == '__main__':
    app.run()
