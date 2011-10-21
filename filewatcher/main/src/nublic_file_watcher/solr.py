'''
Created on 12/09/2011

@author: Alejandro Serrano Mena
@copyright: 2011 Nublic
'''

# Information from Solr schema:
# <field name="isFile" type="boolean" indexed="true" stored="true" default="false" />
# <field name="isDir" type="boolean" indexed="true" stored="true" default="false" />
# <field name="path" type="string" indexed="true" stored="true" />
# <field name="filename" type="string" indexed="true" stored="true" />
# <field name="user" type="string" indexed="true" stored="true" />
# <field name="mime" type="string" indexed="true" stored="true" />
# <field name="createdAt" type="date" indexed="true" stored="true" />
# <field name="updatedAt" type="date" indexed="true" stored="true" />
# <field name="tags" type="string" indexed="true" stored="true" multiValued="true" />

import datetime
import httplib2
import magic
import os.path
from sunburnt import SolrInterface

SOLR_URL = "http://localhost:8080/solr"
http_connection = httplib2.Http(cache="/var/tmp/solr_cache")

Magic = magic.open(magic.MAGIC_MIME_TYPE)
Magic.load()

Interface = SolrInterface(url=SOLR_URL, http_connection=http_connection)

def to_utf8(string):
    return unicode(string, 'utf-8')

def from_utf8(string):
    return string.encode('utf-8')

def has_doc(pathname):
    results = Interface.query(path=to_utf8(pathname)).field_limit("path").execute()
    return len(results) > 0

def retrieve_doc(pathname):
    results = Interface.query(path=to_utf8(pathname)).execute()
    if len(results) > 0:
        return FileInfo(results[0])
    else:
        return None

def retrieve_docs_in_dir(path):
    results = Interface.query(path=to_utf8(path + '/*')).execute()
    for result in results:
        if from_utf8(result['path']).startswith(path + '/'):
            # So the folder name does not appear in the middle
            yield FileInfo(result)

def new_doc(pathname, isdir):
    document = { 'isFile': True
               , 'isDir': isdir
               , 'path': to_utf8(pathname)
               , 'filename': to_utf8(os.path.basename(pathname))
               , 'createdAt': datetime.datetime.now()
               }
    return FileInfo(document)

def delete_all_documents():
    Interface.delete_all()
    Interface.commit()

class FileInfo:
    def __init__(self, props):
        self.props = props
    
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
    
    def save(self):
        self.props['updatedAt'] = datetime.datetime.now()
        self.props['mime'] = self.compute_mime_type()
        # Save in Solr
        Interface.add(self.props)
        Interface.commit()
    
    def delete(self):
        if 'id' in self.props: # Don't delete a not saved document
            Interface.delete(self.props['id'])
            Interface.commit()
