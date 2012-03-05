def to_utf8(string):
    return unicode(string, 'utf-8')

def from_utf8(string):
    return string.encode('utf-8')