import os
import os.path

from preview_processor import PreviewProcessor
from pdf import PdfConverter
import logging
log = logging.getLogger(__name__)


# Set up processors
class DocumentProcessor(PreviewProcessor):
    def __init__(self):
        PreviewProcessor.__init__(self)
        log.info('Document processor initialised')

    def get_id(self):
        return 'document'

    # Inherited methods
    def accept(self, filename, is_dir, info=None):
        if is_dir:
            return False
        self.pdf = PdfConverter(filename, info)
        return self.pdf.needs_pdf()

    def process_updated(self, filename, is_dir, info=None):
        self.pdf.generate_pdf()

    def process_deleted(self, filename, is_dir, info=None):
        cache_path = self.pdf.cache_path()
        thumb_path = self.pdf.thumb_path()
        if os.path.exists(cache_path):
            os.remove(cache_path)
        if os.path.exists(thumb_path):
            os.remove(thumb_path)

    def process_attribs_change(self, filename, is_dir, info=None):
        pass

    def process_moved(self, filename_from, filename_to, is_dir, info=None):
        cache_path = self.pdf.cache_path()
        thumb_path = self.pdf.thumb_path()
        self.from_pdf = PdfConverter(filename_from, info)
        if not os.path.exists(cache_path):
            os.rename(self.from_pdf.cache_path(), cache_path)
        if not os.path.exists(thumb_path):
            os.rename(self.from_pdf.thumb_path(), thumb_path)

    def is_hidden(self, path):
        """
        path is an byte string in utf8
        """
        filename = os.path.basename(path)
        return filename.endswith('~') or filename.startswith('.')

    #def remove_view(self, filename):
        ## exists return false for broken symlinks so we need to check is link
        ## before exists to remove the broken symlink
        #mp3_cache = os.path.join(get_cache_folder(filename), 'view.mp3')
        #if os.path.islink(mp3_cache) or os.path.exists(mp3_cache):
            #log.info(
                #"Cache removed for audio file %s as %s", filename, mp3_cache)
            #os.remove(mp3_cache)
        #else:
            #log.warning(
                #"Cache for audio file %s does not exist in %s. Not removed",
                #filename, mp3_cache)
