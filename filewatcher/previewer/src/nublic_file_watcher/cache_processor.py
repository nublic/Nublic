"""
Cache processor deals with creating the folders for cache,
moving them and deleting them when required
"""
import os
import os.path
import shutil

from preview_processor import PreviewProcessor
from nublic_server.places import (get_cache_folder,
                                  ensure_cache_folder)

import logging
log = logging.getLogger(__name__)


# Set up processors
class CacheProcessor(PreviewProcessor):
    """
    This processor must run before any other processor that uses cache
    This ensures that cache directories are created, moved
    or deleted as required
    """
    def __init__(self):
        PreviewProcessor.__init__(self)
        log.info('Cache processor initialised')

    def get_id(self):
        return 'cache'

    # Inherited methods
    def accept(self, filename, is_dir, info=None):
        return not is_dir

    def process_updated(self, filename, is_dir, info=None):
        log.debug("Ensuring cache dir for '%s'", filename)
        if os.path.exists(filename):
            ensure_cache_folder(filename)

    def process_deleted(self, filename, is_dir, info=None):
        self.ensure_cache_dir_deleted(filename)

    def ensure_cache_dir_deleted(self, filename):
        path = get_cache_folder(filename)
        if os.path.exists(path):
            shutil.rmtree(path)

    def process_attribs_change(self, filename, is_dir, info=None):
        if os.path.exists(filename):
            ensure_cache_folder(filename)

    def process_moved(self, filename_from, filename_to, is_dir, info=None):
        self.move_cache_dir(filename_from, filename_to)

    def move_cache_dir(self, filename_from, filename_to):
        src = get_cache_folder(filename_from)
        dst = get_cache_folder(filename_to)
        if os.path.exists(src):
            # Symlinks will be broken and must be fixed by processors
            os.rename(src, dst)

    def is_hidden(self, path):
        """
        path is an byte string in utf8
        """
        filename = os.path.basename(path)
        return filename.endswith('~') or filename.startswith('.')
