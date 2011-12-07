package com.nublic.app.browser.server

case class BrowserFolder(val name: String, val subfolders: List[BrowserFolder], val writable: Boolean)

case class BrowserFile(val name: String, val mime: String, val view: String,
  val size: Long, val last_update: Long, val writable: Boolean) {
  def isDirectory = mime == "application/x-directory"
}

case class BrowserDevice(val id: Int, val kind: String, val name: String, val owner: Boolean)
object BrowserDevice {
  val MEDIA = "media"
  val MIRROR = "mirror"
  val SYNCED_FOLDER = "synced"
}
