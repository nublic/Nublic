package com.nublic.app.music.server.filewatcher

import java.io.File
import org.jaudiotagger.audio._
import org.jaudiotagger.tag._
import com.nublic.app.music.server.model._
import net.liftweb.json._
import org.apache.commons.io.output.ByteArrayOutputStream
import java.io.BufferedInputStream

//Complete information about a Song
// =================================
case class SongInfo(title: Option[String], artist: Option[String], album: Option[String],
  year: Option[Int], track: Option[Int], disc_no: Option[Int]) {
 
  def hasImportantInfoMissing = title == None || artist == None || album == None
}

object SongInfo {
  
  def from(filename: String, context: String): SongInfo = {
    var tag_info = clean(JAudioTaggerExtractor.from(filename))
	if (tag_info.hasImportantInfoMissing) {
	  EchonestExtractor.from(filename) match {
	    case None => { /* */ }
	    case Some(echonest_info) => tag_info = merge(tag_info, echonest_info)
	  }
	}
    if (tag_info.hasImportantInfoMissing) {
      val fextract = FilenameExtractor.from(filename, context)
      tag_info = merge(tag_info, fextract)
    }
    tag_info
  }
  
  def clean(s: SongInfo): SongInfo = {
    var title = s.title
    val track_regex = """(?i)track (\d+)""".r
    title.map(t =>
      t match {
        case track_regex(n) => {
          val track_no = Some(Integer.parseInt(n))
          s match {
            case SongInfo(_, ar, ab, y, _, d) => SongInfo(None, ar, ab, y, track_no, y)
          }
        }
        case _: String => s
      }
    ).getOrElse(s)
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