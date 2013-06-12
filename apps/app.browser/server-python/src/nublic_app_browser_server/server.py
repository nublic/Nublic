
import os.path
import simplejson as json
import time
import shutil
from flask import Flask, request, abort, send_file
from flask.helpers import send_from_directory

import nublic_server
from nublic_server.helpers import (init_bare_nublic_server,
                                   require_user)
from nublic_server.files import (PermissionError, get_file_info, get_folders,
                                 get_last_dir_name)
from nublic.files_and_users.mirror import get_all_mirrors
from nublic.files_and_users.work_folder import get_all_work_folders
from nublic.filewatcher import Processor, init_socket_watcher
from nublic.filewatcher.change import FileChange
from nublic_file_watcher import file_info
from tempfile import NamedTemporaryFile
from zipfile import ZipFile
from werkzeug.utils import secure_filename
from nublic_server.places import get_cache_folder, get_cache_view
import re
import logging


# Init app
app = Flask(__name__)
app.debug = True
init_bare_nublic_server(app, '/var/log/nublic/nublic-app-browser.python.log')
log = app.logger
log.setLevel(logging.DEBUG)
log.info('Starting browser app')

DATA_ROOT = '/var/nublic/data/'  # It MUST end with '/' for security reasons
GENERIC_THUMB_PATH = '/var/lib/nublic/apache2/apps/browser/generic-thumbnails/'

# Place to save events from filewatcher


class BrowserProcessor(Processor):
    def __init__(self):
        Processor.__init__(self, "browser")

    def process(self, change):
        global WATCHER_EVENTS
        t = time.time() * 1000
        if (change.kind == FileChange.CREATED or
            change.kind == FileChange.MODIFIED or
            change.kind == FileChange.DELETED or
                change.kind == FileChange.MOVED):
            WATCHER_EVENTS.append({'change': change,
                                   'time': t
                                   })
        #WATCHER_EVENTS = [w for w in WATCHER_EVENTS
                          #if w['time'] > t - 5 * 60 * 1000  # 5 min after
                          #]
        log.info("Event captured %s at %s", change, str(t))


WATCHER_EVENTS = []
init_socket_watcher("browser", [BrowserProcessor.start()])


@app.route('/devices')
def devices():
    '''
    /devices
    * Returns:
      return_value ::= [ device ]
      device       ::= { "id": $id, "kind": kind, "name": $name,
                          "owner": true | false }
      kind         ::= "mirror" | "synced" | "media"
    '''
    mirrors = get_all_mirrors()
    work_folders = get_all_work_folders()
    devs = [dev.as_map() for dev in mirrors + work_folders]
    return json.dumps(devs)


@app.route('/folders/<int:depth>/<path:path>')
def folders(depth, path):
    '''
    /folders/:depth/:path
    * :depth  -> Depth of the subfolders to retrieve (> 0)
    * :path   -> path you want the subfolders
    * Returns:
      return_value ::= [ folder ]
                     |   null  # if the path does not exist
      folder       ::= { "name": $name, "subfolders": [ folder ],
                                      "writable": $can_write }
    '''
    if depth <= 0:
        abort(400)
    user = require_user()
    path_absolute = os.path.join(DATA_ROOT, path.encode('utf8'))
    if not os.path.isdir(path_absolute):
        abort(404)
    return json.dumps(get_folders(depth, path_absolute, user))


@app.route('/files/<path:path>')
def files_path(path):
    '''
    /files/:path
    * :path -> path of the folder to obtain files from
    * Returns:
      return_value ::= [ file ]
                     |   null  # if the path does not exist
      file         ::= { "name": $name, "writable": $can_write,
                         "last_update": $time, "size": $size,
                         "thumb": $has_thumb, "mime": $mime_type,
                         "view": $view }
            -> we know it's a directory if mime == "application/x-directory"
            -> view indicates which preview you have for the item:
                -> now pdf, png, mp3, flv, txt
                -> if "null", there is no preview available
                -> Size in bytes
    '''
    user = require_user()
    path_absolute = os.path.join(DATA_ROOT, path.encode('utf8'))
    try:
        if not os.path.exists(path_absolute):
            abort(404)
        else:
            app.logger.info('Getting files from %s', path_absolute)
            user.try_read(path_absolute)
            dirs = os.listdir(path_absolute)
            infos = [get_file_info(os.path.join(path_absolute, p), user)
                     for p in dirs]
    except PermissionError:
        abort(401)
    return json.dumps(infos)


@app.route('/thumbnail/<path:path>')
def thumbnail(path):
    '''
    /thumbnail/:file
    * :file -> File to show the thumbnail (Nublic path)
    * Returns: the raw image data
      -> only try to get things with thumbnail
    '''
    user = require_user()
    path_absolute = os.path.join(DATA_ROOT, path.encode('utf8'))
    if user.can_read(path_absolute):  # @TODO: Rely a generic one if not exists
        return send_file(os.path.join(get_cache_folder(path_absolute),
                                      "thumbnail.png"))
    else:
        abort(404)

directory_mapping = {
    'image': 'folder.png',
    'preview': '',
    'mimes': ["application/x-directory"]
}
images_mapping = {
    'image': 'image.png',
    'preview': 'jpg',
    'mimes': file_info.image_view_mimes
}
audio_mapping = {
    'image': "audio.png",
    'preview': 'mp3',
    'mimes': file_info.audio_view_mimes
}
word_mapping = {
    'image': "document.png",
    'preview': 'pdf',
    'mimes': file_info.word_mimes
}
spreadsheet_mapping = {
    'image': 'spreadsheet.png',
    'preview': 'pdf',
    'mimes': file_info.spreadsheet_mimes
}
presentation_mapping = {
    'image': 'presentation.png',
    'preview': 'pdf',
    'mimes': file_info.presentation_mimes
}
drawing_mapping = {
    'image': 'drawing.png',
    'preview': 'pdf',
    'mimes': file_info.drawing_mimes
}
video_mapping = {
    'image': 'video.png',
    'preview': 'mp4',
    'mimes': file_info.video_mimes
}


@app.route('/generic-thumbnail/<path:mime>')
def generic_thumbnail(mime):
    '''
    /generic-thumbnail/:mime
    * :id -> Identifier of mime type
    * Returns: generic image for that mime type
    '''
    mappings = [directory_mapping, images_mapping, audio_mapping]
    # @TODO: Non Djvu or similar supported. It needs refactoring to filewatcher
    try:
        for mapping in mappings:
            if mime in mapping['mimes']:
                thumb = mapping['image']
                return send_from_directory(GENERIC_THUMB_PATH, thumb)
    except KeyError:
        log.exception('KeyError found looking for generic thumbnail')
    thumb = "file.png"
    return send_from_directory(GENERIC_THUMB_PATH, thumb)


def prepare_zip_file():
    ''' Create a zip file that will be deleted when garbage collected '''
    tmp = NamedTemporaryFile(mode='w+b', suffix='.zip', delete=False)
    zip_file = ZipFile(tmp, 'w', allowZip64=True)
    return zip_file


def add_file_zip(zip_file, absolute, base, user):
    ''' Zip a file or folder recursively with relative paths to base'''
    if os.path.isdir(absolute):
        files = os.listdir(absolute)
        for f in files:
            f_absolute = os.path.join(absolute, f)
            user.try_read(f_absolute)
            add_file_zip(zip_file, f_absolute, base, user)
    else:
        user.try_read(absolute)
        archive_name = os.path.relpath(absolute, base)
        zip_file.write(absolute, archive_name)
    return zip_file


@app.route('/zip/<path:path>')
def zip_path(path):
    '''
    /zip/:path
    * :path -> folder you want to get as compressed file
    * Returns: the data of the zip
    '''
    user = require_user()
    internal_path = os.path.join(DATA_ROOT, path.encode('utf8'))
    try:
        zip_file = prepare_zip_file()
        add_file_zip(zip_file, internal_path,
                     os.path.dirname(internal_path), user)
        zip_file.close()
        return send_file(
            zip_file.filename,
            mimetype='application/zip',
            as_attachment=True,
            attachment_filename=get_last_dir_name(internal_path) + '.zip')
    except PermissionError:
        abort(401)


@app.route('/zip-set', methods=['POST'])
def zip_set():
    '''
    POST /zip-set
    * :files -> set of files separated by :
    * Returns: the data as zip
    '''
    user = require_user()
    files = request.form.get('files').split(':')
    try:
        zip_file = prepare_zip_file()
        for file_zip in files:
            abs_file = os.path.join(DATA_ROOT, file_zip.encode('utf8'))
            add_file_zip(zip_file, abs_file,
                         os.path.dirname(abs_file), user)
        zip_file.close()
        return send_file(
            zip_file.filename,
            mimetype='application/zip',
            as_attachment=True,
            attachment_filename='files.zip')

    except PermissionError:
        abort(401)


@app.route('/raw/<path:file_raw>')
def raw(file_raw):
    '''
    /raw/:file
    * :file -> File to get raw contents (Nublic path)
    * Returns: raw data for the file
    '''
    user = require_user()
    internal_path = os.path.join(DATA_ROOT, file_raw.encode('utf8'))
    try:
        user.try_write(internal_path)
        return send_file(internal_path, as_attachment=True)
    except PermissionError:
        abort(401)


@app.route('/view/<path:file_path_complete>')
def view(file_path_complete):
    '''
    /view/:file.:type
    * :type -> Kind of view to get (pdf, png, mp3, flv)
    * :file -> File to get view
    * Returns: data of the view
      - 403 if a folder is requested
      - 404 if there is no such view for that file
    '''
    file_path, extension = os.path.splitext(file_path_complete)
    extension = extension.replace(".", "", 1)
    if not re.match('\w+', extension):
        abort(401)
    internal_path = os.path.join(DATA_ROOT, file_path.encode('utf8'))
    view = get_cache_view(internal_path, extension)
    if view:
        return send_file(view)
    else:
        abort(404)


@app.route('/rename', methods=['POST'])
def rename():
    ''' Handle this petition:
            POST /rename
            * :from -> file to change the name
            * :to -> new name of the file
    '''
    user = require_user()
    path_from = os.path.join(
        DATA_ROOT, request.form.get('from').encode('utf8'))
    path_to = os.path.join(DATA_ROOT, request.form.get('to').encode('utf8'))
    if not os.path.exists(path_from):
        abort(404)
    if not user.can_write(path_from):
        abort(401)
    # Rename does not need to change the user
    os.rename(path_from, path_to)


@app.route('/move', methods=['POST'])
def move():
    ''' Handle this petition:
        POST /move
        * :files -> files to move
        * :target -> folder to put the files
    '''
    user = require_user()
    from_array = request.form.get('files').split(':')
    internal_to = os.path.join(
        DATA_ROOT, request.form.get('target').encode('utf8'))
    try:
        for from_path in from_array:
            internal_from = os.path.join(DATA_ROOT, from_path.encode('utf8'))
            user.try_write(internal_to)
            shutil.move(internal_from, internal_to)
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
    user = require_user()
    from_array = request.form.get('files').split(':')
    internal_to = os.path.join(
        DATA_ROOT, request.form.get('target').encode('utf8'))
    try:
        for from_path in from_array:
            internal_from = os.path.join(DATA_ROOT, from_path.encode('utf8'))
            nublic_server.files.copy(internal_from, internal_to, user)
    except PermissionError:
        abort(401)
    return 'ok'


@app.route('/delete', methods=['POST'])
def delete():
    ''' Handle this petition:
        POST /delete
        * :files -> files to delete
    '''
    raw_files = request.form.get('files').split(':')
    user = require_user()
    try:
        for raw_file in raw_files:
            internal_path = os.path.join(DATA_ROOT, raw_file.encode('utf8'))
            user.try_write_recursive(internal_path)
            if os.path.isdir(internal_path):
                shutil.rmtree(internal_path)
            else:
                os.remove(internal_path)
    except PermissionError:
        abort(401)
    except:  # Catch a possible rmtree Exception
        log.exception("Exception on delete file %s", unicode(raw_files))
        abort(500)
    return 'ok'


@app.route('/changes/<date>/<path>')
def changes(date, path):
    '''
    GET /changes/:date/:path
    * :date -> date since we want changes to be reported
    * :path -> path we are looking at to see changes
    * Returns:
      return_value ::= { "new_files": [ file, file, ... ]  // files as above
                       , "deleted_files": [ filename, ... ]  // just strings
                       }
    '''
    new_files = []
    deleted_files = []
    for event in WATCHER_EVENTS:
        if event['time'] >= date:
            if event['change'].kind == FileChange.CREATED:
                new_files.append(event['change'].filename)
            elif event['change'].kind == FileChange.DELETED:
                deleted_files.append(event['change'].filename)
            elif event['change'].kind == FileChange.MOVED:
                new_files.append(event['change'].filename_to)
                deleted_files.append(event['change'].filename_from)
    return json.dumps({'new_files': new_files, 'deleted_files': deleted_files})


@app.route('/new-folder', methods=['POST'])
def new_folder():
    ''' Handle
        POST /new-folder
        * :name -> name of the new folder to create
        * :path -> folder where it will be created
        * Returns: nothing, or error 500 if something erroneous happens
        It only creates ONE LEVEL NEW FOLDERS
    '''
    user = require_user()

    path = os.path.join(DATA_ROOT, request.form.get('path').encode('utf8'),
                        request.form.get('name').encode('utf8'))
    try:
        nublic_server.files.mkdir(path, user)
        return 'ok'
    except PermissionError:
        abort(500)


def stay_in_directory(path_check, path_base):
    ''' Check if the join stays in the directory (security reasons)
        reference: http://stackoverflow.com/a/2664652/1729524
    '''
    full = os.path.abspath(os.path.join(path_base, path_check))
    return full[:len(path_base)] == path_base


@app.route('/upload', methods=['POST'])
def upload():
    '''
    POST /upload
    * :name -> name of the new file
    * :path -> folder where it will be uploaded
    * :contents -> POST argument with the file contents
                    (in multipart/form-data format)
    * Returns: nothing, or error 500 if something erroneous happens
    '''
    user = require_user()
    file_request = request.files['Filedata']
    try:
        if file_request:  # All extensions are allowed
            filename = secure_filename(request.form.get('name'))
            path = request.form.get('path').encode('utf8')
            abs_path = os.path.join(DATA_ROOT, path)
            if stay_in_directory(path, DATA_ROOT):
                user.try_write(abs_path)  # Check permission write in directory
                file_request.save(os.path.join(abs_path, filename))
            else:
                abort(401)
            return 'ok'
    except PermissionError:
        abort(401)


@app.route('/about')
def about():
    ''' Returns our Nublic browser version!!'''
    return 'Nublic Server v0.0.3'


if __name__ == '__main__':
    app.run()
