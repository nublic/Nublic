package com.nublic.app.music.server.filewatcher

import java.io.File
import org.apache.commons.io.FilenameUtils
import org.squeryl.PrimitiveTypeMode._
import com.nublic.filewatcher.scala._
import com.nublic.app.music.server.Solr
import com.nublic.app.music.server.model._
import java.io.FileWriter
import java.io.PrintWriter
import java.util.logging.Logger

class MusicProcessor(watcher: FileWatcherActor) extends Processor("music", watcher, true) {
  
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
      // ASF
      "audio/asf",
      // WMA
      "audio/x-ms-wma",
      // Real
      "audio/rmf", "audio/x-rmf",
      // FLAC
      "audio/flac"
      )
  
  def taggedExtensions: List[String] = List("mp3", "mp4", "ogg", "flac", "wma", "rm")
  
  def supportedMimeTypes: List[String] = taggedMimeTypes ::: List(
      // AAC
      "audio/aac", "audio/x-aac",
      // AC3
      "audio/ac3",
      // AIFF
      "audio/aiff", "audio/x-aiff", "sound/aiff",
      "audio/x-pn-aiff",
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
      // Various
      "audio/vnd.qcelp", "audio/x-gsm", "audio/snd"
      )
  
  def supportedExtensions: List[String] = taggedExtensions ::: 
    List("wav", "aac", "ac3", "aiff", "mid", "midi", "au", "pcm")
  
  def process(c: FileChange) = {
    Logger.global.severe("Filewatcher: Processing " + c.toString())
    c match {
      // case Created(filename, false)  => process_updated_file(filename)
      case Modified(filename, context, false) => process_updated_file(filename, context)
      case Moved(from, to, context, _)        => process_moved_file(from, to, context)
      case Deleted(filename, _, false)        => process_deleted_file(filename)
      case _                                  => { /* Nothing */ }
    }
  }
  
  def process_updated_file(filename: String, context: String): Unit = {
    val extension = FilenameUtils.getExtension(filename)
    
    Logger.global.severe("Filewatcher: Getting info from " + filename)
    val song_info = 
      if (taggedExtensions.contains(extension) || taggedMimeTypes.contains(Solr.getMimeType(filename))) {
        Some(SongInfo.from(filename, context))
      } else if (supportedExtensions.contains(extension) || supportedMimeTypes.contains(Solr.getMimeType(filename))) {
        Some(FilenameExtractor.from(filename, context))
      } else {
        None
      }
    
    Logger.global.severe("Filewatcher: Adding to database " + filename)
    if (song_info.isDefined) {
      inTransaction {
        Database.songByFilename(filename) match {
          case Some(song) => {
            Logger.global.severe("Filewatcher: Replacing in database " + filename)
            replace_in_database(filename, song.id, song_info.get) 
          }
          case None =>  {
            Logger.global.severe("Filewatcher: Really adding to database " + filename)
            add_to_database(filename, song_info.get)
          }
        }
      }
    }
    
    // Logger.global.severe("Filewatcher: Added to database " + filename)
  }
  
  def process_moved_file(from: String, to: String, context: String): Unit = inTransaction {
    Database.songByFilename(from) match {
      case None       => process_updated_file(to, context)
      case Some(song) => {
        song.file = to
        Database.songs.update(song)
      }
    }
  }
  
  def process_deleted_file(filename: String): Unit = inTransaction {
    Database.songByFilename(filename).map(song => {
      Database.songCollections.deleteWhere(sc => sc.songId === song.id)
      // Remove from playlists
      var sps: List[SongPlaylist] = Database.songPlaylists.where(sp => sp.songId == song.id).toList
      while (!sps.isEmpty) {
        val sp = sps.head
        val pl_pos = sp.position
        Database.songPlaylists.deleteWhere(x => x.songId === sp.songId and x.playlistId === sp.playlistId and x.position === sp.position)
        update(Database.songPlaylists)(x =>
          where(x.playlistId === sp.playlistId and sp.position > pl_pos.~)
          set(x.position := x.position - 1)
        )
        // Try again
        sps = Database.songPlaylists.where(sp => sp.songId == song.id).toList
      }
      Database.songs.deleteWhere(s => s.id === song.id)
    })
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
  
  def update_solr(file: String, info: SongInfo) = {
    Solr.getInputDocument(file) match {
      case None      => { /* Cannot update it */ }
      case Some(doc) => {
        doc.setField("title", info.title.getOrElse(""))
        doc.setField("artist", info.artist.getOrElse(""))
        doc.setField("album", info.album.getOrElse(""))
        doc.setField("length", info.length.getOrElse(0))
        info.year match {
          case None    => doc.removeField("year")
          case Some(y) => doc.setField("year", y)
        }
        info.track match {
          case None    => doc.removeField("trackNumber")
          case Some(t) => doc.setField("trackNumber", t)
        }
        Solr.update(doc)
      }
    }
  }

  def add_to_database(file: String, info: SongInfo) = {
    inTransaction {
      Logger.global.severe("Filewatcher: Adding artist " + info.artist.getOrElse(""))
      val artist = Database.ensureInDb(info.artist.getOrElse(""), Database.artists, Database.artistByNameNormalizing, new Artist(_))
      Logger.global.severe("Filewatcher: Adding artist image")
      Images.ensureArtist(artist)
      Logger.global.severe("Filewatcher: Adding album " + info.album.getOrElse(""))
      val album = ensure_or_create_album(file, info.artist, info.album)
      Logger.global.severe("Filewatcher: Adding album image")
      Images.ensureAlbum(new File(file), album, Some(artist))
      Logger.global.severe("Filewatcher: Creating song")
      val song = new Song()
      song.file = file
      song.title = info.title.getOrElse("")
      song.artistId = artist.id
      song.albumId = album.id
      song.length = if (info.length.isDefined) { info.length.get } else { 0 } 
      song.year = info.year
      song.track = info.track
      song.disc_no = info.disc_no
      // Logger.global.severe("Filewatcher: Inserting song")
      try {
        Database.songs.insert(song)
      } catch {
        case e: Throwable => Logger.global.severe("Music Filewatcher exception: " + e.getMessage())
      }
    }
    Logger.global.severe("Filewatcher: Adding to solr")
    update_solr(file, info)
  }
  
  def replace_in_database(file: String, id: Long, info: SongInfo) = {
    inTransaction {
      Database.songs.lookup(id).map(song => {
        val prevArtistId = song.artistId
        val prevAlbumId = song.albumId
        val newArtist = Database.ensureInDb(info.artist.getOrElse(""), Database.artists, Database.artistByNameNormalizing, new Artist(_))
        Images.ensureArtist(newArtist)
        val newAlbum = ensure_or_create_album(song.file, info.artist, info.album)
        Images.ensureAlbum(new File(file), newAlbum, Some(newArtist))
        song.title = info.title.getOrElse("")
        song.artistId = newArtist.id
        song.albumId = newAlbum.id
        song.length = if (info.length.isDefined) { info.length.get } else { 0 } 
        song.year = info.year
        song.track = info.track
        song.disc_no = info.disc_no
        try {
          Database.songs.update(song)
        } catch {
          case e: Throwable => Logger.global.severe("Music Filewatcher exception: " + e.getMessage())
        }
        Database.deleteIfNoAssocInDb(prevArtistId, Database.artists, _.artistId)
        Database.deleteIfNoAssocInDb(prevAlbumId, Database.albums, _.albumId)
      })
    }
    update_solr(file, info)
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
