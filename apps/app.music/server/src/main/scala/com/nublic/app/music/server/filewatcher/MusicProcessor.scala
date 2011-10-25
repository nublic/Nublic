package com.nublic.app.music.server.filewatcher

import java.io.File
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
      val song_info = SongInfo.from(filename, context)
      Database.songByFilename(filename) match {
        case Some(song) => replace_in_database(song.id, song_info)
        case None       => add_to_database(filename, song_info)
      }
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
  //
  // Note: Unknown discs or artists are shown with "" in their names
  // ===============================================================
  def ensure_or_create_album(file: String, artistName: Option[String], albumName: Option[String]): Album = {
    val directory = new File(file).getParentFile.getPath
    
    inTransaction {
      // CASE 1. We have a artist name and an album name
      if (artistName != None && albumName != None) {
        // Try to find an album with the same name by the same artist
        find_album_with_artist(artistName.get, albumName.get) match {
          case Some(album) => album // Found an album with same artist and album
          case None        => find_album_by_directory(directory, albumName.get)
        }
      // CASE 2. We have only album name
      } else if (albumName != None /* I have the album name */) { 
        find_album_by_directory(directory, albumName.get)
      // CASE 3. We only have artist name or we have nothing
      } else {
        find_album_with_artist(artistName.getOrElse(""), albumName.getOrElse("")) match {
          case Some(album) => album // Found an album with same artist and album
          case None        => {
            val newAlbum = new Album(albumName.getOrElse(""))
            Database.albums.insert(newAlbum)
            newAlbum
          }
        }
      }
    }
  }
  
  private def find_album_with_artist(artistName: String, albumName: String): Option[Album] = {
    val normalizedArtist = StringUtil.normalize(artistName)
    val normalizedAlbum = StringUtil.normalize(albumName)
    val artistId: Long = Database.artistByNameNormalizing(artistName).map(_.id).getOrElse(-1)
    
    val sameArtistAndAlbum = from(Database.songs, Database.artists, Database.albums)(
      (song, artist, album) =>
        where(song.artistId === artistId and song.albumId === album.id /* join */
              and album.normalized === normalizedAlbum)
        select(album)
      )
    Database.maybe(sameArtistAndAlbum)
  }
  
  private def find_album_by_directory(directory: String, albumName: String): Album = {
    val normalizedAlbum = StringUtil.normalize(albumName)
    // Try to find songs in the same folder with the same album name
    val sameAlbumAndPath = from(Database.songs, Database.albums)(
      (song, album) =>
        where(song.albumId === album.id and album.normalized === normalizedAlbum
              and (song.file like (directory + "/%")))
        select(album)
      )
    Database.maybe(sameAlbumAndPath) match {
      case Some(album) => album
      case None        => {
        // If nothing is found
        val newAlbum = new Album(albumName)
        Database.albums.insert(newAlbum)
        newAlbum
      }
    }
  }

  def add_to_database(file: String, info: SongInfo) = {
    inTransaction {
      Database.ensureInDb(info.artist.getOrElse(""), Database.artists, Database.artistByNameNormalizing, new Artist(_))
      ensure_or_create_album(file, info.artist, info.album)
      Database.songs.insert(info.toSqueryl(file))
    }
  }
  
  def replace_in_database(id: Long, info: SongInfo) = {
    inTransaction {
      Database.songs.lookup(id).map(song => {
        val prevArtistId = song.artistId
        val prevAlbumId = song.albumId
        Database.ensureInDb(info.artist.getOrElse(""), Database.artists, Database.artistByNameNormalizing, new Artist(_))
        ensure_or_create_album(song.file, info.artist, info.album)
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
}