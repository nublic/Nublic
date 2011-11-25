from elixir import Entity, metadata, setup_all
from elixir.fields import Field
from sqlalchemy.types import Integer, UnicodeText, String, Boolean, Unicode, Text
from elixir.relationships import ManyToOne, OneToMany, OneToOne, ManyToMany
from elixir.options import using_options

metadata.bind = 'postgresql://nublic:nublic@localhost:5432/files_and_users'
metadata.bind.echo = True

NOTIFICATION_LEVEL_LOG = 'LOG'
NOTIFICATION_LEVEL_NOTICE = 'NOTICE'
NOTIFICATION_LEVEL_DEFAULT_PERFORMED = 'DEFAULT_PERFORMED'
NOTIFICATION_LEVEL_BLOCKER = 'BLOCKER'

class User(Entity):
    using_options(tablename='users')
    username = Field(String(255), primary_key=True)
    uid = Field(Integer())
    name = Field(String(255))
    mirrors = OneToMany('Mirror')
    synced_folders = OneToMany('SyncedFolder')

    def __init__(self):
        Entity.__init__(self)

class Mirror(Entity):
    using_options(tablename='mirrors')
    id = Field(Integer, primary_key=True)
    name = Field(String(255))
    user = ManyToOne('User', colname='username')

    def __init__(self):
        Entity.__init__(self)

class SyncedFolder(Entity):
    using_options(tablename='synced')
    id = Field(Integer, primary_key=True)
    name = Field(String(255))
    user = ManyToOne('User', colname='username')

    def __init__(self):
        Entity.__init__(self)
