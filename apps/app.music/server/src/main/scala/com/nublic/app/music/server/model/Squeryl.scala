package com.nublic.app.music.server.model

import org.squeryl.Schema
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.KeyedEntity
import org.squeryl.dsl.ManyToOne
import org.squeryl.dsl.OneToMany
import org.squeryl.Query
import org.squeryl.Table
import java.text.Normalizer
import java.util.regex.Pattern
import org.squeryl.dsl.CompositeKey2
import org.squeryl.dsl.CompositeKey3

object StringUtil {
  def unaccent(s: String): String = {
    val temp = Normalizer.normalize(s, Normalizer.Form.NFD)
    val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
    pattern.matcher(temp).replaceAll("")
  }
  
  def normalize(s: String): String = unaccent(s).toLowerCase
}

class Artist(val id: Long, var name: String, var normalized: String) extends KeyedEntity[Long] {
  def this() = this(0, "", "")
  def this(id: Long, name: String) = this(id, name, StringUtil.normalize(name))
  def this(name: String) = this(0, name)
  def getId() = id
  lazy val songs: OneToMany[Song] = Database.songArtists.left(this)
}

class Album(val id: Long, var name: String, var normalized: String) extends KeyedEntity[Long] {
  def this() = this(0, "", "")
  def this(id: Long, name: String) = this(id, name, StringUtil.normalize(name))
  def this(name: String) = this(0, name)
  def getId() = id
  lazy val songs: OneToMany[Song] = Database.songAlbums.left(this)
}

class Song(val id: Long, var file: String, var title: String,
  var artistId: Long, var albumId: Long, var length: Long,
  var year: Option[Int], var track: Option[Int], var disc_no: Option[Int])
  extends KeyedEntity[Long] {
  def this() = this(0, "", "", 0, 0, 0, Some(0), Some(0), Some(0))
  
  lazy val artist: ManyToOne[Artist] = Database.songArtists.right(this)
  lazy val album: ManyToOne[Album] = Database.songAlbums.right(this)
  lazy val tags = Database.songCollections.left(this)
}

class Collection(val id: Long, var name: String) extends KeyedEntity[Long] {
  def this() = this(0, "")
  def this(name: String) = this(0, name)
  
  lazy val songs = Database.songCollections.right(this)
}

class SongCollection(val songId: Long, val collectionId: Long) extends KeyedEntity[CompositeKey2[Long, Long]] {
  def id = compositeKey(songId, collectionId)
}

class Playlist(val id: Long, var name: String, var user: String) extends KeyedEntity[Long] {
  def this() = this(0, "", "")
  def this(name: String, user: String) = this(0, name, user)
  
  lazy val songs = Database.songPlaylists.right(this)
}

class SongPlaylist(val songId: Long, var playlistId: Long, val position: Int) extends KeyedEntity[CompositeKey2[Long, Int]] {
  def id = compositeKey(playlistId, position)
}

object Database extends Schema {
  val artists = table[Artist]
  val albums = table[Album]
  val songs = table[Song]
  val collections = table[Collection]
  val playlists = table[Playlist]
  
  on(artists)(f => declare(
    f.name       is (dbType("varchar(32672)")),
    f.normalized is (dbType("varchar(32672)"))
  ))
  on(albums)(f => declare(
    f.name       is (dbType("varchar(32672)")),
    f.normalized is (dbType("varchar(32672)"))
  ))
  on(songs)(f => declare(
    f.file       is (dbType("varchar(32672)")),
    f.title      is (dbType("varchar(32672)"))
  ))
  on(collections)(f => declare(
    f.name       is (dbType("varchar(32672)"))
  ))
  on(playlists)(f => declare(
    f.name       is (dbType("varchar(32672)"))
  ))
  
  val songArtists = oneToManyRelation(artists, songs).
    via((artist, song) => artist.id === song.artistId)
  val songAlbums = oneToManyRelation(albums, songs).
    via((album, song) => album.id === song.albumId)
  val songCollections = manyToManyRelation(songs, collections).
    via[SongCollection]((s, t, st) => (s.id === st.songId, t.id === st.collectionId))
  val songPlaylists = manyToManyRelation(songs, playlists).
    via[SongPlaylist]((s, p, sp) => (s.id === sp.songId, p.id === sp.playlistId))
  
  def songByFilename(file: String) = maybe(songs.where(s => s.file === file))
  def artistByNameNormalizing(name: String) = maybe(artists.where(a => a.normalized === StringUtil.normalize(name)))
  def albumByNameNormalizing(name: String) = maybe(albums.where(a => a.normalized === StringUtil.normalize(name)))
  def collectionByName(name: String) = maybe(collections.where(t => t.name === name))
  def playlistByName(name: String) = maybe(playlists.where(t => t.name === name))
  def playlistsByUser(user: String) = playlists.where(p => p.user === user).toList
  def isPlaylistOfUser(pid: Long, user: String) = playlists.lookup(pid) match {
    case None     => false
    case Some(pl) => pl.user == user
  }
  
  def maybe[R](q: Query[R]): Option[R] = q.headOption
  /*{
    try {
      Some(q.single)
    } catch {
      case _ => None
    }
  }*/
  
  def ensureInDb[R <: { def getId(): Long }](name: String, table: Table[R], 
      searcher: String => Option[R], constructor: String => R): R = {
    searcher(name) match {
      case Some(element) => element
      case None => {
        val newElement = constructor(name)
        table.insert(newElement)
        newElement
      }
    }
  }
  
  def deleteIfNoAssocInDb[R <: { def getId(): Long }](id: Long, table: Table[R], checker: Song => Long) = {
    if (Database.songs.where(s => checker(s) === id).isEmpty) {
      table.deleteWhere(a => a.getId() === id)
    }
  }
}
