package com.nublic.app.downloads.server

import com.nublic.filesAndUsers.java.User
import com.nublic.ws.json.Response
import com.nublic.ws.json.Result
import java.io.File
import java.lang.Long
import java.net.URI
import java.net.URLDecoder
import java.util.Hashtable
import java.util.Timer
import java.util.TimerTask
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpUtils
import net.liftweb.json._
import net.liftweb.json.Serialization.{read, write}
import org.scalatra._
import org.scalatra.liftjson.JsonSupport
import org.scalatra.util.MapWithIndifferentAccess
import org.scalatra.util.MultiMapHeadView
import scala.collection.JavaConversions

class CommandDownloadsServer extends ScalatraServlet with JsonSupport {
  // JsonSupport adds the ability to return JSON objects
  
  val NUBLIC_DATA_ROOT = "/var/nublic/data/"
  val THE_REST = "splat"
  val ALL_OF_SOMETHING = "all"
    
  implicit val formats = Serialization.formats(NoTypeHints)

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

  def getAria(routeMatchers: org.scalatra.RouteMatcher)(action: (User, AriaDbUser) => Any) = getUser(routeMatchers) {
    user => {
      if (AriaDbUser.get.connected) {
        action(user, AriaDbUser.get)
      } else {
        write("not-connected")
      }
    }
  }

  def postAria(routeMatchers: org.scalatra.RouteMatcher)(action: (User, AriaDbUser) => Any) = postUser(routeMatchers) {
    user => {
      if (AriaDbUser.get.connected) {
        action(user, AriaDbUser.get)
      } else {
        write("not-connected")
      }
    }
  }

  def postAriaId(routeMatchers: org.scalatra.RouteMatcher)(action: (User, AriaDbUser, Long) => Any) = postAria(routeMatchers) {
    (user, aria) => {
      val downloadId = Long.parseLong(params("id"))
      if (aria.isOf(user, downloadId)) {
        action(user, aria, downloadId)
      } else {
        halt(404)
      }
    }
  }

  /* DOWNLOADS */

  postAria("/add") { (user, aria) => {
    if (params("source") != null && params("target") != null) {
      aria.addDownload(user, params("source"), params("target")) match {
        case None    => halt(500)
        case Some(s) => s
      }
    } else {
      halt(500)
    }
  } }

  postAriaId("/pause") { (user, aria, dId) => {

  } }

  postAriaId("/unpause") { (user, aria, dId) => {

  } }

  postAriaId("/stop") { (user, aria, dId) => {

  } }
  
  postAriaId("/remove") { (user, aria, dId) => {

  } }

  postAriaId("/change-target") { (user, aria, dId) => {

  } }

  getAria("/ask-properties") { (user, aria) => {

  } }

  /* OPTIONS */

  getAria("/global-options") { (user, aria) => {

  } }

  postAria("/global-options") { (user, aria) => {

  } }

  getAria("/aria-version") { (user, aria) => aria.getVersion match {
    case Result(r) => write(r)
    case _         => halt(500)
  } }

  notFound {  // Executed when no other route succeeds
    JNull
  }
}
