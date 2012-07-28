from nublic.filewatcher import init_watcher, Processor
from nublic.files_and_users import get_all_mirrors

import logging
#import sys

# Init app
from flask import Flask, request
app = Flask(__name__)

# Set up logging handlers
handler = logging.FileHandler('/var/log/nublic/nublic-app-photos.python.log')
handler.setFormatter(logging.Formatter(
    '%(asctime)s %(levelname)s: %(message)s '
    '[in %(pathname)s:%(lineno)d]'
))
app.logger.addHandler(handler)

# Set up processors
class NothingProcessor(Processor):
    def __init__(self, watcher):
        Processor.__init__(self, 'nothing', watcher, False)

    def process(self, change):
        app.logger.error('Nothing processor: %s', change)
init_watcher('Photos', [lambda w: NothingProcessor.start(w)], app.logger)

app.logger.error('Starting photos app')

@app.route('/')
def hello_world():
    auth = request.authorization
    return 'Hello ' + auth.username

@app.route('/mirrors')
def mirrors():
    return str(get_all_mirrors())

if __name__ == '__main__':
    app.run()