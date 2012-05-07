import apt
import copy
import httplib2
import simplejson as json
from StringIO import StringIO
import time

# Flash definition
from flask import Flask, abort
app = Flask(__name__)

# URL where packages list is found
packages_url = 'http://nublic.com/packages.json'
# Maximum difference between updates
max_update_diff = 2 * 3600  # 2 hours
# Information downloaded from server
packages_list = None
# Time where information was last downloaded
last_packages_update = None


@app.route('/packages', methods=['GET'])
def list_packages():
    # Refer to global variables
    global packages_url
    global max_update_diff
    global packages_list
    global last_packages_update
    # Try to update the packages list
    now = time.time()
    if packages_list == None or last_packages_update == None or \
            (last_packages_update - now) > max_update_diff:
        # Try to get new package list
        h = httplib2.Http()
        resp, content = h.request(packages_url, "GET")
        if resp.status == 200:  # Ok
            # Save new package list
            last_packages_update = now
            # Decode the JSON
            packages_list = json.loads(content)
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
        json.dump(['streaming API'], io)
        return io.getvalue()


def is_package_installed(cache, pkg_name):
    try:
        pkg = cache[pkg_name]
        return pkg.is_installed
    except:
        return False

    
@app.route('/packages', methods=['PUT'])
def install_package():
    return 'Installed'


@app.route('/packages', methods=['DELETE'])
def remove_package():
    return 'Removed'


@app.route('/upgrade', methods=['POST'])
def upgrade_system():
    return 'Upgraded'


if __name__ == '__main__':
    app.run(host='0.0.0.0')
