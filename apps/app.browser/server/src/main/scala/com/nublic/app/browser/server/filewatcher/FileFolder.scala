package com.nublic.app.browser.server.filewatcher

import java.io.File
import org.apache.commons.codec.digest.DigestUtils

object FileFolder {
  
  val ROOT_FOLDER = "/var/nublic/cache/browser"
  val THUMBNAIL_FILENAME = "thumbnail.png"
  val THUMBNAIL_SIZE = 96
  val MAX_IMAGE_WIDTH = 1920
  val MAX_IMAGE_HEIGHT = 1200
  
  def getName(filepath: String): String = DigestUtils.shaHex(filepath)
  def getFolder(filepath: String): File = new File(ROOT_FOLDER, getName(filepath))
  def getThumbnail(filepath: String): File = new File(getFolder(filepath), THUMBNAIL_FILENAME)
}