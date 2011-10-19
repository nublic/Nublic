package com.nublic.app.music.server

import java.io.File
import org.scalatra._
import org.scalatra.liftjson.JsonSupport
import net.liftweb.json._
import net.liftweb.json.Serialization.{read, write}
import javax.servlet.http.HttpServlet
import com.nublic.app.music.server.filewatcher.MusicActor

class MusicServer extends ScalatraFilter with JsonSupport {
  // JsonSupport adds the ability to return JSON objects
  
  val NUBLIC_DATA_ROOT = "/var/nublic/data/"
  val THE_REST = "splat"
  
  val watcher = new MusicActor()
  watcher.start()
  
  implicit val formats = Serialization.formats(NoTypeHints)
  
  
  notFound {  // Executed when no other route succeeds
    JNull
  }
}
