import datetime
import httplib2
#import magic
import os
import os.path
from sunburnt import SolrInterface
from preview_processor import PreviewProcessor
from file_info import FileInfo
#from nublic.filewatcher.change import FileChange

import logging
log = logging.getLogger(__name__)


def to_utf8(text):
    try:
        text = unicode(text, 'utf-8')
    except TypeError:
        return text
    return text


def from_utf8(text):
    if isinstance(text, str):
        log.debug("Not needed from_utf8 call")
        return text
    elif isinstance(text, unicode):
        return text.encode('utf-8')
    else:
        log.error("Not handled type %s", type(text))
        return text


class SolrFileInfo:
    def __init__(self, props, interface):
        self.props = props
        self.info = FileInfo(from_utf8(self.props['path']))
        self.interface = interface

    def set_new_pathname(self, new_pathname):
        self.props['path'] = to_utf8(new_pathname)
        self.props['filename'] = to_utf8(os.path.basename(new_pathname))

    def get_pathname(self):
        return from_utf8(self.props['path'])

    def is_directory(self):
        return self.props['isDir']

    def save(self, should_recreate_mime):
        self.props['updatedAt'] = datetime.datetime.now()
        # path = from_utf8(self.props['path'])
        # self.props['size'] = self.info.size()
        if should_recreate_mime:
            self.props['mime'] = self.info.mime_type()
        # Save in Solr
        self.interface.add(self.props)
        self.interface.commit()

    def delete(self):
        if 'id' in self.props:  # Don't delete a not saved document
            self.interface.delete(self.props['id'])
            self.interface.commit()

SOLR_URL = "http://localhost:8080/solr"


class SolrProcessor(PreviewProcessor):
    def __init__(self):
        self._solr_interface = None

    def get_id(self):
        return 'solr'

    def get_solr_interface(self):
        if self._solr_interface is None:
            http_connection = httplib2.Http(cache="/var/tmp/solr_cache")
            self._solr_interface = SolrInterface(
                url=SOLR_URL, http_connection=http_connection)
        return self._solr_interface

    def has_doc(self, pathname):
        results = self.get_solr_interface(
        ).query(path=pathname).field_limit("path").execute()
        return len(results) > 0

    def retrieve_doc(self, pathname):
        results = self.get_solr_interface(
        ).query(path=to_utf8(pathname)).execute()
        if len(results) > 0:
            return SolrFileInfo(results[0], self.get_solr_interface())
        else:
            return None

    def retrieve_docs_in_dir(self, path):
        results = self.get_solr_interface(
        ).query(path=(path + '/*')).execute()
        for result in results:
            if from_utf8(result['path']).startswith(path + '/'):
                # So the folder name does not appear in the middle
                yield SolrFileInfo(result, self.get_solr_interface())

    def new_doc(self, pathname, isdir):
        document = {'isFile': True, 'isDir': isdir, 'path': to_utf8(pathname),
                    'filename': to_utf8(os.path.basename(pathname)),
                    'createdAt': datetime.datetime.now()
                    }
        return SolrFileInfo(document, self.get_solr_interface())

    def delete_all_documents(self):
        self.get_solr_interface().delete_all()
        self.get_solr_interface().commit()

    def process_attribs_change(self, filename, is_dir, info=None):
        self.update_in_solr(filename, is_dir, False)

    def process_updated(self, filename, is_dir, info=None):
        self.update_in_solr(filename, is_dir, True)

    def update_in_solr(self, filename, is_dir, should_recreate_mime):
        # Check if it is itself in Solr
        file_info = self.retrieve_doc(filename)
        if file_info is None:
            # Create new Solr document
            file_info = self.new_doc(filename, is_dir)
            file_info.save(True)
            # If it is a directory, search all files inside
            if is_dir:
                for f in os.listdir(from_utf8(filename)):
                    inner_fname = os.path.join(from_utf8(filename), f)
                    self.update_in_solr(inner_fname,
                                        os.path.isdir(inner_fname),
                                        should_recreate_mime)
        else:
            # Recreate Solr info
            file_info.save(should_recreate_mime)

    def process_deleted(self, filename, is_dir, info=None):
        file_info = self.retrieve_doc(filename)
        if file_info is not None:
            file_info.delete()

    def process_moved(self, from_, to, is_dir, info=None):
        if self.has_doc(from_):
            file_info = self.retrieve_doc(from_)
            file_info.set_new_pathname(to)
            file_info.save(False)
        if is_dir:
            for file_info in self.retrieve_docs_in_dir(from_):
                # Change files inside
                file_path = file_info.get_pathname()
                new_file_path = file_path.replace(from_, to, 1)
                file_info.set_new_pathname(new_file_path)
                file_info.save(False)
