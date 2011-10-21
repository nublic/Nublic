package com.nublic.app.music.server.filewatcher

import java.io.File
import org.jaudiotagger.audio._
import org.jaudiotagger.tag._
import org.apache.commons.io.FilenameUtils
import org.squeryl.PrimitiveTypeMode._
import com.nublic.filewatcher.scala._
import com.nublic.app.music.server.Solr
import com.nublic.app.music.server.model._

class MusicProcessor(watcher: FileWatcherActor) extends Processor("music", watcher) {
  
  def taggedMimeTypes: List[String] = List(
      // MP4
      "audio/mp4",
      // MP3
      "audio/mpeg", "audio/x-mpeg", "audio/mp3",
      "audio/x-mp3", "audio/mpeg3", "audio/x-mpeg3",
      "audio/mpg", "audio/x-mpg", "audio/x-mpegaudio",
      // OGG
      "audio/ogg", "application/ogg", "audio/x-ogg",
      "application/x-ogg",
      // FLAC
      "audio/flac"
      )
  
  def taggedExtensions: List[String] = List("mp3", "mp4", "ogg", "flac")
  
  def supportedMimeTypes: List[String] = taggedMimeTypes ::: List(
      // AAC
      "audio/aac", "audio/x-aac",
      // AC3
      "audio/ac3",
      // AIFF
      "audio/aiff", "audio/x-aiff", "sound/aiff",
      "audio/x-pn-aiff",
      // ASF
      "audio/asf",
      // MIDI
      "audio/mid", "audio/x-midi", 
      // AU
      "audio/basic", "audio/x-basic", "audio/au", 
      "audio/x-au", "audio/x-pn-au", "audio/x-ulaw",
      // PCM
      "application/x-pcm",
      // WAV
      "audio/wav", "audio/x-wav", "audio/wave",
      "audio/x-pn-wav",
      // WMA
      "audio/x-ms-wma",
      // Various
      "audio/rmf", "audio/x-rmf", "audio/vnd.qcelp",
      "audio/x-gsm", "audio/snd"
      )
  
  def process(c: FileChange) = c match {
    // case Created(filename, false)  => process_updated_file(filename)
    case Modified(filename, context, false) => process_updated_file(filename, context)
    case Moved(from, to, context, _)        => process_moved_file(from, to, context)
    case Deleted(filename, _, false)        => process_deleted_file(filename)
    case _                                  => { /* Nothing */ }
  }
  
  def process_updated_file(filename: String, context: String): Unit = {
    val extension = FilenameUtils.getExtension(filename)
    if (taggedExtensions.contains(extension) || 
        taggedMimeTypes.contains(Solr.getMimeType(filename))) {
	  val audio = AudioFileIO.read(new File(filename))
	  val song_info = extract_info(audio.getTag())
    }
  }
  
  def process_moved_file(from: String, to: String, context: String): Unit = transaction {
    Database.songByFilename(from) match {
      case None       => process_updated_file(to, context)
      case Some(song) => {
        song.file = to
        Database.songs.update(song)
      }
    }
  }
  
  def process_deleted_file(filename: String): Unit = transaction {
    Database.songByFilename(filename).map(song => Database.songs.deleteWhere(s => s.id === song.id))
  }
  
  // Functions for working with the database
  // =======================================
  def add_to_database(file: String, info: SongInfo) = {
    inTransaction {
      Database.ensureInDb(info.artist, Database.artists, Database.artistByName, new Artist(0, _))
      Database.ensureInDb(info.album, Database.albums, Database.albumByName, new Album(0, _))
      Database.songs.insert(info.toSqueryl(file))
    }
  }
  
  def replace_in_database(id: Long, info: SongInfo) = {
    inTransaction {
      Database.ensureInDb(info.artist, Database.artists, Database.artistByName, new Artist(0, _))
      Database.ensureInDb(info.album, Database.albums, Database.albumByName, new Album(0, _))
      Database.songs.lookup(id).map(song => {
        val prevArtistId = song.artistId
        val prevAlbumId = song.albumId
        info.toExistingSqueryl(song)
        Database.songs.update(song)
        Database.deleteIfNoAssocInDb(prevArtistId, Database.artists, _.artistId)
        Database.deleteIfNoAssocInDb(prevAlbumId, Database.albums, _.albumId)
      })
    }
  }
  
  def remove_from_database(filename: String) = {
    inTransaction {
      Database.songByFilename(filename).map(song => { 
        val artistId = song.artistId
        val albumId = song.albumId
        Database.songs.deleteWhere(s => s.id === song.id)
        Database.deleteIfNoAssocInDb(artistId, Database.artists, _.artistId)
        Database.deleteIfNoAssocInDb(albumId, Database.albums, _.albumId)
      })
    }
  }
  
  // Functions for extracting tags with JAudioTagger
  // ===============================================
    
  def extract_info(tag: Tag): SongInfo = {
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
  
  // Complete information about a Song
  // =================================
  case class SongInfo(title: Option[String], artist: Option[String], album: Option[String],
    year: Option[Int], track: Option[Int], disc_no: Option[Int]) {
    
    def toSqueryl(filename: String) =
      new Song(0, filename, title.getOrElse(filename),
          artist.map(a => Database.artistByName(a).get).map(_.id),
          album.map(a => Database.albumByName(a).get).map(_.id),
          year, track, disc_no)
    
    def toExistingSqueryl(song: Song) = {
      song.title = title.getOrElse(song.file)
      song.artistId = artist.map(a => Database.artistByName(a).get).map(_.id)
      song.albumId = album.map(a => Database.albumByName(a).get).map(_.id)
      song.year = year
      song.track = track
      song.disc_no = disc_no
    }
  }
}