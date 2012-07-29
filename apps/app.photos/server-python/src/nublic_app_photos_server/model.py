from flaskext.sqlalchemyext import SQLAlchemy

# Create database to base the model
db = SQLAlchemy()

photoAlbums = db.Table('PhotoAlbum',
    db.Column('photoId', db.BigInteger, db.ForeignKey('photo.id')),
    db.Column('albumId', db.BigInteger, db.ForeignKey('album.id'))
)

class Photo(db.Model):
    id = db.Column(db.BigInteger, primary_key=True)
    file = db.Column(db.Unicode)
    title = db.Column(db.Unicode)
    date = db.Column(db.DateTime)
    lastModified = db.Column(db.DateTime)
    
    def __init__(self, file, title, date, lastModified):
        self.file = file
        self.title = title
        self.date = date
        self.lastModified = lastModified
    
    def __repr__(self):
        return '<Photo %r "%r" at %r>' % (self.id, self.title, self.file)
    
    albums = db.relationship('Album', secondary=photoAlbums,
        backref=db.backref('photos', lazy='dynamic'))

def photo_by_filename(filename):
    Photo.query.filter_by(file=filename).first()

class Album(db.Model):
    id = db.Column(db.BigInteger, primary_key=True)
    name = db.Column(db.Unicode)
    
    def __init__(self, name):
        self.name = name
    
    def __repr__(self):
        return '<Album %r "%r">' % (self.id, self.name)
    
    photos = db.relationship('Photo', secondary=photoAlbums,
        backref=db.backref('albums', lazy='dynamic'))

def album_by_name(album_name):
    Album.query.filter_by(name=album_name).first()

def get_or_create_album(album_name):
    ab = album_by_name(album_name)
    if ab == None:
        ab = Album(album_name)
        db.session.add(ab)
        db.session.commit()
        return ab
    else:
        return ab
