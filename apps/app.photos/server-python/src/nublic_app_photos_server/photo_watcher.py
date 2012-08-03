import os
import os.path

from nublic.filewatcher import FileChange, Processor
from model import db, Photo, Album, PhotoAlbum

# Set up processors
class PhotoProcessor(Processor):
    def __init__(self, logger, watcher):
        Processor.__init__(self, 'nothing', watcher, False, logger)
        self._db = db

    def process(self, change):
        self.get_logger().error('Photo processor: %s', change)
        if change.kind == FileChange.CREATED and not change.is_dir:
            self.process_updated_file(change.filename, change.context)
        elif change.kind == FileChange.MODIFIED and not change.is_dir:
            self.process_updated_file(change.filename, change.context)
        elif change.kind == FileChange.DELETED and not change.is_dir:
            self.process_deleted_file(change.filename)
        elif change.kind == FileChange.MOVED:
            if change.is_dir:
                self.process_moved_folder(change.filename_from, change.filename_to, change.context)
            else:
                self.process_moved_file(change.filename_from, change.filename_to, change.context)
    
    def is_hidden(self, path):
        filename = os.path.basename(path)
        return filename.endswith('~') or filename.startswith('.')
    
    def process_updated_file(self, filename, context):
        pass
    
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
            if os.path.isdir(file_from):
                self.process_moved_folder(file_from, file_to, context)
            else:
                self.process_moved_file(file_from, file_to, context)
    
    def process_moved_file(self, from_, to, context):
        photo = Photo.query.filter_by(file=from_).first()
        if photo != None:
            photo.file = to
            db.session.commit()
        else:
            process_updated_file(to, context)