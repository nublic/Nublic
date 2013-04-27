import datetime
from file_info import FileInfo
from nublic.filewatcher.change import FileChange
#import sys

import logging
log = logging.getLogger(__name__)


def trace_calls(frame, event, arg):
    if event != 'call':
        return
    co = frame.f_code
    func_name = co.co_name
    if func_name == 'write':
        # Ignore write() calls from print statements
        return
    func_line_no = frame.f_lineno
    func_filename = co.co_filename
    caller = frame.f_back
    caller_line_no = caller.f_lineno
    caller_filename = caller.f_code.co_filename
    print 'Call to %s on line %s of %s from line %s of %s' % \
        (func_name, func_line_no, func_filename,
         caller_line_no, caller_filename)
    return


class Element(object):
    '''
    self.path is a byte utf8 object
    '''
    def __init__(self, change):
        self.change = change
        self.path = change.filename
        self.info = FileInfo(self.path)
        self.time = datetime.datetime.now()

    def get_change(self):
        return self.change

    def get_info(self):
        return self.info

    def get_time(self):
        return self.time

    def __le__(self, other):
        if self.path == other.path:
            # Same file, we compare the times
            return self.time <= other.time
        else:
            # Different files
            # First we compare the type of changes
            # (small time changes are better)
            if self.is_short_kind() and not other.is_short_kind():
                return True
            elif other.is_short_kind() and not self.is_short_kind():
                return False
            # Second we compare the size of files
            return self.info.size() <= other.info.size()

    def is_short_kind(self):
        return (self.change.kind == FileChange.DELETED or
                self.change.kind == FileChange.ATTRIBS_CHANGED or
                self.change.kind == FileChange.MOVED)


class PreviewProcessor(object):
    def __init__(self):
        #sys.settrace(trace_calls)
        pass

    def process(self, element):
        change = element.change
        info = element.get_info()
        log.info('Processor %s: Change received: %i %s',
                 self.get_id(), change.kind, change.filename)
        if not self.accept(change.filename, change.is_dir, info):
            return
        if change.kind == FileChange.CREATED:
            self.process_updated(change.filename, change.is_dir, info)
        elif change.kind == FileChange.MODIFIED:
            self.process_updated(change.filename, change.is_dir, info)
        elif change.kind == FileChange.DELETED:
            self.process_deleted(change.filename, change.is_dir)
        elif change.kind == FileChange.ATTRIBS_CHANGED:
            self.process_attribs_change(change.filename, change.is_dir, info)
        elif change.kind == FileChange.MOVED:
            self.process_moved(
                change.filename_from, change.filename_to, change.is_dir)
        log.info('Processor %s: Finish processing file: %i %s',
                 self.get_id(), change.kind, change.filename)

    def process_updated(self, filename, is_dir, info=None):
        """ Process a file changed or created.
        It might be called several times without any changes
        filename is a byte string in utf8
        """
        raise NotImplementedError("Should be implemented in derived classes")

    def process_deleted(self, filename, is_dir, info=None):
        """ Process a file deleted
        filename is a byte string in utf8
        """
        raise NotImplementedError("Should be implemented in derived classes")

    def process_attribs_change(self, filename, is_dir, info=None):
        """ Process change of attributes.
        Attributes are things specified in the os.stat
        filename is a byte string in utf8
        """
        raise NotImplementedError("Should be implemented in derived classes")

    def process_moved(self, filename_from, filename_to, is_dir, info=None):
        """ Process a file changed or created.
        filename_from, filename_to are byte strings in utf8
        """
        raise NotImplementedError("Should be implemented in derived classes")

    def get_id(self):
        raise NotImplementedError("Should be implemented in derived classes")

    def accept(self, filename, is_dir, info=None):
        """ By default everything is accepted. You can override this function
        to skip entirely files. The files for which accept is False
        will not be called by any process_* function
        """
        return True
