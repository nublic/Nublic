import mad
import mutagen
import re

class SongInfo:
    def __init__(self, title, artist, album, length, year, track, disc_no):
        self.title = title
        self.artist = artist
        self.album = album
        self.length = length
        self.year = year
        self.track = track
        self.disc_no = disc_no
    
    def has_important_info_missing(self):
        return self.title == None or self.title == '' or \
            self.artist == None or self.artist == '' or \
            self.album == None or self.album == ''
    
    def clone(self):
        return SongInfo(self.title, self.artist, self.album, self.length, self.year, self.track, self.disc_no)
    
    def clean(self):
        title = self.title
        match = re.match(r'(?i)track (\d+)', title)
        if match != None:
            track_no = int(match.group(1))
            return SongInfo(None, self.artist, self.album, self.length, self.year, track_no, self.disc_no)
        else:
            return self.clone()

def merge_song_infos(s1, s2):
    title = _merge_info(s1.title, s2.title)
    artist = _merge_info(s1.artist, s2.artist)
    album = _merge_info(s1.album, s2.album)
    length = _merge_info(s1.length, s2.length)
    year = _merge_info(s1.year, s2.year)
    track = _merge_info(s1.track, s2.track)
    disc_no = _merge_info(s1.disc_no, s2.disc_no)
    return SongInfo(title, artist, album, length, year, track, disc_no)

def _merge_info(i1, i2):
    if i1 != None and i1 != '':
        return i1
    else:
        return i2

# EXTRACTORS
# ==========

def empty_song_info():
    return SongInfo('', '', '', '', '', '', '')

def extract_using_mutagen(file_):
    try:
        madf = mad.MadFile(file_)
        length = madf.total_time() // 1000
        mutf = mutagen.File(file_, easy=True)
        if 'title' in mutf:
            title = mutf['title']
        else:
            title = None
        if 'artist' in mutf:
            artist = mutf['artist']
        else:
            artist = None
        if 'album' in mutf:
            album = mutf['album']
        else:
            album = None
        if 'date' in mutf:
            year = int(mutf['date'].split('-')[0])
        else:
            year = None
        if 'tracknumber' in mutf:
            track_no = int(mutf['tracknumber'].split('/')[0])
        else:
            track_no = None
        if 'discnumber' in mutf:
            disc_no = mutf['discnumber']
        else:
            disc_no = None
        return SongInfo(title, artist, album, length, year, track_no, disc_no)
    except:
        return empty_song_info()

def extract_using_echonest(file_):
    pass
        