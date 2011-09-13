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

def has_doc(pathname):
    results = Interface.query(path=unicode(pathname, 'utf-8')).execute()
    return len(results) > 0

def retrieve_doc(pathname):
    results = Interface.query(path=unicode(pathname, 'utf-8')).execute()
    return FileInfo(results[0])

def new_doc(pathname, isdir):
    document = { 'isFile': True
               , 'isDir': isdir
               , 'path': unicode(pathname, 'utf-8')
               , 'filename': unicode(os.path.basename(pathname), 'utf-8')
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
        return Magic.file(self.props['path'].encode('utf-8'))
    
    def set_new_pathname(self, new_pathname):
        self.props['path'] = new_pathname
        self.props['filename'] = unicode(os.path.basename(new_pathname), 'utf-8')
    
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
