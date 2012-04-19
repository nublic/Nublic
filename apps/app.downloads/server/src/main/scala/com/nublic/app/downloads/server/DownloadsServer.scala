package com.nublic.app.downloads.server

import com.nublic.filesAndUsers.java.User
import java.io.File
import java.lang.Long
import java.util.Hashtable
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpUtils
import net.liftweb.json._
import net.liftweb.json.Serialization.{read, write}
import org.scalatra._
import org.scalatra.liftjson.JsonSupport
import org.scalatra.util.MapWithIndifferentAccess
import org.scalatra.util.MultiMapHeadView
import scala.collection.JavaConversions

class PhotosServer extends ScalatraServlet with JsonSupport {
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
        (URLDecoder.decode(e(0), charset), URLDecoder.decode(e(1), charset))
      } )
      
      _extraParams = Some(Map() ++ tuples)
    }
    _extraParams.get
  }
  
  def put2(routeMatchers: org.scalatra.RouteMatcher)(action: =>Any) = put(routeMatchers) {
    __extraParams = None
    action
  }
  
  def delete2(routeMatchers: org.scalatra.RouteMatcher)(action: =>Any) = delete(routeMatchers) {
    __extraParams = None
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
  
  notFound {  // Executed when no other route succeeds
    JNull
  }
}
