package com.nublic.app.browser.server.filewatcher.workers

import com.nublic.app.browser.server.filewatcher.DocumentWorker
import scala.collection.immutable.List
import scala.actors.Actor._
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import org.apache.commons.io.FilenameUtils
import com.nublic.app.browser.server.filewatcher.FileFolder
import org.im4java.core.ConvertCmd
import org.im4java.core.IMOperation

object AudioWorker extends DocumentWorker {

  def supportedMimeTypes: List[String] = List(
      /* Obtained looking at:
       * - List of files supported by ffmpeg: `ffmpeg -formats`
       * - Information about file extensions: http://filext.com/
       */
      
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
      // MP4
      "audio/mp4",
      // MP3
      "audio/mpeg", "audio/x-mpeg", "audio/mp3",
      "audio/x-mp3", "audio/mpeg3", "audio/x-mpeg3",
      "audio/mpg", "audio/x-mpg", "audio/x-mpegaudio",
      // WAV
      "audio/wav", "audio/x-wav", "audio/wave",
      "audio/x-pn-wav",
      // OGG
      "audio/ogg", "application/ogg", "audio/x-ogg",
      "application/x-ogg",
      // FLAC
      "audio/flac",
      // WMA
      "audio/x-ms-wma",
      // Various
      "audio/rmf", "audio/x-rmf", "audio/vnd.qcelp",
      "audio/x-gsm", "audio/snd"
      )

  def supportedViews: List[String] = List("mp3")
  
  val BITRATE      = 128000 // 128 kbps
  val SAMPLE_FREQ  = 44100  // 44.1 KHz
  val MP3_FILENAME = "audio.mp3"
      
  def process(file: String, folder: File): Unit = {
    // Run `ffmpeg -i <inputfile> -f mp3 -acodec libmp3lame -ab <bitrate> -ar <sample_freq> -y <outputfile>`
    val mp3File = new File(folder, MP3_FILENAME)
    val cmd = new ProcessBuilder("ffmpeg", "-i", file, "-f", "mp3", "-acodec", "libmp3lame",
        "-ab", BITRATE.toString(), "-ar", SAMPLE_FREQ.toString(), "-y", mp3File.getAbsolutePath())
    cmd.redirectErrorStream(true)
    val process = cmd.start()
    // Flush the entire output
    flushActor(process).start
    process.waitFor()
  }
  
  def getMimeTypeForView(viewName: String): String = viewName match {
    case "mp3" => "audio/mpeg"
    case _     => null
  }
  
  def hasView(viewName: String, file: String): Boolean = {
    val folder = FileFolder.getFolder(file)
    viewName match {
      case "mp3" => {
        val mp3_file = new File(folder, MP3_FILENAME)
        mp3_file.exists()
      }
      case _ => false
    }
  }
  
  def getView(viewName: String, file: String): File = {
    val folder = FileFolder.getFolder(file)
    viewName match {
      case "mp3" => {
        val mp3_file = new File(folder, MP3_FILENAME)
        if (mp3_file.exists()) mp3_file else null
      }
      case _ => null
    }
  }

}