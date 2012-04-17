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
  
  var __extraParams : Option[scala.collection.immutable.Map[String, Seq[String]]] = None
  
  def _extraParams : Map[String, Seq[String]] = {
    if (__extraParams == None) {
      val ht = HttpUtils.parsePostData(request.getContentLength(),
          request.getInputStream())
      __extraParams = Some(JavaConversions.mapAsScalaMap(ht.asInstanceOf[Hashtable[String, Array[String]]]).toMap.map(f => (f._1, f._2.toSeq)))
    }
    __extraParams.get
  }
  
  protected val extraParams = new MultiMapHeadView[String, String] with MapWithIndifferentAccess[String] {
    protected def multiMap = _extraParams
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
  
  def splitThatRespectsReasonableSemantics(sep: String)(s: String) : List[String] = {
    val v = s.trim()
    if (v == "") {
      List()
    } else {
      v.split(sep).toList.map(_.trim()).filter(_ != "")
    }
  }
    
  notFound {  // Executed when no other route succeeds
    JNull
  }
}
