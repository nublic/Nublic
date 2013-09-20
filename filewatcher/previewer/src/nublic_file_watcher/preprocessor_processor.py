from preview_processor import PreviewProcessor
from solr_processor import SolrProcessor
from cache_processor import CacheProcessor

import logging
log = logging.getLogger(__name__)


# Set up processors
class PreprocessorProcessor(PreviewProcessor):
    """
    This processor runs SolrProcessor and CacheProcessor on parallel
    """
    def __init__(self):
        PreviewProcessor.__init__(self)
        log.info('Preprocessor processor initialised')
        self.solr = SolrProcessor()
        self.cache = CacheProcessor()

    def get_id(self):
        return 'preprocessor'

    # Inherited methods
    def accept(self, filename, is_dir, info=None):
        return (self.solr.accept(filename, is_dir, info) or
                self.cache.accept(filename, is_dir, info))

    def process_updated(self, filename, is_dir, info=None):
        self.solr.process_updated(filename, is_dir, info)
        self.cache.process_updated(filename, is_dir, info)

    def process_deleted(self, filename, is_dir, info=None):
        self.solr.process_deleted(filename, is_dir, info)
        self.cache.process_deleted(filename, is_dir, info)

    def process_attribs_change(self, filename, is_dir, info=None):
        self.solr.process_attribs_change(filename, is_dir, info)
        self.cache.process_attribs_change(filename, is_dir, info)

    def process_moved(self, filename_from, filename_to, is_dir, info=None):
        self.solr.process_moved(filename_from, filename_to, is_dir, info)
        self.cache.process_moved(filename_from, filename_to, is_dir, info)
