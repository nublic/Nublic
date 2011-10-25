package com.nublic.app.music.server.filewatcher

import java.io.File
import org.jaudiotagger.audio._
import org.jaudiotagger.tag._
import com.nublic.app.music.server.model._
import net.liftweb.json._
import org.apache.commons.io.output.ByteArrayOutputStream
import java.io.BufferedInputStream


object SongInfo {
  
  
  
  def from(filename: String, context: String): SongInfo = {
    var tag_info = JAudioTaggerExtractor.from(filename)
	if (tag_info.hasImportantInfoMissing) {
	  EchonestExtractor.from(filename) match {
	    case None => { /* */ }
	    case Some(echonest_info) => tag_info = merge(tag_info, echonest_info)
	  }
	}
    if (tag_info.hasImportantInfoMissing) {
      val fname = filename.replaceFirst(context, "")
    }
    tag_info
  }
  
  def merge(s1: SongInfo, s2: SongInfo): SongInfo = {
    val title = merge(s1.title, s2.title)
    val artist = merge(s1.artist, s2.artist)
    val album = merge(s1.album, s2.album)
    val year = merge(s1.year, s2.year)
    val track = merge(s1.track, s2.track)
    val disc_no = merge(s1.disc_no, s2.disc_no)
    SongInfo(title, artist, album, year, track, disc_no)
  }
  
  def merge[T](o1: Option[T], o2: Option[T]): Option[T] = if (o1 != None) o1 else o2
  
}

//Complete information about a Song
// =================================
case class SongInfo(title: Option[String], artist: Option[String], album: Option[String],
  year: Option[Int], track: Option[Int], disc_no: Option[Int]) {
 
  def hasImportantInfoMissing = title == None || artist == None || album == None
  
  def toSqueryl(filename: String) = {
    new Song(0, filename, title.getOrElse(filename),
        artist.map(a => Database.artistByNameNormalizing(a).get).map(_.id).getOrElse(-1),
        album.map(a => Database.albumByNameNormalizing(a).get).map(_.id).getOrElse(-1),
        year, track, disc_no)
  }
 
  def toExistingSqueryl(song: Song) = {
    song.title = title.getOrElse(song.file)
    song.artistId = artist.map(a => Database.artistByNameNormalizing(a).get).map(_.id).getOrElse(-1)
    song.albumId = album.map(a => Database.albumByNameNormalizing(a).get).map(_.id).getOrElse(-1)
    song.year = year
    song.track = track
    song.disc_no = disc_no
  }
}
