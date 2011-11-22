package com.nublic.app.browser.server

case class BrowserFolder(val name: String, val subfolders: List[BrowserFolder])
case class BrowserFile(val name: String, val mime: String, val view: String,
  val size: Long, val last_update: Long) {
  def isDirectory = mime == "application/x-directory"
}
