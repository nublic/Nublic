import datetime
from flask import Flask, request, abort
from pykka.actor import ThreadingActor
import simplejson as json
import traceback

import nublic.market
from nublic_server.helpers import init_bare_nublic_server, require_user

# Init app
app = Flask(__name__)
app.debug = True
init_bare_nublic_server(app, '/var/log/nublic/nublic-app-browser.python.log')
app.logger.error('Starting browser app')

@app.route('/about')
def about():
    return 'Nublic Server v0.0.3'

if __name__ == '__main__':
    app.run()
