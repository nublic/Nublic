'''
:author Alejandro Serrano <alex@nublic.com>
'''


class FileChange:
    '''
    Represents a change in the file system
    '''
    CREATED = 0
    DELETED = 1
    MODIFIED = 2
    ATTRIBS_CHANGED = 3
    MOVED = 4
    SCAN_REPEATED = 5

    def __init__(self, kind, filename, filename_from, filename_to, is_dir):
        '''
        Creates a new file change

        :type kind: one of the elements in the enumeration
        :type filename: string
        :param filename_from: only available in MOVE changes
        :type filename_from: string
        :param filename_to: only available in MOVE changes
        :type filename_to: string
        :type is_dir: boolean
        '''
        self.kind = kind
        self.filename = filename
        self.filename_from = filename_from
        self.filename_to = filename_to
        self.is_dir = is_dir

    #def __str__(self):
        #return self.__unicode__().encode('utf8')

    #def __repr__(self):
        #return self.__str__()

    #def __unicode__(self):
        #return ('FileChange' + ' kind :' + unicode(self.kind) +
                #' file : ' + unicode(self.filename, 'utf8') +
                #' file_from : ' + unicode(self.filename_from, 'utf8')
                #if self.filename_from else "None" +
                #' filename_to : ' + unicode(self.filename_to, 'utf8')
                #if self.filename_to else "None" +
                #' is_dir : ' + unicode(self.is_dir))


def parse_file_change(kind, pathname, src_pathname, is_dir):
    if type(pathname) == unicode:
        pathname = pathname.encode('utf8')
    if type(src_pathname) == unicode:
        src_pathname = src_pathname.encode('utf8')
    if kind == 'create':
        return FileChange(FileChange.CREATED, pathname, None, None, is_dir)
    elif kind == 'delete':
        return FileChange(FileChange.DELETED, pathname, None, None, is_dir)
    elif kind == 'modify':
        return FileChange(FileChange.MODIFIED, pathname, None, None, is_dir)
    elif kind == 'attrib':
        return FileChange(FileChange.ATTRIBS_CHANGED, pathname, None, None, is_dir)
    elif kind == 'move':
        return FileChange(FileChange.MOVED, pathname, src_pathname, pathname, is_dir)
    elif kind == 'repeat':
        return FileChange(FileChange.SCAN_REPEATED, pathname, None, None, is_dir)
    else:
        raise ValueError('Unknown event kind')
