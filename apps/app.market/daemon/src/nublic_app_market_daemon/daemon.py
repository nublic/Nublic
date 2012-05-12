import apt
import apt.progress
import aptdaemon.client
import aptdaemon.enums
from bottle import Bottle, request, response, run
import copy
import httplib2
import simplejson as json
from StringIO import StringIO
import sys
import time
import traceback

import os
os.environ['DISPLAY']=':0'

app = Bottle()

# URL where packages list is found
packages_url = 'http://nublic.com/packages.json'
# Maximum difference between updates
max_update_diff = 2 * 3600  # 2 hours
# Information downloaded from server
packages_list = None
# Time where information was last downloaded
last_packages_update = None
# Use local packages
use_local = True
# Local packages path
local_packages_path = '/var/lib/nublic/packages.json'


@app.get('/about')
def about():
    return u'Nublic Market Server 0.0.1'


def ensure_updated_packages():
    # Refer to global variables
    global max_update_diff
    global packages_list
    global last_packages_update
    # Try to update the packages list
    now = time.time()
    if packages_list == None or last_packages_update == None or \
            (last_packages_update - now) > max_update_diff:
        # Execute apt-get update
        print >> sys.stderr, 'Updating apt-get cache'
        client = aptdaemon.client.AptClient()
        transaction = client.update_cache()
        transaction.connect("finished", lambda x: sys.stderr.write('Exit code ' + x + '\n'))
        transaction.connect("progress-changed", lambda p: sys.stderr.write('Progress ' + p + '\n'))
        transaction.run()
        # Try to get new package list
        print >> sys.stderr, 'Updating package list'
        new_packages_list = grab_packages()
        if new_packages_list != None:
            last_packages_update = now
            packages_list = new_packages_list

def grab_packages():
    # Refer to global variables
    global packages_url
    global use_local
    # Try to get new package list
    if not use_local:
        h = httplib2.Http()
        resp, content = h.request(packages_url, "GET")
        if resp.status == 200:  # Ok
            try:
                return json.loads(content)
            except:
                return None
        else:
           return  None
    else:
        try:
            return json.load(open(local_packages_path))
        except:
            return None


@app.get('/packages')
def list_packages():
    # Refer to global variables
    global packages_url
    global max_update_diff
    global packages_list
    global last_packages_update
    # Try to update the packages list
    try:
        ensure_updated_packages()
    except:
        return (traceback.format_exc(), 500)
    # If we have a package list, use it
    if packages_list == None:
        # If we don't have a package list, return error
        return ('"no package list"', 500, { 'Content-Type': 'application/json' })
    else:
        # Initialize cache
        cache = apt.Cache()
        # Send the information along with the status
        info = copy.deepcopy(packages_list)
        for pkg in info:
            deb_name = pkg[u'deb']
            if is_package_installed(cache, deb_name):
                pkg[u'status'] = u'installed'
            else:
                pkg[u'status'] = u'not_installed'
        # Get the JSON string
        io = StringIO()
        json.dump(info, io)
        response.set_header('Content-Type', 'application/json')
        return io.getValue()


def is_package_installed(cache, pkg_name):
    try:
        pkg = cache[pkg_name]
        return pkg.is_installed
    except:
        return False


def get_package_info(pkg_name):
    for pkg in packages_list:
        if pkg[u'id'] == pkg_name:
            return pkg
    return None


class ErrorInstallProgress(apt.progress.InstallProgress):
    def __init__(self):
        apt.progress.InstallProgress.__init__()
        self._any_error = False
        self._errors = []
    
    def error(self, pkg, errormsg):
        apt.progress.InstallProgress.error(self, pkg, errormsg)
        self._any_error = True
        self._errors.append(errormsg)

    def any_error(self):
        return self._any_error

    def get_error_messages(self):
        return self._errors
        
    
@app.put('/packages')
def install_package():
    # Refer to global variables
    global packages_list
    # Ensure it is updated
    ensure_updated_packages()
    # Initialize response
    status = None
    response_code = None
    # Now try to install
    if packages_list == None:
        status = { 'status': 'no-package-list' }
        response_code = 500
    else:
        try:
            pkg_name = request.forms.get('package')
            pkg = get_package_info(pkg_name)
            # Check package exists
            if pkg == None:
                status = { 'status': 'does-not-exist' }
                response_code = 404
            else:
                # Find the deb in the cache
                cache = apt.Cache()
                deb_name = pkg[u'deb']
                if not is_package_installed(cache, deb_name):
                    # Install the package
                    client = aptdaemon.client.AptClient()
                    exit_code = client.install_packages([ deb_name ], wait=True)
                    # Commit changes
                    if exit_code != aptdaemon.enums.EXIT_SUCCESS:
                        status = { 'status': 'error' }
                        response_code = 500
                    else:
                        status = { 'status': 'ok' }
                        response_code = 200
                else:
                    status = { 'status': 'already-installed' }
                    response_code = 200
        except:
            status = { 'status': 'error', 'errors': [ traceback.format_exc() ] }
            response_code = 500
    # Get the JSON string
    io = StringIO()
    json.dump(status, io)
    response.set_header('Content-Type', 'application/json')
    response.status = response_code
    return io.getvalue()


def autoremove():
    any_package_autoremoved = False
    # Find packages to autoremove
    cache = apt.Cache()
    for pkg in cache:
        if pkg.is_auto_removable:
            any_package_autoremoved = True
            pkg.mark_delete()
    # If any package was found, commit and repeat
    if any_package_autoremoved:
        cache.commit()


@app.delete('/packages')
def remove_package():
    # Refer to global variables
    global packages_list
    # Ensure it is updated
    ensure_updated_packages()
    # Initialize response
    status = None
    response_code = None
    # Now try to remove
    if packages_list == None:
        status = { 'status': 'no-package-list' }
        response_code = 500
    else:
        try:
            pkg_name = request.forms.get('package')
            pkg = get_package_info(pkg_name)
            # Check package exists
            if pkg == None:
                status = { 'status': 'does-not-exist' }
                response_code = 404
            else:
                # Find the deb in the cache
                cache = apt.Cache()
                deb_name = pkg[u'deb']
                if is_package_installed(cache, deb_name):
                    # Delete the package
                    client = aptdaemon.client.AptClient()
                    exit_code = client.remove_packages([ deb_name ], wait=True)
                    if exit_code != aptdaemon.enums.EXIT_SUCCESS:
                        status = { 'status': 'error' }
                        response_code = 500
                    else:
                        autoremove()
                        status = { 'status': 'ok' }
                        response_code = 200
                else:
                    status = { 'status': 'not-installed' }
                    response_code = 200
        except:
            status = { 'status': 'error', 'errors': [ traceback.format_exc() ] }
            response_code = 500
    # Get the JSON string
    io = StringIO()
    json.dump(status, io)
    response.set_header('Content-Type', 'application/json')
    response.status = response_code
    return io.getvalue()


@app.post('/upgrade')
def upgrade_system():
    # Initialize response
    status = None
    response_code = None
    try:
        # Ensure package list is updated
        ensure_updated_packages()
        # Now upgrade the system (dist-upgrade)
        client = aptdaemon.client.AptClient()
        exit_code = client.upgrade_system(safe_mode=False, wait=True)
        if exit_code != aptdaemon.enums.EXIT_SUCCESS:
            status = { 'status': 'error' }
            response_code = 500
        else:
            status = { 'status': 'ok' }
            response_code = 200
    except:
        status = { 'status': 'error', 'errors': [ traceback.format_exc() ] }
        response_code = 500
    # Get the JSON string
    io = StringIO()
    json.dump(status, io)
    response.set_header('Content-Type', 'application/json')
    response.status = response_code
    return io.getvalue()


if __name__ == '__main__':
    run(app, host='0.0.0.0', port=5000)
