import apt
import copy
import httplib2
import simplejson as json
from StringIO import StringIO
import time

# Flash definition
from flask import Flask, abort, request
app = Flask(__name__)

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


@app.route('/about', methods=['GET'])
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
        # Try to get new package list
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


@app.route('/packages', methods=['GET'])
def list_packages():
    # Refer to global variables
    global packages_url
    global max_update_diff
    global packages_list
    global last_packages_update
    # Try to update the packages list
    ensure_updated_packages()
    # If we have a package list, use it
    if packages_list == None:
        # If we don't have a package list, return error
        abort(500)
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
        return (io.getvalue(), 200, { 'Content-Type': 'application/json' })


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

    
@app.route('/packages', methods=['PUT'])
def install_package():
    # Refer to global variables
    global packages_list
    # Ensure it is updated
    ensure_updated_packages()
    # Now try to install
    if packages_list == None:
        abort(500)
    else:
        try:
            pkg_name = request.form['package']
            pkg = get_package_info(pkg_name)
            # Check package exists
            if pkg == None:
                abort(404) # Non existant
                return
            # Find the deb in the cache
            cache = apt.Cache()
            deb_name = pkg[u'deb']
            if not is_package_installed(cache, deb_name):
                # Install the package
                deb = cache[deb_name]
                deb.mark_install()
                cache.commit()
            return ('"ok"', 200, { 'Content-Type': 'application/json' })
        except:
            abort(500)


@app.route('/packages', methods=['DELETE'])
def remove_package():
     # Refer to global variables
    global packages_list
    # Ensure it is updated
    ensure_updated_packages()
    # Now try to install
    if packages_list == None:
        abort(500)
    else:
        try:
            pkg_name = request.form['package']
            pkg = get_package_info(pkg_name)
            # Check package exists
            if pkg == None:
                abort(404) # Non existant
                return
            # Find the deb in the cache
            cache = apt.Cache()
            deb_name = pkg[u'deb']
            if is_package_installed(cache, deb_name):
                # Install the package
                deb = cache[deb_name]
                deb.mark_delete()
                cache.commit()
            return ('"ok"', 200, { 'Content-Type': 'application/json' })
        except:
            abort(500)
            

@app.route('/upgrade', methods=['POST'])
def upgrade_system():
    return 'Upgraded'


if __name__ == '__main__':
    app.run(host='0.0.0.0')
