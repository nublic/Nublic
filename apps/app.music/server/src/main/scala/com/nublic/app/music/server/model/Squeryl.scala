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
  var artistId: Long, var albumId: Long,
  var year: Option[Int], var track: Option[Int], var disc_no: Option[Int])
  extends KeyedEntity[Long] {
  def this() = this(0, "", "", 0, 0, Some(0), Some(0), Some(0))
  
  lazy val artist: ManyToOne[Artist] = Database.songArtists.right(this)
  lazy val album: ManyToOne[Album] = Database.songAlbums.right(this)
}

object Database extends Schema {
  val artists = table[Artist]
  val albums = table[Album]
  val songs = table[Song]
  
  val songArtists = oneToManyRelation(artists, songs).
    via((artist, song) => artist.id === song.artistId)
  val songAlbums = oneToManyRelation(albums, songs).
    via((album, song) => album.id === song.albumId)
  
  def songByFilename(file: String) = maybe(songs.where(s => s.file === file))
  def artistByNameNormalizing(name: String) = maybe(artists.where(a => a.normalized === StringUtil.normalize(name)))
  def albumByNameNormalizing(name: String) = maybe(albums.where(a => a.normalized === StringUtil.normalize(name)))
  
  def maybe[R](q: Query[R]): Option[R] = {
    try {
      Some(q.single)
    } catch {
      case _ => None
    }
  }
  
  def ensureInDb[R <: { def getId(): Long }](name: String, table: Table[R], 
      searcher: String => Option[R], constructor: String => R): Option[Long] = {
    searcher(name) match {
      case Some(element) => Some(element.getId)
      case None => {
        val newElement = constructor(name)
        table.insert(newElement)
        Some(newElement.getId)
      }
    }
  }
  
  def deleteIfNoAssocInDb[R <: { def getId(): Long }](id: Long, table: Table[R], checker: Song => Long) = {
    if (Database.songs.where(s => checker(s) === id).isEmpty) {
      table.deleteWhere(a => a.getId() === id)
    }
  }
}