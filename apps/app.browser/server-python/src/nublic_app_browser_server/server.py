import datetime
from flask import Flask, request, abort, send_file
import os
import os.path
import random
import simplejson as json
import traceback
import string
import pexpect

from nublic.files_and_users import get_all_users, Mirror, WorkFolder, create_mirror, create_work_folder
from nublic_server.helpers import init_bare_nublic_server, require_user, makedirs, permissionRead, permissionWrite, PermissionError,\
    tryWrite

# Init app
app = Flask(__name__)
app.debug = True
init_bare_nublic_server(app, '/var/log/nublic/nublic-app-browser.python.log')
app.logger.error('Starting browser app')

DATA_ROOT = '/var/nublic/data/'


@app.route('/devices')
def devices():
    require_user()
    return None
'''
/devices
* Returns:
  return_value ::= [ device ]
  device       ::= { "id": $id, "kind": kind, "name": $name, "owner": true | false }
  kind         ::= "mirror" | "synced" | "media"
'''

def get_folders(depth, path, user):
    subfolders = []
    if depth != 0:
        for folder in os.listdir(path):
            if os.path.isdir(folder) :
                subfolders = subfolders + get_folders(depth-1, folder)
    name = os.path.basename(path)
    return {'name' : name, "subfolders": subfolders, "writable"}
    
    
@app.route('/folders/<int:depth>/<path>')
def folders(depth, path):
    user = require_user()
    pathAbsolute = DATA_ROOT + path
    if depth <= 0:
        abort(400)
    if (not os.path.isdir(pathAbsolute)):
        abort(404)
    return get_folders(depth, pathAbsolute)


'''
/folders/:depth/:path
* :depth  -> Depth of the subfolders to retrieve (> 0)
* :path   -> path you want the subfolders
* Returns:
  return_value ::= [ folder ]
                 |   null  # if the path does not exist
  folder       ::= { "name": $name, "subfolders": [ folder ], "writable": $can_write }
'''

def get_file_info(path, resource):
    info = []
    info['name'] = path
    if os.path.isdir(path + resource):
        info['mime'] = 'application/x-directory'
    else: # TODO
        pass
    pass


@app.route('/files/<path>')
def files(path):
    require_user()
    internalPath = DATA_ROOT + path
    if os.path.exists(internalPath):
        # TODO
        dirs = os.listdir(internalPath)
    else:
        abort(404)
    
    pass
'''
/files/:path
* :path -> path of the folder to obtain files from
* Returns:
  return_value ::= [ file ]
                 |   null  # if the path does not exist
  file         ::= { "name": $name, "mime": $mime_type, "view": $view, "writable": $can_write }
               -> we know it\'s a directory if mime == "application/x-directory"
               -> view indicates which preview you have for the item: now pdf, png, mp3, flv
                  -> if "null", there is no preview available
'''

@app.route('/thumbnail/<file>')
def thumbnail():
    pass

'''
/thumbnail/:file
* :file -> File to show the thumbnail (Nublic path)
* Returns: the raw image data
  -> only try to get things with thumbnail
'''

@app.route('/generic-thumbnail/<mime>')
def generic_thumbnail(mime):
    pass
'''
/generic-thumbnail/:mime
* :id -> Identifier of mime type
* Returns: generic image for that mime type
'''

@app.route('/zip/<path>')
def zip_path():
    pass
'''
/zip/:path
* :path -> folder you want to get as compressed file
* Returns: the data of the zip
'''
@app.route('/zip-set', methods=['POST'])
def zip_set():
    pass
'''
POST /zip-set
* :files -> set of files separated by :
* Returns: the data as zip
'''

@app.route('/raw/<file>')
def raw():
    pass

'''
/raw/:file
* :file -> File to get raw contents (Nublic path)
* Returns: raw data for the file
'''

@app.route('/view/<file>.<type>')
def view():
    pass

'''
/view/:file.:type
* :type -> Kind of view to get (pdf, png, mp3, flv)
* :file -> File to get view
* Returns: data of the view
  - 403 if a folder is requested
  - 404 if there is no such view for that file
'''

@app.route('/rename', methods=['POST'])
def rename():
    ''' Handle this petition:
            POST /rename
            * :from -> file to change the name
            * :to -> new name of the file
    '''
    user = require_user().get_user_uid()
    path = DATA_ROOT + request.form.get('path')
    if not os.path.exists(path)
        abort(500)
    try:
        tryWrite(path, user)
        os.rename(path + request.form.get('name'))
    pass

@app.route('/move', methods=['POST'])
def move():
    ''' Handle this petition:
        POST /move or POST /copy
        * :files -> files to move or copy
        * :target -> folder to put the files
    '''
    internalFrom = DATA_ROOT + request.form.get('from')
    internalTo = DATA_ROOT + request.form.get('to')
    user = require_user()
    if not os.path.exists(internalFrom):
        abort(404)
    if not permissionRead(internalFrom, user.get_user_uid()):
        abort(401)
    if not permissionWrite(os.path.dirname(internalTo))
        abort(401)
    try:
        makedirs(os.path.dirname(internalTo),0777, user.get_user_uid())
        os.rename(internalFrom, internalTo)
    except PermissionError:
        abort(401)
    return 'ok'

@app.route('/copy', methods=['POST'])
def copy():
    ''' Handle this petition:
        POST /move or POST /copy
        * :files -> files to move or copy separated by # TODO
        * :target -> folder to put the files
    '''
    internalFrom = DATA_ROOT + request.form.get('from')
    internalTo = DATA_ROOT + request.form.get('to')
    user = require_user()
    if not os.path.exists(internalFrom):
        abort(404)
    if not permissionRead(internalFrom, user.get_user_uid()):
        abort(401)
    if not permissionWrite(os.path.dirname(internalTo))
        abort(401)
    try:
        makedirs(os.path.dirname(internalTo),0777, user.get_user_uid())
        os.copy(internalFrom, internalTo)
    except PermissionError:
        abort(401)
    return 'ok'

'''
POST /move or POST /copy
* :files -> files to move or copy
* :target -> folder to put the files
'''

@app.route('/delete', methods=['POST'])
def delete():
    pass

'''
POST /delete
* :files -> files to delete
'''

@app.route('/changes/<date>/<path>')
def changes():
    pass


'''
GET /changes/:date/:path
* :date -> date since we want changes to be reported
* :path -> path we are looking at to see changes
* Returns:
  return_value ::= { "new_files": [ file, file, ... ]  // files as above
                   , "deleted_files": [ filename, filename, ... ]  // just strings
                   }
'''

@app.route('/new-folder', methods=['POST'])
def new_folder():
    ''' Handle
        POST /new-folder
        * :name -> name of the new folder to create
        * :path -> folder where it will be created
        * Returns: nothing, or error 500 if something erroneous happens
    '''
    user = require_user().get_user_uid()
    path = DATA_ROOT + request.form.get('path')
    if not os.path.exists(path)
        abort(500)
    try:
        tryWrite(path, user)
        os.mkdir(path + request.form.get('name'))
        return 'ok'
    except PermissionError:
        abort(500)

@app.route('/upload', methods=['POST'])
def upload():
    pass

'''
POST /upload
* :name -> name of the new file
* :path -> folder where it will be uploaded
* :contents -> POST argument with the file contents (in multipart/form-data format)
* Returns: nothing, or error 500 if something erroneous happens
'''


@app.route('/about')
def about():
    return 'Nublic Server v0.0.3'


if __name__ == '__main__':
    app.run()
