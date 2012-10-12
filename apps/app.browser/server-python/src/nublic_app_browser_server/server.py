import datetime
from flask import Flask, request, abort, send_file
import os
import os.path
import random
import simplejson as json
import traceback
import string
import pexpect

from nublic_server.helpers import init_bare_nublic_server, require_user
from nublic_server.files import tryRead, permissionRead, permissionWrite,\
    PermissionError, makedirs, tryWrite, tryWriteRecursive
from nublic_server import files, places
from string import split
import shutil
from nublic_files_and_users_client.dbus_client import list_mirrors,\
    list_synced_folders
from nublic_server.places import get_mime_type

# Init app
app = Flask(__name__)
app.debug = True
init_bare_nublic_server(app, '/var/log/nublic/nublic-app-browser.python.log')
app.logger.error('Starting browser app')

DATA_ROOT = '/var/nublic/data/'

@app.route('/devices')
def devices():
    '''
    /devices
    * Returns:
      return_value ::= [ device ]
      device       ::= { "id": $id, "kind": kind, "name": $name, "owner": true | false }
      kind         ::= "mirror" | "synced" | "media"
    '''
    user = require_user().get_user_uid()
    mirrors = list_mirrors()
    devices = map(mirrors, lambda s: s.update({'kind':'mirror'}))
    synced = list_synced_folders()
    devices.append(map(synced, lambda s: s.update({'kind':'synced'})))
    return json.dumps(devices)

def get_folders(depth, path, user):
    subfolders = []
    if depth != 0:
        for folder in os.listdir(path):
            if os.path.isdir(folder) and permissionRead(folder, user):
                subfolders = subfolders + get_folders(depth-1, folder, user)
    name = os.path.basename(path)
    return {'name' : name, "subfolders": subfolders, \
             "writable" : permissionWrite(path, user) }
    
@app.route('/folders/<int:depth>/<path>')
def folders(depth, path):
    '''
    /folders/:depth/:path
    * :depth  -> Depth of the subfolders to retrieve (> 0)
    * :path   -> path you want the subfolders
    * Returns:
      return_value ::= [ folder ]
                     |   null  # if the path does not exist
      folder       ::= { "name": $name, "subfolders": [ folder ], "writable": $can_write }
    '''
    if depth <= 0:
        abort(400)
    uid = require_user().get_user_id()
    pathAbsolute = os.path.join(DATA_ROOT, path)
    if ((not os.path.isdir(pathAbsolute)) or (not permissionRead(pathAbsolute, uid))):
        abort(404)
    return get_folders(depth, pathAbsolute, uid)



def get_file_info(path, uid):
    info = []
    info['name'] = os.path.basename(path)
    if os.path.isdir(path):
        info['mime'] = 'application/x-directory'
    else:
        info['mime'] = get_mime_type(path)
    info['writable'] = permissionWrite(path, uid)
    # @todo
    info['view'] = "" # TODO
    pass


@app.route('/files/<path>')
def files_path(path):
    uid = require_user().get_user_uid
    internalPath = DATA_ROOT + path
    try:
        if not os.path.exists(internalPath):
            abort(404)
        else:
            tryRead(internalPath, uid)
            dirs = os.listdir(internalPath)
            info = map(dirs, lambda p: get_file_info(p, uid))
    except PermissionError:
        abort(401)
    return json.dumps(info)
    
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
    # @todo
    pass

'''
/thumbnail/:file
* :file -> File to show the thumbnail (Nublic path)
* Returns: the raw image data
  -> only try to get things with thumbnail
'''

@app.route('/generic-thumbnail/<mime>')
def generic_thumbnail(mime):
    # @todo
    pass

'''
/generic-thumbnail/:mime
* :id -> Identifier of mime type
* Returns: generic image for that mime type
'''

@app.route('/zip/<path>')
def zip_path():
    # @todo
    pass

'''
/zip/:path
* :path -> folder you want to get as compressed file
* Returns: the data of the zip
'''
@app.route('/zip-set', methods=['POST'])
def zip_set():
    # @todo
    pass
'''
POST /zip-set
* :files -> set of files separated by :
* Returns: the data as zip
'''

@app.route('/raw/<file>')
def raw():
    # @todo
    pass

'''
/raw/:file
* :file -> File to get raw contents (Nublic path)
* Returns: raw data for the file
'''

@app.route('/view/<file>.<type>')
def view():
    # @todo
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
    pathFrom = os.path.join(DATA_ROOT, request.form.get('from'))
    pathTo = os.path.join(DATA_ROOT, request.form.get('to'))
    if not os.path.exists(pathFrom):
        abort(404)
    if not permissionWrite(pathFrom, user):
        abort(401)
    # Rename does not need to change the user
    os.rename(pathFrom,pathTo)

@app.route('/move', methods=['POST'])
def move():
    ''' Handle this petition:
        POST /move
        * :files -> files to move
        * :target -> folder to put the files
    '''
    from_array = split(request.form.get('from'),':')
    internalTo = os.path.join(DATA_ROOT, request.form.get('to'))
    uid = require_user().get_user_id()
    try:
        for from_path in from_array:
            internalFrom = os.path.join(DATA_ROOT, from_path)
            tryWrite(internalTo, uid)
            shutil.move(internalFrom, internalTo)
    except PermissionError:
        abort(401)
    return 'ok'

@app.route('/copy', methods=['POST'])
def copy():
    ''' Handle this petition:
         POST /copy
        * :files -> files to move or copy separated by : 
        * :target -> folder to put the files
    '''
    from_array = split(request.form.get('from'),':')
    internalTo = os.path.join(DATA_ROOT, request.form.get('to'))
    uid = require_user().get_user_id()
    try:
        for from_path in from_array:
            internalFrom = os.path.join(DATA_ROOT, from_path)
            files.copy(internalFrom, internalTo, uid)
    except PermissionError:
        abort(401)
    return 'ok'

@app.route('/delete', methods=['POST'])
def delete():
    ''' Handle this petition:
        POST /delete
        * :files -> files to delete
    '''
    raw_files = split(request.form.get('files'),':')
    uid = require_user().get_user_id()
    try:
        for raw_file in raw_files:
            internal_path = os.path.join(DATA_ROOT, raw_file)
            tryWriteRecursive(internal_path, uid)
            #if os.path.isdir(internal_path):
            shutil.rmtree(internal_path)
            #else:
            #os.remove(internal_path)
    except PermissionError:
        abort(401)
    except: # Catch a possible rmtree Exception
        abort(500)
    return 'ok'

@app.route('/changes/<date>/<path>')
def changes():
    # @todo:  
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
        It only creates ONE LEVEL NEW FOLDERS
    '''
    uid = require_user().get_user_uid()
    path = os.path.join(DATA_ROOT, request.form.get('path'),\
                         request.form.get('name'))
    try:
        files.mkdir(path)
        return 'ok'
    except PermissionError:
        abort(500)

@app.route('/upload', methods=['POST'])
def upload():
    # @todo Upload
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
