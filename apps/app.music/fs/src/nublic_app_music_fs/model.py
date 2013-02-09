from elixir import Entity
from elixir.fields import Field
from sqlalchemy.types import Integer, String
from elixir.relationships import ManyToOne, OneToMany, ManyToMany
from elixir.options import using_options


class Song(Entity):
    '''
    Represents a song in the database
    '''
    using_options(tablename='Song')
    id = Field(Integer, primary_key=True)
    title = Field(String())
    file = Field(String())
    disc_no = Field(Integer)
    track = Field(Integer)
    year = Field(Integer)
    album = ManyToOne('Album', colname='albumId')
    artist = ManyToOne('Artist', colname='artistId')
    collections = ManyToMany('Collection', tablename='SongCollection', local_colname='songId', remote_colname='collectionId')

    def __init__(self):
        Entity.__init__(self)


class Artist(Entity):
    '''
    Represents an artist in the database
    '''
    using_options(tablename='Artist')
    id = Field(Integer, primary_key=True)
    name = Field(String())
    normalized = Field(String())
    songs = OneToMany('Song')

    def __init__(self):
        Entity.__init__(self)


class Album(Entity):
    '''
    Represents an album in the database
    '''
    using_options(tablename='Album')
    id = Field(Integer, primary_key=True)
    name = Field(String())
    normalized = Field(String())
    songs = OneToMany('Song')

    def __init__(self):
        Entity.__init__(self)


class Collection(Entity):
    '''
    Represents a collection in the database
    '''
    using_options(tablename='Collection')
    id = Field(Integer, primary_key=True)
    name = Field(String())
    songs = ManyToMany('Song', tablename='SongCollection',
                       local_colname='collectionId', remote_colname='songId')

    def __init__(self):
        Entity.__init__(self)
