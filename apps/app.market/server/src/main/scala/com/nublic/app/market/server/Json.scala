package com.nublic.app.market.server

case class TranslatedText(val default: String, val translated: Map[String, String])

case class Package(val id: String, val icon: String, val name: TranslatedText,
                   val short_description: TranslatedText, val long_description: TranslatedText,
                   val screenshots: List[String], val categories: List[String],
                   val links: List[String], val developer: String,
                   val deb: String, val status:Option[String])

object Package {
  val STATUS_INSTALLED     = "installed"
  val STATUS_INSTALLING    = "installing"
  val STATUS_NOT_INSTALLED = "not-installed"
  val STATUS_ERROR         = "error"

  def change_status(p: Package, status: String) = 
    Package(p.id, p.icon, p.name, p.short_description, p.long_description,
            p.screenshots, p.categories, p.links, p.developer, p.deb, Some(status))
}

case class InstallStatus(val status: String, val errors: Option[List[String]])

object InstallStatus {
  val STATUS_NOT_EXIST = "does-not-exist"
  val STATUS_ALREADY   = "already-installed"
  val STATUS_NOT       = "not-installed"
  val STATUS_OK        = "ok"
  val STATUS_ERROR     = "error"
}

