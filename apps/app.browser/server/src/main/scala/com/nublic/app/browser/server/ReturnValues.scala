package com.nublic.app.browser.server

case class BrowserFolder(val name: String, val subfolders: List[BrowserFolder])
case class BrowserFile(val name: String, val mime: String)
