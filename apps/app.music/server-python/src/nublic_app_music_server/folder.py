import os.path

ROOT_FOLDER = "/var/nublic/cache/music"
ARTISTS_FOLDER = os.path.join(ROOT_FOLDER, "artists")
ALBUMS_FOLDER = os.path.join(ROOT_FOLDER, "albums")
ORIGINAL_FILENAME = "orig"
THUMBNAIL_FILENAME = "thumb.png"
ARTIST_THUMBNAIL_SIZE = 96

def get_artist_folder(artist_id):
    return os.path.join(ARTISTS_FOLDER, str(artist_id))

def get_album_folder(artist_id):
    return os.path.join(ALBUMS_FOLDER, str(artist_id))
