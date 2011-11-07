package com.nublic.app.manager.server

case class AppData(id: String, name: Name, developer: String, icon: Map[String, String],
    web: Option[WebInfo], filewatcher: Option[FilewatcherInfo]) {
  def toWeb(favourite: Boolean): WebData = 
    WebData(id, name, developer, web.getOrElse(WebInfo(null)).path, favourite)
}
case class WebData(id: String, name: Name, developer: String, path: String, favourite: Boolean)

case class Name(default: String, localized: Map[String, String])
case class WebInfo(path: String)
case class FilewatcherInfo(supported: Boolean, paths: List[String])

