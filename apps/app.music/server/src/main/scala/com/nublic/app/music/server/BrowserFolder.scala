package com.nublic.app.music.server
import org.apache.commons.codec.digest.DigestUtils
import java.io.File

/**
 * Reference to things implemented in Browser,
 * which is responsible of handling conversion
 * to mp3. This should disappear at some point. 
 */
object BrowserFolder {
  
  val ROOT_FOLDER = "/var/nublic/cache/browser"
  val MP3_FILENAME = "audio.mp3"
  
  def getName(filepath: String): String = DigestUtils.shaHex(filepath)
  def getFolder(filepath: String): File = new File(ROOT_FOLDER, getName(filepath))
  def getMp3(filepath: String): File = new File(getFolder(filepath), MP3_FILENAME)
}