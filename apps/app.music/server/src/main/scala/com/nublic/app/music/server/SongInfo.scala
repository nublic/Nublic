package com.nublic.app.music.server

import java.io.File
import org.jaudiotagger.audio._
import org.jaudiotagger.tag._
import com.nublic.app.music.server.model._
import com.echonest.api.{v4 => E}

object SongInfo {
  
  val ECHONEST_API_KEY = "UR4VKX7JXDXAULIWB"
  
  def from(filename: String): SongInfo = {
    Console.println(extract_from_echonest(filename))
    val audio = AudioFileIO.read(new File(filename))
	val tag_info = extract_info_from_tag(audio.getTag())
	tag_info
  }
  
  def extract_from_echonest(filename: String): String = {
    val file = new File(filename)
    val api = new E.EchoNestAPI(ECHONEST_API_KEY)
    val track = api.getKnownTrack(file)
    val song = new E.Song(api, track.getSongID())
    return song.getTitle()
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
