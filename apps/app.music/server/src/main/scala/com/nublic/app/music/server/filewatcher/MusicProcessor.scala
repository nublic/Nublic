package com.nublic.app.music.server.filewatcher

import com.nublic.filewatcher.scala._
import java.io.File
import org.jaudiotagger.audio._
import org.jaudiotagger.tag._
import com.nublic.app.music.server.Solr
import org.apache.commons.io.FilenameUtils

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
    case Deleted(filename, _, false)        => process_deleted_file(filename)
    case _                                  => { /* Nothing */ }
  }
  
  def process_updated_file(filename: String, context: String): Unit = {
    val extension = FilenameUtils.getExtension(filename)
    if (taggedExtensions.contains(extension) || 
        taggedMimeTypes.contains(Solr.getMimeType(filename))) {
	  val audio = AudioFileIO.read(new File(filename))
	  val tag = audio.getTag()
	  val artist = extract_string_tag(tag, FieldKey.ARTIST)
      val album = extract_string_tag(tag, FieldKey.ALBUM)
	  val title = extract_string_tag(tag, FieldKey.TITLE)
	  val year = extract_integer_tag(tag, FieldKey.YEAR)
	  val track = extract_integer_tag(tag, FieldKey.TRACK)
	  val disco_no = extract_integer_tag(tag, FieldKey.DISC_NO)
	  Console.printf("%s by %s on %s", title, artist, album)
    }
  }
  
  def extract_string_tag(tag: Tag, key: FieldKey): Option[String] = {
    try {
      Some(tag.getFirst(key))
    } catch {
      case _ => None
    }
  }
  
  def extract_integer_tag(tag: Tag, key: FieldKey): Option[Int] = {
    try {
      Some(tag.getFirst(key).toInt)
    } catch {
      case _ => None
    }
  }
  
  def process_deleted_file(filename: String): Unit = {
    
  }
}