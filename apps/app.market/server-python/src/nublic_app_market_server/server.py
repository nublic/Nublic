import datetime
from flask import Flask, request, abort
from pykka.actor import ThreadingActor
import requests
import simplejson as json
import traceback

import nublic.market
from nublic_server.helpers import init_bare_nublic_server, require_user

# Init app
app = Flask(__name__)
app.debug = True
init_bare_nublic_server(app, '/var/log/nublic/nublic-app-market.python.log')
app.logger.error('Starting market app')

PACKAGES_URL = "http://nublic.com/packages.json"
MAX_UPDATE_DIFF = datetime.timedelta(hours=2)
USE_LOCAL = True
LOCAL_PACKAGES_PATH = "/var/lib/nublic/packages.json"

PACKAGES_LIST = None
LAST_PACKAGES_UPGRADE = None


@app.route('/about')
def about():
    return 'Nublic Server v0.0.3'


def ensure_updated_packages():
    global PACKAGES_LIST, LAST_PACKAGES_UPGRADE, MAX_UPDATE_DIFF
    now = datetime.datetime.now()
    if PACKAGES_LIST is None or LAST_PACKAGES_UPGRADE is None or (now - LAST_PACKAGES_UPGRADE) > MAX_UPDATE_DIFF:
        try:
            nublic.market.update_cache()
        except:
            pass
        new_pkgs = grab_packages()
        if new_pkgs is not None:
            PACKAGES_LIST = new_pkgs
            LAST_PACKAGES_UPGRADE = now


def grab_packages():
    global PACKAGES_URL, USE_LOCAL, LOCAL_PACKAGES_PATH
    if USE_LOCAL:
        fp = open(LOCAL_PACKAGES_PATH)
        pkgs = json.load(fp)
        fp.close()
        return pkgs
    else:
        r = requests.get(PACKAGES_URL)
        pkgs = json.loads(r.content)
        return pkgs


def require_package_list():
    global PACKAGES_LIST
    ensure_updated_packages()
    if PACKAGES_LIST is not None:
        return PACKAGES_LIST
    else:
        abort(500)


def require_package(pkg_name):
    pkgs = require_package_list()
    for pkg in pkgs:
        if pkg['id'] == pkg_name:
            return pkg
    abort(404)

# Needed to handle more than one change at a time
PACKAGES_BEING_INSTALLED = []
PACKAGES_BEING_REMOVED = []
PACKAGES_WITH_ERROR = []

# Magic names
STATUS_DOES_NOT_EXIST = "does-not-exist"
STATUS_INSTALLED = "installed"
STATUS_INSTALLING = "installing"
STATUS_REMOVING = "removing"
STATUS_NOT_INSTALLED = "not-installed"
STATUS_ERROR = "error"


class PackageActor(ThreadingActor):
    def __init__(self):
        ThreadingActor.__init__(self)

    def on_receive(self, message):
        global PACKAGES_BEING_INSTALLED, PACKAGES_BEING_REMOVED, PACKAGES_WITH_ERROR
        pkg = message['pkg']
        order = message['order']
        if order == 'install':
            PACKAGES_BEING_INSTALLED.append(pkg['id'])
            try:
                if not nublic.market.install_package(pkg['deb']):
                    PACKAGES_WITH_ERROR.append(pkg['id'])
            except:
                app.logger.error(traceback.format_exc())
                PACKAGES_WITH_ERROR.append(pkg['id'])
            PACKAGES_BEING_INSTALLED.remove(pkg['id'])
        elif order == 'remove':
            PACKAGES_BEING_REMOVED.append(pkg['id'])
            try:
                if not nublic.market.remove_package(pkg['deb']):
                    PACKAGES_WITH_ERROR.append(pkg['id'])
            except:
                app.logger.error(traceback.format_exc())
                PACKAGES_WITH_ERROR.append(pkg['id'])
            PACKAGES_BEING_REMOVED.remove(pkg['id'])

PACKAGE_ACTOR = PackageActor.start()

# Get packages in meanwhile
ensure_updated_packages()


def get_package_status(pkg):
    global PACKAGES_BEING_INSTALLED, PACKAGES_BEING_REMOVED, PACKAGES_WITH_ERROR
    if pkg['id'] in PACKAGES_WITH_ERROR:
        PACKAGES_WITH_ERROR.remove(pkg['id'])
        return STATUS_ERROR
    elif pkg['id'] in PACKAGES_BEING_INSTALLED:
        return STATUS_INSTALLING
    elif pkg['id'] in PACKAGES_BEING_REMOVED:
        return STATUS_REMOVING
    else:
        try:
            if nublic.market.is_package_installed(pkg['deb']):
                return STATUS_INSTALLED
            else:
                return STATUS_NOT_INSTALLED
        except:
            return STATUS_ERROR


@app.route('/packages', methods=['GET', 'PUT', 'DELETE'])
def packages():
    require_user()
    if request.method == 'GET':
        return get_packages()
    else:
        pkg_name = request.form.get('package', None)
        if pkg_name is None:
            abort(500)
        else:
            pkg = require_package(pkg_name)
            if request.method == 'PUT':
                return put_package(pkg)
            elif request.method == 'DELETE':
                return delete_package(pkg)


def get_packages():
    pkgs = require_package_list()
    for pkg in pkgs:
        pkg['status'] = get_package_status(pkg)
    return json.dumps(pkgs)


def put_package(pkg):
    global PACKAGE_ACTOR
    try:
        if pkg['id'] in PACKAGES_BEING_INSTALLED:
            return json.dumps({'status': STATUS_INSTALLING})
        elif nublic.market.is_package_installed(pkg['deb']):
            return json.dumps({'status': STATUS_INSTALLED})
        else:
            PACKAGE_ACTOR.tell({'pkg': pkg, 'order': 'install'})
            return json.dumps({'status': STATUS_INSTALLING})
    except:
        return json.dumps({'status': STATUS_ERROR})


def delete_package(pkg):
    global PACKAGE_ACTOR
    try:
        if pkg['id'] in PACKAGES_BEING_REMOVED:
            return json.dumps({'status': STATUS_REMOVING})
        elif not nublic.market.is_package_installed(pkg['deb']):
            return json.dumps({'status': STATUS_NOT_INSTALLED})
        else:
            PACKAGE_ACTOR.tell({'pkg': pkg, 'order': 'remove'})
            return json.dumps({'status': STATUS_REMOVING})
    except:
        return json.dumps({'status': STATUS_ERROR})


@app.route('/package/<pkg_name>')
def package_info(pkg_name):
    require_user()
    pkg = require_package(pkg_name)
    pkg['status'] = get_package_status(pkg)
    return json.dumps(pkg)


@app.route('/status/<pkg_name>')
def package_status(pkg_name):
    require_user()
    pkg = require_package(pkg_name)
    return json.dumps({'status': get_package_status(pkg)})


@app.route('/upgrade')
def upgrade():
    pass  # Do nothing by now

if __name__ == '__main__':
    app.run()
