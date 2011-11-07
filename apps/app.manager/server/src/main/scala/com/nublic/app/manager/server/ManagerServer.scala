package com.nublic.app.manager.server

import java.io.File
import org.scalatra._
import org.scalatra.liftjson.JsonSupport
import net.liftweb.json._
import net.liftweb.json.Serialization.{read, write}
import javax.servlet.http.HttpServlet
import java.io.FilenameFilter
import java.io.FileReader
import java.io.FileWriter

class ManagerServer extends ScalatraFilter with JsonSupport {
  // JsonSupport adds the ability to return JSON objects
  
  val NUBLIC_APP_DATA_ROOT = "/var/lib/nublic/apps"
  val FAVOURITES_PATH = "/var/nublic/cache/manager.favourites"
  val THE_REST = "splat"
  
  val APPS = "apps"
  val FAVS = "favs"
  
  implicit val formats = Serialization.formats(NoTypeHints)
  
  before() {
    session.put(APPS, load_apps)
    session.put(FAVS, load_favourites)
  }
  
  get("/apps") {
    val apps = session.get(APPS).get.asInstanceOf[Map[String, AppData]]
    val favs = session.get(FAVS).get.asInstanceOf[List[String]]
    val apps_json = apps.values.filter(a => a.web != None).map(
      a => a.toWeb(favs.contains(a.id))
    )
    write(apps_json)
  }
  
  get("/app-image/:id/:size") {
    val apps = session.get(APPS).get.asInstanceOf[Map[String, AppData]]
    val id = params("id")
    val size = params("size")
    if (apps.contains(id)) {
      val app = apps.get(id).get
      if (app.icon.contains(size)) {
        response.setContentType("image")
        new File(app.icon.get(size).get)
      } else {
        halt(404)
      }
    } else {
      halt(404)
    }
  }
  
  put("/favourite/:id") {
    val id = params("id")
    val apps = session.get(APPS).get.asInstanceOf[Map[String, AppData]]
    val favs = session.get(FAVS).get.asInstanceOf[List[String]]
    if (apps.contains(id) && !favs.contains(id)) {
      save_favourites(id :: favs)
    }
  }
  
  delete("/favourite/:id") {
    val id = params("id")
    val apps = session.get(APPS).get.asInstanceOf[Map[String, AppData]]
    val favs = session.get(FAVS).get.asInstanceOf[List[String]]
    if (apps.contains(id) && favs.contains(id)) {
      save_favourites(favs.remove(_ == id))
    }
  }
  
  notFound {  // Executed when no other route succeeds
    JNull
  }
  
  def load_apps : Map[String, AppData] = {
    val app_root = new File(NUBLIC_APP_DATA_ROOT)
    val files = app_root.listFiles(new FilenameFilter() {
      def accept(f: File, name: String) = name.endsWith(".json")
    })
    var return_map = Map[String, AppData]()
    for (file <- files) {
      try {
	    val reader = new FileReader(file)
	    val data = read[AppData](reader)
	    return_map += (data.id -> data)
      } catch {
        case e => Console.println(e.getMessage())
      }
    }
    return_map
  }
  
  def load_favourites : List[String] = {
    val f = new File(FAVOURITES_PATH)
    if (f.exists()) {
      try {
    	val reader = new FileReader(f)
    	read(reader)  
      } catch {
        case _ => Nil
      }
    } else {
      val initial_favs = List("browser", "music", "photos")
      save_favourites(initial_favs)
      initial_favs
    }
  }
  
  def save_favourites(favs: List[String]) : Unit = {
    val f = new File(FAVOURITES_PATH)
    val writer = new FileWriter(f)
    write(favs, writer)
    writer.flush
    writer.close
  }

}
