package com.nublic.app.music.server

import java.io.File
import org.jaudiotagger.audio._
import org.jaudiotagger.tag._
import com.nublic.app.music.server.model._
import com.echonest.api.{v4 => E}
import scala.actors.Actor._
import net.liftweb.json._
import org.apache.commons.io.output.ByteArrayOutputStream
import java.io.BufferedInputStream
import com.echonest.api.v4.util.Commander

object SongInfo {
  
  val ECHONEST_API_KEY = "UR4VKX7JXDXAULIWB"
  
  def from(filename: String): SongInfo = {
    val audio = AudioFileIO.read(new File(filename))
	var tag_info = extract_info_from_tag(audio.getTag())
	if (tag_info.hasImportantInfoMissing) {
	  extract_from_echonest(filename) match {
	    case None => { /* */ }
	    case Some(echonest_info) => tag_info = merge(tag_info, echonest_info)
	  }
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
  
  // Extracting info from Echonest
  // =============================
  def extract_from_echonest(filename: String): Option[SongInfo] = {
    val file = new File(filename)
    // Get fingerprint
    val cmd = new ProcessBuilder("echoprint-codegen", filename, "10", "30")
    val process = cmd.start()
    val byteStream = new ByteArrayOutputStream()
    actor {
      // Read file
      val buffer = new Array[Byte](1024)
      val buffered_in_stream = new BufferedInputStream(process.getInputStream())
      var bytes_read = buffered_in_stream.read(buffer, 0, buffer.length)
      while(bytes_read != -1) {
        byteStream.write(buffer, 0, bytes_read)
        bytes_read = buffered_in_stream.read(buffer, 0, buffer.length)
      }
      // Get bytes
      byteStream.flush()
      byteStream.close()
    }.start
    process.waitFor()
    // Send query
    val query = byteStream.toString()
    val commander = new Commander("Nublic")
    val params = new E.Params()
    params.set("api_key", ECHONEST_API_KEY)
    params.set("query", query)
    val result = commander.sendCommand("song/identify", params, true)
    val response = result.get("response").asInstanceOf[java.util.Map[String, _]]
    val songList = response.get("songs").asInstanceOf[java.util.List[_]]
    if (songList.size() > 0) {
      val songMap = songList.get(0).asInstanceOf[java.util.Map[String, _]]
      val title = songMap.get("title").asInstanceOf[String]
      val artist = songMap.get("artist_name").asInstanceOf[String]
      Some(SongInfo(Some(title), Some(artist), None, None, None, None))
    } else {
      None
    }
  }
  
  // Functions for extracting tags with JAudioTagger
  // ===============================================
  def extract_info_from_tag(tag: Tag): SongInfo = {
	val title = extract_tag_field(tag, FieldKey.TITLE)
    val artist = extract_tag_field(tag, FieldKey.ARTIST)
    val album = extract_tag_field(tag, FieldKey.ALBUM)
	val year = extract_tag_field(tag, FieldKey.YEAR, _.toInt)
	val track = extract_tag_field(tag, FieldKey.TRACK, _.toInt)
	val disc_no = extract_tag_field(tag, FieldKey.DISC_NO, _.toInt)
	SongInfo(title, artist, album, year, track, disc_no)
  }
  
  def extract_tag_field[R](tag: Tag, key: FieldKey, f: (String => R) = (a: String) => a): Option[R] = {
    try {
      Some(f(tag.getFirst(key)))
    } catch {
      case _ => None
    }
  }
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
