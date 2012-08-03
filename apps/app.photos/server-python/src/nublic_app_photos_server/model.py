from nublic_server.sqlalchemyext import SQLAlchemy

# Create database to base the model
db = SQLAlchemy()

class Photo(db.Model):
    __tablename__ = 'Photo'
    
    id = db.Column(db.BigInteger, primary_key=True)
    file = db.Column(db.Unicode)
    title = db.Column(db.Unicode)
    date = db.Column(db.BigInteger)
    lastModified = db.Column(db.BigInteger)
    
    def __init__(self, file_, title, date, lastModified):
        self.file = file_
        self.title = title
        self.date = date
        self.lastModified = lastModified
    
    def __repr__(self):
        return '<Photo %r "%r" at %r>' % (self.id, self.title, self.file)

def photo_by_filename(filename):
    Photo.query.filter_by(file=filename).first()

def photo_as_json(photo):
    return { 'id': photo.id,
             'title': photo.title,
             'date': photo.date
           }

def photos_and_row_count_as_json(row_count, photos):
    return { 'row_count': row_count, 'photos': map(photo_as_json, photos) }

class Album(db.Model):
    __tablename__ = 'Album'
    
    id = db.Column(db.BigInteger, primary_key=True)
    name = db.Column(db.Unicode)
    
    def __init__(self, name):
        self.name = name
    
    def __repr__(self):
        return '<Album %r "%r">' % (self.id, self.name)

def album_by_name(album_name):
    Album.query.filter_by(name=album_name).first()

def album_as_json(album):
    return { 'id': album.id,
             'name': album.name
           }

def get_or_create_album(album_name):
    ab = album_by_name(album_name)
    if ab == None:
        ab = Album(album_name)
        db.session.add(ab)
        db.session.commit()
        return ab
    else:
        return ab

class PhotoAlbum(db.Model):
    __tablename__ = 'PhotoAlbum'
    
    photoId = db.Column('photoId', db.BigInteger, db.ForeignKey('Photo.id'), primary_key=True)
    albumId = db.Column('albumId', db.BigInteger, db.ForeignKey('Album.id'), primary_key=True)
    
    def __init__(self, albumId, photoId):
        self.photoId = photoId
        self.albumId = albumId
    
    photo = db.relationship(Photo, backref='albums')
    album = db.relationship(Album, backref='photos')
