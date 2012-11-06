
import os.path
import simplejson as json
import shutil
from flask import Flask, request, abort, send_file
from flask.helpers import send_from_directory

from nublic_server.helpers import init_bare_nublic_server, require_uid, \
    require_user
from nublic_server.files import try_read, permission_write, \
    PermissionError, try_write, try_write_recursive, get_file_info, \
    get_folders, try_read_recursive
from nublic_server import files
from nublic_files_and_users_client.dbus_client import list_mirrors, \
    list_synced_folders
from tempfile import NamedTemporaryFile
from zipfile import ZipFile
from werkzeug.utils import secure_filename


# Init app
app = Flask(__name__)
app.debug = True
init_bare_nublic_server(app, '/var/log/nublic/nublic-app-browser.python.log')
app.logger.error('Starting browser app')

DATA_ROOT = '/var/nublic/data/'  # It MUST end with '/' for security reasons
GENERIC_THUMB_PATH = '/var/lib/nublic/apache2/apps/browser/generic-thumbnails'


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
    user = require_user()
    mirrors = list_mirrors()
    synced = list_synced_folders()
    devs = [dev.__setitem__(dev['owner'] == user.username)\
                 for dev in mirrors + synced]
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
    uid = require_uid()
    path_absolute = os.path.join(DATA_ROOT, path)
    if not os.path.isdir(path_absolute):
        abort(404)
    return json.dumps(get_folders(depth, path_absolute, uid))


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
                -> now pdf, png, mp3, flv
                -> if "null", there is no preview available
                -> Size in bytes
    '''
    uid = require_uid()
    path_absolute = os.path.join(DATA_ROOT, path)
    try:
        if not os.path.exists(path_absolute):
            abort(404)
        else:
            app.logger.error('Getting files from %s', path_absolute)
            try_read(path_absolute, uid)
            dirs = os.listdir(path_absolute)
            app.logger.error('Getting files: %s', str(dirs))
            infos = [get_file_info(os.path.join(path_absolute, p), uid) \
                      for p in dirs]
    except PermissionError:
        abort(401)
    return json.dumps(infos)


@app.route('/thumbnail/<path:file>')
def thumbnail():
    '''
    /thumbnail/:file
    * :file -> File to show the thumbnail (Nublic path)
    * Returns: the raw image data
      -> only try to get things with thumbnail
    '''
    # @todo: thumbnail DEPENDS ON PROCESSOR
    pass


@app.route('/generic-thumbnail/<path:mime>')
def generic_thumbnail(mime):
    '''
    /generic-thumbnail/:mime
    * :id -> Identifier of mime type
    * Returns: generic image for that mime type
    '''
    directory_mapping = {'image': "folder.png",
                         'mimes': ["application/x-directory"]}
    images_mapping = {'image': "image.png",
                      'mimes': ["image/bmp", "image/gif", "image/png",
                                "image/jpg", "image/jpeg", "image/pjpeg",
                                "image/svg", "image/x-icon", "image/x-pict",
                                "image/x-pcx", "image/pict",
                                "image/x-portable-bitmap", "image/tiff",
                                "image/x-tiff", "image/x-xbitmap",
                                "image/x-xbm", "image/xbm", "application/wmf",
                                "application/x-wmf", "image/wmf",
                                "image/x-wmf", "image/x-ms-bmp"
                                ]
                      }
    audio_mapping = {'image': "audio.mp3",
                     'mimes': [
                        '''Obtained looking at:
                        - List of files supported by ffmpeg: `ffmpeg -formats`
                        - Information about file extensions: http://filext.com/
                        '''
                                # AAC
                                "audio/aac", "audio/x-aac",
                                # AC3
                                "audio/ac3",
                                # AIFF
                                "audio/aiff", "audio/x-aiff", "sound/aiff",
                                "audio/x-pn-aiff",
                                # ASF
                                "audio/asf",
                                # MIDI
                                "audio/mid", "audio/x-midi",
                                # AU
                                "audio/basic", "audio/x-basic", "audio/au",
                                "audio/x-au", "audio/x-pn-au", "audio/x-ulaw",
                                # PCM
                                "application/x-pcm",
                                # MP4
                                "audio/mp4",
                                # MP3
                                "audio/mpeg", "audio/x-mpeg", "audio/mp3",
                                "audio/x-mp3", "audio/mpeg3", "audio/x-mpeg3",
                                "audio/mpg", "audio/x-mpg",
                                "audio/x-mpegaudio",
                                # WAV
                                "audio/wav", "audio/x-wav", "audio/wave",
                                "audio/x-pn-wav",
                                # OGG
                                "audio/ogg", "application/ogg", "audio/x-ogg",
                                "application/x-ogg",
                                # FLAC
                                "audio/flac",
                                # WMA
                                "audio/x-ms-wma",
                                # Various
                                "audio/rmf", "audio/x-rmf", "audio/vnd.qcelp",
                                "audio/x-gsm", "audio/snd"
                               ]
                     }
    mappings = [directory_mapping, images_mapping, audio_mapping]
    # @todo: Non Djvu or similar supported. It needs refactoring to filewatcher
    try:
        for mapping in mappings:
            if mime in mapping['mimes']:
                thumb = mapping['image']
                return send_from_directory(GENERIC_THUMB_PATH, thumb)
    except KeyError:
        thumb = "file.png"
    return send_from_directory(GENERIC_THUMB_PATH, thumb)


def prepare_zip_file():
    ''' Create a zip file that will be deleted when garbage collected '''
    tmp = NamedTemporaryFile(mode='w+b', suffix='.zip')
    zip_file = ZipFile(tmp, 'w', allowZip64=True)
    return zip_file


def add_file_zip(zip_file, absolute, base, uid):
    ''' Zip a file or folder recursively with relative paths to base'''
    if os.path.isdir(absolute):
        files = os.listdir(absolute)
        for f in files:
            f_absolute = os.path.join(absolute, f)
            try_read(f_absolute, uid)
            add_file_zip(zip_file, f_absolute, base, uid)
    else:
        try_read(absolute, uid)
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
    uid = require_uid()
    internal_path = os.path.join(DATA_ROOT, path)
    try:
        #try_read_recursive(internal_path, uid)
        zip_file = prepare_zip_file()
        add_file_zip(zip_file, internal_path, \
                     os.path.dirname(internal_path), uid)
        zip_file.close()
        return send_file(zip_file.filename)
    except PermissionError:
        abort(401)


@app.route('/zip-set', methods=['POST'])
def zip_set():
    '''
    POST /zip-set
    * :files -> set of files separated by :
    * Returns: the data as zip
    '''
    uid = require_uid()
    files = request.form.get('files').split(':')
    try:
        zip_file = prepare_zip_file()
        for file_zip in files:
            abs_file = os.path.join(DATA_ROOT, file_zip)
            add_file_zip(zip_file, abs_file, \
                         os.path.dirname(abs_file), uid)
        zip_file.close()
        return send_file(zip_file.fp)
    except PermissionError:
        abort(401)


@app.route('/raw/<path:file_raw>')
def raw(file_raw):
    '''
    /raw/:file
    * :file -> File to get raw contents (Nublic path)
    * Returns: raw data for the file
    '''
    uid = require_uid()
    internal_path = os.path.join(DATA_ROOT, file_raw)
    try:
        try_write(internal_path, uid)
        return send_file(internal_path, as_attachment=True)
    except PermissionError:
        abort(401)


@app.route('/view/<path:file>.<type>')
def view():
    '''
    /view/:file.:type
    * :type -> Kind of view to get (pdf, png, mp3, flv)
    * :file -> File to get view
    * Returns: data of the view
      - 403 if a folder is requested
      - 404 if there is no such view for that file
    '''
    # @todo: view DEPENDS ON PROCESSORS
    pass


@app.route('/rename', methods=['POST'])
def rename():
    ''' Handle this petition:
            POST /rename
            * :from -> file to change the name
            * :to -> new name of the file
    '''
    user = require_uid()
    path_from = os.path.join(DATA_ROOT, request.form.get('from'))
    path_to = os.path.join(DATA_ROOT, request.form.get('to'))
    if not os.path.exists(path_from):
        abort(404)
    if not permission_write(path_from, user):
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
    from_array = request.form.get('files').split(':')
    internal_to = os.path.join(DATA_ROOT, request.form.get('target'))
    uid = require_uid()
    try:
        for from_path in from_array:
            internal_from = os.path.join(DATA_ROOT, from_path)
            try_write(internal_to, uid)
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
    from_array = request.form.get('files').split(':')
    internal_to = os.path.join(DATA_ROOT, request.form.get('target'))
    uid = require_uid()
    try:
        for from_path in from_array:
            internal_from = os.path.join(DATA_ROOT, from_path)
            files.copy(internal_from, internal_to, uid)
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
    uid = require_uid()
    try:
        for raw_file in raw_files:
            internal_path = os.path.join(DATA_ROOT, raw_file)
            try_write_recursive(internal_path, uid)
            #if os.path.isdir(internal_path):
            shutil.rmtree(internal_path)
            #else:
            #os.remove(internal_path)
    except PermissionError:
        abort(401)
    except:  # Catch a possible rmtree Exception
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
    # @todo: changes DEPENDS ON PROCESSOR
    pass


@app.route('/new-folder', methods=['POST'])
def new_folder():
    ''' Handle
        POST /new-folder
        * :name -> name of the new folder to create
        * :path -> folder where it will be created
        * Returns: nothing, or error 500 if something erroneous happens
        It only creates ONE LEVEL NEW FOLDERS
    '''
    uid = require_uid()
    path = os.path.join(DATA_ROOT, request.form.get('path'), \
                         request.form.get('name'))
    try:
        files.mkdir(path, uid)
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
    uid = require_uid()
    file_request = request.files['contents']
    try:
        if file_request:  # All extensions are allowed
            filename = secure_filename(request.form.get('name'))
            path = request.form.get('path')
            abs_path = os.path.join(DATA_ROOT, path)
            if stay_in_directory(path, DATA_ROOT):
                try_write(abs_path, uid)  # Check permission write in directory
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
