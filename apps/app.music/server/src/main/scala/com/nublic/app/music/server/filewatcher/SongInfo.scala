package com.nublic.app.music.server.filewatcher

import java.io.File
import org.jaudiotagger.audio._
import org.jaudiotagger.tag._
import com.nublic.app.music.server.model._
import net.liftweb.json._
import org.apache.commons.io.output.ByteArrayOutputStream
import java.io.BufferedInputStream
import java.util.logging.Logger

//Complete information about a Song
// =================================
case class SongInfo(title: Option[String], artist: Option[String], album: Option[String],
  length: Option[Int], year: Option[Int], track: Option[Int], disc_no: Option[Int]) {
 
  def hasImportantInfoMissing = title == None || artist == None || album == None
}

object SongInfo {
  
  def EMPTY_SONG_INFO = SongInfo(None, None, None, None, None, None, None)
  
  def from(filename: String, context: String): SongInfo = {
    Logger.global.severe("Filewatcher: JAudioTagger for " + filename)
    var tag_info = clean(JAudioTaggerExtractor.from(filename))
    
    if (tag_info.hasImportantInfoMissing) {
      Logger.global.severe("Filewatcher: Echonest for " + filename)
      EchonestExtractor.from(filename) match {
        case None => { /* */ }
        case Some(echonest_info) => tag_info = merge(tag_info, echonest_info)
      }
    }
    
    if (tag_info.hasImportantInfoMissing) {
      Logger.global.severe("Filewatcher: Filenaming for " + filename)
      val fextract = FilenameExtractor.from(filename, context)
      tag_info = merge(tag_info, fextract)
    }
    tag_info
  }
  
  def fromFilenameOnly(filename: String, context: String): SongInfo = {
    FilenameExtractor.from(filename, context)
  }
  
  def clean(s: SongInfo): SongInfo = {
    var title = s.title
    val track_regex = """(?i)track (\d+)""".r
    title.map(t =>
      t match {
        case track_regex(n) => {
          val track_no = Some(Integer.parseInt(n))
          s match {
            case SongInfo(_, ar, ab, l, y, _, d) => SongInfo(None, ar, ab, l, y, track_no, y)
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
    val length = merge(s1.length, s2.length)
    val year = merge(s1.year, s2.year)
    val track = merge(s1.track, s2.track)
    val disc_no = merge(s1.disc_no, s2.disc_no)
    SongInfo(title, artist, album, length, year, track, disc_no)
  }
  
  def merge[T](o1: Option[T], o2: Option[T]): Option[T] = if (o1 != None) o1 else o2
  
}
