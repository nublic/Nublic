from nublic_server.sqlalchemyext import SQLAlchemy

# Create database to base the model
db = SQLAlchemy()

photoAlbums = db.Table('PhotoAlbum',
    db.Column('photoId', db.BigInteger, db.ForeignKey('Photo.id')),
    db.Column('albumId', db.BigInteger, db.ForeignKey('Album.id'))
)

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
    
    albums = db.relationship('Album', secondary=photoAlbums)

def photo_by_filename(filename):
    Photo.query.filter_by(file=filename).first()

def photo_as_json(photo):
    return { 'id': photo.id,
             'title': photo.title,
             'date': photo.date
           }

class Album(db.Model):
    __tablename__ = 'Album'
    
    id = db.Column(db.BigInteger, primary_key=True)
    name = db.Column(db.Unicode)
    
    def __init__(self, name):
        self.name = name
    
    def __repr__(self):
        return '<Album %r "%r">' % (self.id, self.name)
    
    photos = db.relationship('Photo', secondary=photoAlbums)

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
