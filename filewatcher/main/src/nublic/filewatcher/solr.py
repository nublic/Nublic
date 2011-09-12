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

class FileInfo:
    def __init__(self, pathname, isdir, doc_id=None):
        self.pathname = pathname
        self.isdir = isdir
        self.id = doc_id
    
    def compute_mime_type(self):
        return Magic.file(self.pathname)
    
    def set_new_pathname(self, new_pathname):
        self.pathname = new_pathname
    
    def save(self):
        now = datetime.datetime.now() # So we have equal createdAt and updatedAt
        document = { 'isFile': True
                   , 'isDir': self.isdir
                   , 'path': unicode(self.pathname, 'utf-8')
                   , 'filename': unicode(os.path.basename(self.pathname), 'utf-8')
                   , 'mime': self.compute_mime_type()
                   , 'updatedAt': now
                   }
        
        if self.id == None: # New document
            document['createdAt'] = now
        else:
            document['id'] = self.id
        # Save in Solr
        Interface.add(document)
        Interface.commit()
