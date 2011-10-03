package com.nublic.app.browser.server

case class BrowserFolder(val name: String, val folder: List[BrowserFolder])
case class BrowserFile(val name: String, val mime: String)
