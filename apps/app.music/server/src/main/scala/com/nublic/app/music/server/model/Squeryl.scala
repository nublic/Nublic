package com.nublic.app.music.server.model

import org.squeryl.Schema
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.KeyedEntity
import org.squeryl.dsl.ManyToOne
import org.squeryl.dsl.OneToMany
import org.squeryl.Query
import org.squeryl.Table

class Artist(val id: Long, var name: String) extends KeyedEntity[Long] {
  def this() = this(0, "")
  def getId() = id
  lazy val songs: OneToMany[Song] = Database.songArtists.left(this)
}

class Album(val id: Long, var name: String) extends KeyedEntity[Long] {
  def this() = this(0, "")
  def getId() = id
  lazy val songs: OneToMany[Song] = Database.songAlbums.left(this)
}

class Song(val id: Long, var file: String, var title: String,
  var artistId: Option[Long], var albumId: Option[Long],
  var year: Option[Int], var track: Option[Int], var disc_no: Option[Int])
  extends KeyedEntity[Long] {
  def this() = this(0, "", "", Some(0), Some(0), Some(0), Some(0), Some(0))
  
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
  def artistByName(name: String) = maybe(artists.where(a => a.name === name))
  def albumByName(name: String) = maybe(albums.where(a => a.name === name))
  
  def maybe[R](q: Query[R]): Option[R] = {
    try {
      Some(q.single)
    } catch {
      case _ => None
    }
  }
  
  def ensureInDb[R <: { def getId(): Long }](name: Option[String], table: Table[R], 
      searcher: String => Option[R], constructor: String => R): Option[Long] = {
    name match {
      case None => None
      case Some(innerName) => {
        searcher(innerName) match {
          case Some(element) => Some(element.getId)
          case None => {
            val newElement = constructor(innerName)
            table.insert(newElement)
            Some(newElement.getId)
          }
        }
      }
    }
  }
  
  def deleteIfNoAssocInDb[R <: { def getId(): Long }](idToCheck: Option[Long], table: Table[R], checker: Song => Option[Long]) = {
    idToCheck.map(id => {
      if (Database.songs.where(s => checker(s) === id).isEmpty) {
        table.deleteWhere(a => a.getId() === id)
      }
    })
  }
}