import mad
import mutagen
import os.path
import re

from pyechonest import config
from pyechonest.song import identify

import logging
log = logging.getLogger(__name__)


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
        return self.title is None or self.title == '' or \
            self.artist is None or self.artist == '' or \
            self.album is None or self.album == ''

    def clone(self):
        return SongInfo(self.title, self.artist, self.album, self.length,
                        self.year, self.track, self.disc_no)

    def clean(self):
        title = self.title
        if isinstance(title, basestring):
            match = re.match(r'(?i)track (\d+)', title)
            if match is not None:
                track_no = int(match.group(1))
                return SongInfo(None, self.artist, self.album, self.length,
                                self.year, track_no, self.disc_no)
        return self.clone()


def get_song_info(file_):
    """ Get the best song info possible.
        file_ is in utf8
    """
    log.info('Extracting from mutagen: %s', file_)
    tag_info = extract_using_mutagen(file_).clean()
    if tag_info.has_important_info_missing():
        log.info('Extracting from Echonest: %s', file_)
        echonest_info = extract_using_echonest(file_).clean()
        tag_info = merge_song_infos(tag_info, echonest_info)
    if tag_info.has_important_info_missing():
        log.info('Extracting from filename: %s', file_)
        file_info = extract_using_filename(file_).clean()
        tag_info = merge_song_infos(tag_info, file_info)
    return tag_info


def merge_song_infos(s1, s2):
    if s1 is None:
        return s2
    elif s2 is None:
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
    if i1 is not None and i1 != '':
        return i1
    else:
        return i2

# EXTRACTORS
# ==========


def empty_song_info():
    return SongInfo('', '', '', None, None, None, None)


def extract_using_mutagen(file_):
    """ file_ is an utf8 path
    """
    # Use Mad for length
    try:
        madf = mad.MadFile(file_)
        length = madf.total_time() // 1000
    except:
        length = None
    # Use Mutagen for tags
    try:
        mutf = mutagen.File(file_, easy=True)
        try:
            titles = mutf.get('title', None)
            title = titles[0] if titles is not None else None
            #title = mutf['title'][0]
        except:
            log.exception("Mutagen could not obtain the title")
            title = None
        try:
            artists = mutf.get('artist', None)
            artist = artists[0] if artists is not None else None
            #artist = mutf['artist'][0]
        except:
            log.exception("Mutagen could not obtain the artist")
            artist = None
        try:
            albums = mutf.get('album', None)
            album = albums[0] if albums is not None else None
            #album = mutf['album'][0]
        except:
            log.exception("Mutagen could not obtain the album")
            album = None
        try:
            year_raws = mutf.get('year', None)
            #year_raw = mutf['date'][0]
            year = (int(year_raws[0].split('-')[0]) if year_raws is not None
                    else None)
        except:
            log.exception("Mutagen could not obtain the year")
            year = None
        try:
            track_no_raws = mutf.get('tracknumber', None)
            #track_no_raw = mutf['tracknumber'][0]
            track_no = (int(track_no_raws[0].split('/')[0])
                        if track_no_raws is not None
                        else None)
        except:
            log.exception("Mutagen could not obtain the track_no")
            track_no = None
        try:
            disc_no_s = mutf.get('discnumber', None)
            disc_no = int(disc_no_s[0]) if disc_no_s is not None else None
        except:
            log.exception("Mutagen could not obtain the disc_no")
            disc_no = None
        return SongInfo(title, artist, album, length, year, track_no, disc_no)
    except:
        log.exception("Mutagen could not obtain the information")
        return empty_song_info()


def extract_using_echonest(file_):
    """ file_ is an utf8 pathname
    """
    # Get length
    try:
        madf = mad.MadFile(file_)
        length = madf.total_time() // 1000
    except:
        length = None
        log.exception("Exception detected executing MadFile")
    # Identify via Echo Nest
    config.ECHO_NEST_API_KEY = 'CSVF0WRJPUBLMKNRV'
    config.CODEGEN_BINARY_OVERRIDE = '/usr/bin/echoprint-codegen'
    try:
        possible_songs = identify(filename=file_)
        if possible_songs:
            song = possible_songs[0]
            return SongInfo(song.title, song.artist_name, '', length,
                            None, None, None)
        else:
            log.info("No possible song found")
            return empty_song_info()
    except:
        log.exception("Exception processing Echonest")
        return empty_song_info()


def extract_using_filename(file_):
    """ file_ is an utf8 pathname
    """
    # Get length
    try:
        madf = mad.MadFile(file_)
        length = madf.total_time() // 1000
    except:
        length = None
    # Get information from filename
    log.info('File: %s', file_)
    # Assuming context == "/var/nublic/data"
    context = ""
    no_context = file_.replace('/var/nublic/data/' + context, '', 1)
    log.info('No context: %s', no_context)
    parent, filename = os.path.split(no_context)
    # Initialize to empty
    track = None
    title = None
    artist = None
    album = None
    # Try to get track number
    tmatch = re.match(r"(\d+)\.? *(.*)$", filename)
    if tmatch is not None:
        track = int(tmatch.group(1))
        filename = tmatch.group(2).strip()
    # Try to parse rest of string
    ar_ab_title_match = re.match(r"\s*(.*)-\s*(.*)-\s*(.*)$", filename)
    ar_title_match = re.match(r"\s*(.*)-\s*(.*)$", filename)
    if ar_ab_title_match is not None:
        artist = ar_ab_title_match.group(1).strip()
        album = ar_ab_title_match.group(2).strip()
        title = ar_ab_title_match.group(3).strip()
    elif ar_title_match is not None:
        artist = ar_title_match.group(1).strip()
        title = ar_title_match.group(2).strip()
    else:
        title = filename
    # Get information from path
    while parent != '' and parent != '/':
        log.info('Parent is now: %s', parent)
        p_parent, p_name = os.path.split(parent)
        if album is None:
            ar_ab_match = re.match(r"\s*(.*)-\s*(.*)$", p_name)
            if ar_ab_match is not None:
                artist = ar_ab_match.group(1).strip()
                album = ar_ab_match.group(2).strip()
            else:
                album = p_name.strip()
        else:
            if artist is None:
                artist = p_name.strip()
            else:
                pass
        parent = p_parent
    # Put everythung together
    return SongInfo(title, artist, album, length, None, track, None)
