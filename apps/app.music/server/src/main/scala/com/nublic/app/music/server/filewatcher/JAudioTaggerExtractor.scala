package com.nublic.app.music.server.filewatcher

import org.jaudiotagger.audio._
import org.jaudiotagger.tag._
import java.io.File

object JAudioTaggerExtractor {
  
  def from(filename: String): SongInfo = {
    try {
      from_internal(filename)
    } catch {
      case _: Throwable => SongInfo.EMPTY_SONG_INFO
    }
  }
  
  def from_internal(filename: String): SongInfo = {
    val audio = AudioFileIO.read(new File(filename))
    val length = audio.getAudioHeader().getTrackLength()
	extract_info_from_tag(length, audio.getTag())
  }

  // Functions for extracting tags with JAudioTagger
  // ===============================================
  def extract_info_from_tag(length: Int, tag: Tag): SongInfo = {
	val title = extract_tag_field(tag, FieldKey.TITLE)
    val artist = extract_tag_field(tag, FieldKey.ARTIST)
    val album = extract_tag_field(tag, FieldKey.ALBUM)
	val year = extract_tag_field(tag, FieldKey.YEAR, _.toInt)
	val track = extract_tag_field(tag, FieldKey.TRACK, _.toInt)
	val disc_no = extract_tag_field(tag, FieldKey.DISC_NO, _.toInt)
	SongInfo(title, artist, album, Some(length), year, track, disc_no)
  }
  
  def extract_tag_field[R](tag: Tag, key: FieldKey, f: (String => R) = (a: String) => a): Option[R] = {
    try {
      Some(f(tag.getFirst(key)))
    } catch {
      case _ => None
    }
  }
}