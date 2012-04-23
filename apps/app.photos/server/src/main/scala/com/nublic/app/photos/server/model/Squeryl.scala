package com.nublic.app.photos.server.model

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

class Photo(val id: Long, var file: String, var title: String, var date: Long, var lastModified: Long)
  extends KeyedEntity[Long] {
  def this() = this(0, "", "", 0, 0)
  
  lazy val albums = Database.photoAlbums.left(this)
}

class Album(val id: Long, var name: String) extends KeyedEntity[Long] {
  def this() = this(0, "")
  def this(name: String) = this(0, name)
  
  lazy val photos = Database.photoAlbums.right(this)
}

class PhotoAlbum(val photoId: Long, val albumId: Long) extends KeyedEntity[CompositeKey2[Long, Long]] {
  def id = compositeKey(photoId, albumId)
}

object Database extends Schema {
  val photos = table[Photo]
  val albums = table[Album]
  
  on(photos)(f => declare(
    f.file       is (dbType("varchar(32672)")),
    f.title      is (dbType("varchar(32672)"))
  ))
  on(albums)(f => declare(
    f.name       is (dbType("varchar(32672)"))
  ))
  
  val photoAlbums = manyToManyRelation(photos, albums).
    via[PhotoAlbum]((p, a, pa) => (p.id === pa.photoId, a.id === pa.albumId))
  
  def photoByFilename(file: String) = maybe(photos.where(p => p.file === file))
  def albumByName(name: String) = maybe(albums.where(a => a.name === name))
  def maybe[R](q: Query[R]): Option[R] = q.headOption
  
  def getOrCreateAlbum(album_name: String): Album = {
    albumByName(album_name) match {
      case Some(album) => album
      case None        => {
        val album = new Album()
        album.name = album_name
        Database.albums.insert(album)
        album
      }
    }
  }
}
