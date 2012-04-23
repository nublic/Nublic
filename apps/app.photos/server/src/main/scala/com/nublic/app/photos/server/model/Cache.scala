package com.nublic.app.photos.server.model

import java.util.Date

class CacheItem(val action: String, val id: Long, val is_photo_item: Boolean, val is_album_item: Boolean)
case class CachePhotoAdded(val photo_id: Long) extends CacheItem("photo_added", photo_id, true, false)
case class CachePhotoDeleted(val photo_id: Long) extends CacheItem("photo_deleted", photo_id, true, false)
case class CachePhotoTitleChange(val photo_id: Long, val title: String) extends CacheItem("photo_title", photo_id, true, false)
case class CachePhotoAddedToAlbum(val photo_id: Long, val album_id: Long) extends CacheItem("photo_photo_added_album", photo_id, true, true)
case class CachePhotoRemovedFromAlbum(val photo_id: Long, val album_id: Long) extends CacheItem("photo_photo_removed_album", photo_id, true, true)
case class CacheAlbumAdded(val album_id: Long) extends CacheItem("album_added", album_id, false, false)
case class CacheAlbumRemoved(val album_id: Long) extends CacheItem("album_added", album_id, false, false)

object Cache {
  var cache: Map[Long, CacheItem] = Map[Long, CacheItem]()
  
  def TWO_HOURS: Long = 2 * 3600 * 1000; 
  def now: Long = (new Date()).getTime()
  def prune = {
    def since = now - TWO_HOURS
    cache = cache.filter(x => x._1 > since)
  }
  
  def add(item: CacheItem) = cache += now -> item
  def since(time: Long): List[CacheItem] = cache.filter(x => x._1 >= time).values.toList
  def photos_since(time: Long): List[CacheItem] = cache.filter(x => x._1 >= time && x._2.is_photo_item && !x._2.is_album_item).values.toList
  def photo_albums_since(time: Long, album_id: Long): List[CacheItem] = cache.filter(
        x => x._1 >= time && x._2.is_photo_item && x._2.is_album_item && get_album_id(x._2) == album_id
      ).values.toList
  def albums_since(time: Long): List[CacheItem] = cache.filter(
        x => x._1 >= time && !x._2.is_photo_item && x._2.is_album_item
      ).values.toList
  def get_album_id(item: CacheItem): Long = item match {
    case CachePhotoAddedToAlbum(_, i)     => i
    case CachePhotoRemovedFromAlbum(_, i) => i
    case CacheAlbumAdded(i)               => i
    case CacheAlbumRemoved(i)             => i
    case _                                => -1
  }
}