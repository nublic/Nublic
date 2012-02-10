package com.nublic.app.music.server.filewatcher

import java.io.File

// Algorithm taken from Banshee

object FilenameExtractor {
  
  def from(filename: String, context: String): SongInfo = {
    val no_context = filename.replaceFirst(context + "/", "")
    var path = new File(no_context)
    var fname = path.getName()
    // Initialize to empty
    var track: Option[Int] = None
    var title: Option[String] = None
    var artist: Option[String] = None
    var album: Option[String] = None
    // Try to get track number
    val track_regex = """(\d+)\.? *(.*)$""".r
    fname match {
      case track_regex(n, rest) => {
        track = Some(Integer.parseInt(n.trim()))
        fname = rest.trim()
        path = new File(path.getParentFile(), fname)
      }
      case _ => { /* Do nothing */ }
    }
    // Parse rest of the string
    val artist_album_title = """\s*(.*)-\s*(.*)-\s*(.*)$"""".r
    val artist_title = """\s*(.*)-\s*(.*)$""".r
    fname match {
      case artist_album_title(art, alb, tit) => {
        artist = Some(art)
        album = Some(alb)
        title = Some(tit)
      }
      case artist_title(art, tit) => {
        artist = Some(art)
        title = Some(tit)
      }
      case _: String => {
        title = Some(fname)
      }
    }
    // Get information from path
    while (path.getParentFile() != null) {
      path = path.getParentFile()
      val name = path.getName()
      album match {
        case None    => {
          val artist_album = """\s*(.*)-\s*(.*)$""".r
          name match {
            case artist_album(art, alb) => {
              artist = Some(art)
              album = Some(alb)
            }
            case _: String => album = Some(name)
          }
        }
        case Some(_) => artist match {
          case None    => artist = Some(name)
          case Some(_) => { /* */ }
        }
      }
    }
    // Trim elements
    SongInfo(title.map(_.trim()), artist.map(_.trim()), album.map(_.trim()),
        None, track, None)
  }
}