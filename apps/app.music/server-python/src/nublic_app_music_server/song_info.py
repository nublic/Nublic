import mad
import mutagen
import os.path
import re
import subprocess

from pyechonest import config
from pyechonest.song import identify

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

def get_song_info(file_, context):
    tag_info = extract_using_mutagen(file_).clean()
    if tag_info.has_important_info_missing():
        echonest_info = extract_using_echonest(file_)
        tag_info = merge_song_infos(tag_info, echonest_info)
    if tag_info.has_important_info_missing():
        file_info = extract_using_filename(file_, context)
        tag_info = merge_song_infos(tag_info, file_info)
    return tag_info

def merge_song_infos(s1, s2):
    if s1 == None:
        return s2
    elif s2 == None:
        return s1
    else:
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
    return SongInfo('', '', '', None, None, None, None)

def extract_using_mutagen(file_):
    # Use Mad for length
    try:
        madf = mad.MadFile(file_)
        length = madf.total_time() // 1000
    except:
        length = None
    # Use Mutagen for tags
    try:
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
    # Get length
    try:
        madf = mad.MadFile(file_)
        length = madf.total_time() // 1000
    except:
        length = None
    # Identify via Echo Nest
    config.ECHO_NEST_API_KEY = 'UR4VKX7JXDXAULIWB'
    config.CODEGEN_BINARY_OVERRIDE = '/usr/bin/echoprint-codegen'
    possible_songs = identify(filename=file_)
    if possible_songs:
        song = possible_songs[0]
        return SongInfo(song.title, song.artist_name, '', length, None, None, None)
    else:
        return empty_song_info()

def extract_using_filename(file_, context):
    # Get length
    try:
        madf = mad.MadFile(file_)
        length = madf.total_time() // 1000
    except:
        length = None
    # Get information from filename
    no_context = file_.replace(context + '/', '', 1)
    parent, filename = os.path.split(no_context)
    # Initialize to empty
    track = None
    title = None
    artist = None
    album = None
    # Try to get track number
    tmatch = re.match(r"(\d+)\.? *(.*)$", filename)
    if tmatch != None:
        track = int(tmatch.group(1))
        filename = tmatch.group(2).strip()
    # Try to parse rest of string
    ar_ab_title_match = re.match(r"\s*(.*)-\s*(.*)-\s*(.*)$", filename)
    ar_title_match = re.match(r"\s*(.*)-\s*(.*)$", filename)
    if ar_ab_title_match != None:
        artist = ar_ab_title_match.group(1).strip()
        album = ar_ab_title_match.group(2).strip()
        title = ar_ab_title_match.group(3).strip()
    elif ar_title_match != None:
        artist = ar_ab_title_match.group(1).strip()
        title = ar_ab_title_match.group(2).strip()
    else:
        title = filename
    # Get information from path
    while parent != '':
        p_parent, p_name = os.path.split(parent)
        if album == None:
            ar_ab_match = re.match(r"\s*(.*)-\s*(.*)$", p_name)
            if ar_ab_match != None:
                artist = ar_ab_match.group(1).strip()
                album = ar_ab_match.group(2).strip()
            else:
                album = p_name.strip()
        else:
            if artist == None:
                artist = p_name.strip()
            else:
                pass
        parent = p_parent
    # Put everythung together
    return SongInfo(title, artist, album, length, None, track, None)