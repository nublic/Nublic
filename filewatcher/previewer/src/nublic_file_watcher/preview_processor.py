class Element:
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
        return (self.change.kind == FileChange.DELETED or \
                self.change.kind == FileChange.ATTRIBS_CHANGED or \
                self.change.kind == FileChange.MOVED)

class PreviewProcessor:
    def __init__(self):
        pass

    def process(self, element):
        raise NotImplementedError("Should be implemented in derived classes")

    def get_id(self):
        raise NotImplementedError("Should be implemented in derived classes")
