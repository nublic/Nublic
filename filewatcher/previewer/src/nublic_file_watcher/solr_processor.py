import datetime
import httplib2
import magic
import os
import os.path
from sunburnt import SolrInterface
from nublic.filewatcher import FileChange, Processor

def to_utf8(self, string):
    return unicode(string, 'utf-8')

def from_utf8(self, string):
    return string.encode('utf-8')

class FileInfo:
    def __init__(self, props, interface):
        self.props = props
        self.Magic = magic.open(magic.MAGIC_MIME_TYPE)
        self.Magic.load()
        self.interface = interface
    
    def compute_mime_type(self):
        path = from_utf8(self.props['path'])
        mime = Magic.file(path)
        extension = os.path.splitext(path)[1].lower()
        if mime == "application/zip": # for Office XML docs
            if extension == ".docx":
                mime = "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            elif extension == ".xlsx":
                mime = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            elif extension == ".pptx":
                mime = "application/vnd.openxmlformats-officedocument.presentationml.presentation"
        elif mime == "application/vnd.ms-office": # for Office non-XML docs
            if extension == ".doc":
                mime = "application/msword"
            elif extension == ".xls":
                mime = "application/msexcel"
            elif extension == ".ppt":
                mime = "application/mspowerpoint"
        elif mime == "application/x-staroffice" or mime == "application/soffice" or mime == "application/x-soffice":
            if extension == ".sdw":
                mime = "application/vnd.stardivision.writer"
            elif extension == ".sdc":
                mime = "application/vnd.stardivision.calc"
            elif extension == ".sdd":
                mime = "application/vnd.stardivision.impress"
            elif extension == ".sda":
                mime = "application/vnd.stardivision.draw"
        elif mime == "application/octet-stream":
            if extension == ".mp3":
                mime = "audio/mpeg"
        # In any other case
        return mime
    
    def set_new_pathname(self, new_pathname):
        self.props['path'] = to_utf8(new_pathname)
        self.props['filename'] = to_utf8(os.path.basename(new_pathname))
    
    def get_pathname(self):
        return from_utf8(self.props['path'])
    
    def is_directory(self):
        return self.props['isDir']
    
    def save(self, should_recreate_mime):
        self.props['updatedAt'] = datetime.datetime.now()
        path = from_utf8(self.props['path'])
        self.props['size'] = os.path.getsize(path)
        if should_recreate_mime:
            self.props['mime'] = self.compute_mime_type()
        # Save in Solr
        self.interface.add(self.props)
        self.interface.commit()
    
    def delete(self):
        if 'id' in self.props: # Don't delete a not saved document
            self.interface.delete(self.props['id'])
            self.interface.commit()

SOLR_URL = "http://localhost:8080/solr"

class SolrProcessor(Processor):
    def __init__(self, logger, watcher):
        Processor.__init__(self, 'solr', watcher, False, logger)
        self._solr_interface = None
        logger.error('Solr processor initialised')
    
    def get_solr_interface(self):
        if self._solr_interface == None:
            http_connection = httplib2.Http(cache="/var/tmp/solr_cache")
            self._solr_interface = SolrInterface(url=SOLR_URL, http_connection=http_connection)
        return self._solr_interface

    def has_doc(self, pathname):
        results = self.get_solr_interface().query(path=to_utf8(pathname)).field_limit("path").execute()
        return len(results) > 0

    def retrieve_doc(self, pathname):
        results = self.get_solr_interface().query(path=to_utf8(pathname)).execute()
        if len(results) > 0:
            return FileInfo(results[0], self.get_solr_interface())
        else:
            return None

    def retrieve_docs_in_dir(self, path):
        results = self.get_solr_interface().query(path=to_utf8(path + '/*')).execute()
        for result in results:
            if from_utf8(result['path']).startswith(path + '/'):
                # So the folder name does not appear in the middle
                yield FileInfo(result, self.get_solr_interface())

    def new_doc(self, pathname, isdir):
        document = { 'isFile': True
                   , 'isDir': isdir
                   , 'path': to_utf8(pathname)
                   , 'filename': to_utf8(os.path.basename(pathname))
                   , 'createdAt': datetime.datetime.now()
                   }
        return FileInfo(document, self.get_solr_interface())

    def delete_all_documents(self):
        self.get_solr_interface().delete_all()
        self.get_solr_interface().commit()

    def process(self, change):
        if change.kind == FileChange.CREATED:
            self.process_updated_file(change.filename, change.is_dir, True)
        elif change.kind == FileChange.MODIFIED:
            self.process_updated_file(change.filename, change.is_dir, True)
        elif change.kind == FileChange.ATTRIBS_CHANGED:
            self.process_updated_file(change.filename, change.is_dir, False)
        elif change.kind == FileChange.DELETED:
            self.process_deleted_file(change.filename)
        elif change.kind == FileChange.MOVED:
            self.process_moved_file(change.filename_from, change.filename_to, change.is_dir)

    def process_updated_file(self, filename, is_dir, should_recreate_preview):
        # Check if it is itself in Solr
        file_info = self.retrieve_doc(filename)
        if file_info == None:
            # Create new Solr document
            file_info = self.new_doc(filename, is_dir)
            file_info.save(True)
            # If it is a directory, search all files inside
            if is_dir:
                for f in os.listdir(filename):
                    inner_fname = os.path.join(filename, f)
                    self.process_updated_file(self, inner_fname, os.path.isdir(inner_fname), should_recreate_preview)
        else:
            # Recreate Solr info
            file_info.save(should_recreate_preview)
    
    def process_deleted_file(self, filename):
        file_info = solr.retrieve_doc(filename)
        if file_info != None:
            file_info.delete()
    
    def process_moved_file(self, from_, to, is_dir):
        if solr.has_doc(from_):
            file_info = solr.retrieve_doc(from_)
            file_info.set_new_pathname(to)
            file_info.save(False)
        if is_dir:
            for file_info in self.retrieve_docs_in_dir(dir_name):
                # Change files inside
                file_path = file_info.get_pathname()
                new_file_path = file_path.replace(dir_name, new_dir_name, 1)
                file_info.set_new_pathname(new_file_path)
                file_info.save(False)
