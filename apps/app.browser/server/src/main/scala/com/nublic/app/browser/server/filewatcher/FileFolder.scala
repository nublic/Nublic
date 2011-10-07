package com.nublic.app.browser.server.filewatcher

import java.io.File
import org.apache.commons.codec.digest.DigestUtils

object FileFolder {
  
  val ROOT_FOLDER = "/var/nublic/cache/browser"
  
  def getName(filepath: String): String = DigestUtils.shaHex(filepath)
  def getFolder(filepath: String): File = new File(ROOT_FOLDER, getName(filepath))
}