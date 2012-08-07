import datetime
import EXIF
import os
import os.path

from nublic.filewatcher import FileChange, Processor
from nublic.files_and_users import get_file_owner, is_file_shared
from nublic_server.places import get_mime_type
from model import db, Album, Artist, Collection, Playlist, Song, SongCollection, SongPlaylist

# Set up processors
class MusicProcessor(Processor):
    def __init__(self, logger, watcher):
        Processor.__init__(self, 'photo', watcher, False, logger)
        logger.error('Photo processor initialised')

    def process(self, change):
        self.get_logger().error('Photo processor file: %s, context: %s', change.filename, change.context)
        if change.kind == FileChange.CREATED and not change.is_dir:
            self.process_updated_file(change.filename, change.context)
        elif change.kind == FileChange.MODIFIED and not change.is_dir:
            self.process_updated_file(change.filename, change.context)
        elif change.kind == FileChange.DELETED and not change.is_dir:
            self.process_deleted_file(change.filename)
        elif change.kind == FileChange.ATTRIBS_CHANGED and not change.is_dir:
            self.process_attribs_change(change.filename, change.context)
        elif change.kind == FileChange.MOVED:
            if change.is_dir:
                self.process_moved_folder(change.filename_from, change.filename_to, change.context)
            else:
                self.process_moved_file(change.filename_from, change.filename_to, change.context)
    
    def is_hidden(self, path):
        filename = os.path.basename(path)
        return filename.endswith('~') or filename.startswith('.')
    
    def process_updated_file(self, filename, context):
        if not self.is_hidden(filename) and get_mime_type(filename).startswith('image/'):
            # Get modification time
            f = open(filename, 'rb')
            tags = EXIF.process_file(f, stop_tag='DateTimeOriginal')
            if 'EXIF DateTimeOriginal' in tags:
                date_as_string = tags['EXIF DateTimeOriginal'].values
                date = datetime.datetime.strptime(date_as_string, '%Y:%m:%d %H:%M:%S')
            else:
                date = datetime.datetime.fromtimestamp(os.path.getmtime(filename))
            f.close()
            # Update or write new
            photo = Photo.query.filter_by(file=filename).first()
            now = datetime.datetime.now()
            owner = get_file_owner(filename).get_username()
            shared = is_file_shared(filename)
            if photo != None:
                photo.date = date
                photo.lastModified = now
                photo.owner = owner
                photo.shared = shared
                db.session.commit()
            else:
                photo = Photo(filename, os.path.basename(filename), date, \
                              datetime.datetime.now(), owner, shared)
                db.session.add(photo)
                db.session.commit()
                # Add to album
                context_path = '/var/nublic/data/' + context[:-1]
                (parent, _basename) = os.path.split(filename)
                (p_parent, p_basename) = os.path.split(parent)
                (_p_p_parent, p_p_basename) = os.path.split(p_parent)
                if context_path == parent:
                    album = None
                elif context_path == p_parent:
                    album = p_basename
                else:
                    album = p_p_basename + '/' + p_basename
                if album != None:
                    ab = get_or_create_album(album)
                    relation = PhotoAlbum(ab.id, photo.id)
                    db.session.add(relation)
                    db.session.commit()
    
    def process_attribs_change(self, filename, context):
        photo = Photo.query.filter_by(file=filename).first()
        if photo != None:
            photo.owner = get_file_owner(filename).get_username()
            photo.shared = is_file_shared(filename)
            db.session.commit()
        else:
            self.process_updated_file(filename, context)
    
    def process_deleted_file(self, filename):
        photo = Photo.query.filter_by(file=filename).first()
        if photo != None:
            PhotoAlbum.query.filter_by(photoId=photo.id).delete()
            db.session.delete(photo)
            db.session.commit()
    
    def process_moved_folder(self, from_, to, context):
        for e in os.listdir(from_):
            file_from = os.path.join(from_, e)
            file_to = os.path.join(to, e)
            if os.path.isdir(file_from) and not self.is_hidden(file_from):
                self.process_moved_folder(file_from, file_to, context)
            else:
                self.process_moved_file(file_from, file_to, context)
    
    def process_moved_file(self, from_, to, context):
        photo = Photo.query.filter_by(file=from_).first()
        if photo != None:
            photo.file = to
            db.session.commit()
        else:
            self.process_updated_file(to, context)