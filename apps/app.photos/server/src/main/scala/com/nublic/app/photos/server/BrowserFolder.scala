package com.nublic.app.photos.server
import org.apache.commons.codec.digest.DigestUtils
import java.io.File

/**
 * Reference to things implemented in Browser,
 * which is responsible of handling conversion
 * to mp3. This should disappear at some point. 
 */
object BrowserFolder {
  
  val ROOT_FOLDER = "/var/nublic/cache/browser"
  val THUMBNAIL_FILENAME = "thumbnail.png"
  val IMAGE_FILENAME = "image.png"
  
  def getName(filepath: String): String = DigestUtils.shaHex(filepath)
  def getFolder(filepath: String): File = new File(ROOT_FOLDER, getName(filepath))
  def getThumbnail(filepath: String): File = new File(getFolder(filepath), THUMBNAIL_FILENAME)
  def getImage(filepath: String): File = new File(getFolder(filepath), IMAGE_FILENAME)
}