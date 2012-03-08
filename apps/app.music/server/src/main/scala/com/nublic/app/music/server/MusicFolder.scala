package com.nublic.app.music.server
import java.io.File

object MusicFolder {

  val ROOT_FOLDER = "/var/nublic/cache/music"
  val ARTISTS_FOLDER = ROOT_FOLDER + "/artists"
  val ALBUMS_FOLDER = ROOT_FOLDER + "/albums"
  val ORIGINAL_FILENAME = "orig"
  val THUMBNAIL_FILENAME = "thumb.png"
  val ARTIST_THUMBNAIL_SIZE = 48
  
  def getArtistFolder(id: Long): File = new File(ARTISTS_FOLDER, id.toString())
  def getAlbumFolder(id: Long): File = new File(ALBUMS_FOLDER, id.toString())
}