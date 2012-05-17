package com.nublic.app.market.server

import java.io.File
import java.io.FileReader
import java.io.InputStreamReader
import java.lang.Long
import java.net.URLDecoder
import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.methods.GetMethod
import org.scalatra._
import org.scalatra.liftjson.JsonSupport
import net.liftweb.json._
import net.liftweb.json.Serialization.{read, write}
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpUtils
import com.nublic.filesAndUsers.java._
import scala.collection.JavaConversions

class MarketServer extends ScalatraServlet with JsonSupport {
  
  implicit val formats = Serialization.formats(NoTypeHints)

  val PACKAGES_URL: String = "http://nublic.com/packages.json"
  val MAX_UPDATE_DIFF: Long = 1 // 2 * 3600 * 1000 // 2 hours
  val USE_LOCAL = true
  val LOCAL_PACKAGES_PATH = "/var/lib/nublic/packages.json"

  var packages_list: Option[List[Package]] = None
  var last_packages_update: Option[Long] = None

  def splitThatRespectsReasonableSemantics(sep: String)(s: String) : List[String] = {
    val v = s.trim()
    if (v == "") {
      List()
    } else {
      v.split(sep).toList.map(_.trim()).filter(_ != "")
    }
  }
  
  var _extraParams : Option[scala.collection.immutable.Map[String, String]] = None
  
  def extraParams : Map[String, String] = {
    if (_extraParams == None) {
      // Get body
      val len = request.getContentLength()
      val in = request.getInputStream()
      val bytes = new Array[Byte](len)
      var offset = 0
      do {
        val inputLen = in.read(bytes, offset, len - offset)
        if (inputLen <= 0) {
          throw new IllegalArgumentException("unable to parse body")
        }
        offset += inputLen
      } while ((len - offset) > 0)
      val body = new String(bytes, 0, len, "8859_1");

      // Try to detect charset
      val charset = if (request.getHeader("Content-Type") != null) {
        val ct = request.getHeader("Content-Type")
        val charset_index = ct.indexOf("charset=")
        if (charset_index != -1) {
          ct.substring(charset_index + "charset=".length()).trim().toUpperCase()
        } else {
          "UTF-8"
        }
      } else {
        "UTF-8"
      }

      // Parse body
      val tuples = splitThatRespectsReasonableSemantics("&")(body).map(t => {
        val e = splitThatRespectsReasonableSemantics("=")(t)
        if (e.size == 1) { // Empty parameter
          (URLDecoder.decode(e(0), charset), "")
        } else {
          (URLDecoder.decode(e(0), charset), URLDecoder.decode(e(1), charset))
        }
      } )
      
      _extraParams = Some(Map() ++ tuples)
    }
    _extraParams.get
  }
  
  def put2(routeMatchers: org.scalatra.RouteMatcher)(action: =>Any) = put(routeMatchers) {
    _extraParams = None
    action
  }
  
  def delete2(routeMatchers: org.scalatra.RouteMatcher)(action: =>Any) = delete(routeMatchers) {
    _extraParams = None
    action
  }
  
  def withUser(action: User => Any) : Any = {
    val user = new User(request.getRemoteUser())
    action(user)
  }
  
  def getUser(routeMatchers: org.scalatra.RouteMatcher)(action: User => Any) = get(routeMatchers) {
    withUser(action)
  }
  
  def postUser(routeMatchers: org.scalatra.RouteMatcher)(action: User => Any) = post(routeMatchers) {
    withUser(action)
  }
  
  def putUser(routeMatchers: org.scalatra.RouteMatcher)(action: User => Any) = put2(routeMatchers) {
    withUser(action)
  }
  
  def deleteUser(routeMatchers: org.scalatra.RouteMatcher)(action: User => Any) = delete2(routeMatchers) {
    withUser(action)
  }
    
  // Collections
  // ===========
  getUser("/about") { _ =>
    "Nublic Server v0.0.2"
  }

  def ensure_updated_packages() = {
    val now = System.currentTimeMillis()
    if (!packages_list.isDefined || !last_packages_update.isDefined || (now - last_packages_update.get) > MAX_UPDATE_DIFF) {
      // Execute apt-get update
      Singleton.getApt().update_cache()
      val new_packages_list = grab_packages()
      Console.err.println("Grabbed package")
      Console.err.println(new_packages_list.toString())
      if (new_packages_list.isDefined) {
        last_packages_update = Some(now)
        packages_list = new_packages_list
      }
    }
  }

  def grab_packages(): Option[List[Package]] = {
    if (USE_LOCAL) {
      val f = new File(LOCAL_PACKAGES_PATH)
      if (f.exists()) {
        try {
          val reader = new FileReader(f)
          val lst = read[List[Package]](reader)
          Some(lst)
        } catch {
          case e: Exception => {
            Console.err.println(e.getMessage())
            None
          }
        }
      } else {
        None
      }
    } else {
      try {
        val client = new HttpClient()
        val method = new GetMethod(PACKAGES_URL)
        val result = client.executeMethod(method)
        val reader = new InputStreamReader(method.getResponseBodyAsStream())
        Some(read[List[Package]](reader))
      } catch {
        case e: Exception => {
          Console.err.println(e.getMessage())
          None
        }
      }
    }
  }

  def withPackageList(action: List[Package] => Any) = {
    try {
      ensure_updated_packages()
    } catch {
      case e: Throwable => halt(status = 500,
                                headers = Map("Content-Type" -> "application/json"),
                                body = write(e.getMessage()))
    }

    if (!packages_list.isDefined) {
      halt(status = 500,
           headers = Map("Content-Type" -> "application/json"),
           body = write("no package list"))
    } else {
      action(packages_list.get)
    }
  }

  def withPackage(action: Package => Any) = {
    withPackageList { lst =>
      extraParams.get("package") match {
        case None => halt(status = 500,
                          headers = Map("Content-Type" -> "application/json"),
                          body = write("no package name"))
        case Some(pkg_name) => 
          lst.find(p => p.id == pkg_name) match {
            case None => halt(status = 404,
                              headers = Map("Content-Type" -> "application/json"),
                              body = write(InstallStatus(InstallStatus.STATUS_NOT_EXIST, None)))
            case Some(pkg) => action(pkg)
          }
      }
    }
  }

  getUser("/packages") { _ =>
    withPackageList { lst =>
      var info = lst.map((p: Package) => 
        if (Singleton.getApt().is_package_installed(p.deb)) {
          Package.change_status(p, Package.STATUS_INSTALLED)
        } else {
          Package.change_status(p, Package.STATUS_NOT_INSTALLED)
        })
      write(info)
    }
  }

  def try_and_send_result(b: Boolean) = {
    if (b) {
      write(InstallStatus(InstallStatus.STATUS_OK, None))
    } else {
      write(InstallStatus(InstallStatus.STATUS_ERROR, None))
    }
  }

  putUser("/packages") { _ =>
    withPackage { pkg =>
      if (Singleton.getApt().is_package_installed(pkg.deb)) {
        write(InstallStatus(InstallStatus.STATUS_ALREADY, None))
      } else {
        try_and_send_result(Singleton.getApt().install_package(pkg.deb))
      }
    }
  }

  deleteUser("/packages") { _ =>
    withPackage { pkg =>
      if (!Singleton.getApt().is_package_installed(pkg.deb)) {
        write(InstallStatus(InstallStatus.STATUS_NOT, None))
      } else {
        try_and_send_result(Singleton.getApt().remove_package(pkg.deb))
      }
    }
  }

  postUser("/upgrade") { _ =>
    try_and_send_result(Singleton.getApt().upgrade_system())
  }
  
  notFound {  // Executed when no other route succeeds
    JNull
  }
}




