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
import com.nublic.filesAndUsers.java._
import scala.collection.JavaConversions._

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
  
  def withUser(action: User => Any) : Any = {
    val user = new User(request.getRemoteUser())
    action(user)
  }
  
  get("/apps") { withUser { user =>
    val apps = session.get(APPS).get.asInstanceOf[Map[String, AppData]]
    val favs = get_favourites_for(user.getUsername())
    val apps_json = apps.values.filter(a => a.web != None).map(
      a => a.toWeb(favs.contains(a.id))
    )
    write(apps_json)
  } }
  
  get("/app-image/:id/:size") { withUser { _ =>
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
  } }
  
  put("/favourite/:id") { withUser { user =>
    val id = params("id")
    val apps = session.get(APPS).get.asInstanceOf[Map[String, AppData]]
    val username = user.getUsername()
    val user_favs = get_favourites_for(username)
    if (apps.contains(id) && !user_favs.contains(id)) {
      set_favourites_for(username, id :: user_favs)
    }
    halt(200)
  } }
  
  delete("/favourite/:id") { withUser { user =>
    val id = params("id")
    val apps = session.get(APPS).get.asInstanceOf[Map[String, AppData]]
    val username = user.getUsername()
    val user_favs = get_favourites_for(username)
    if (apps.contains(id) && user_favs.contains(id)) {
      set_favourites_for(username, user_favs.remove(_ == id))
    }
    halt(200)
  } }
  
  get("/mirrors") { withUser { user =>
    user.getOwnedMirrors().toList.map(mirror =>
      write(ReturnMirror(mirror.getId(), mirror.getName()))
    )
  } }
  
  put("/mirror/*") { withUser { user =>
    val name = params(THE_REST)
    val m = Mirror.create(name, user.getUsername())
    m.getId()
  } }
  
  delete("/mirror/:mid") { withUser { user =>
    val mid = Integer.parseInt(params("mid"))
    val m = new Mirror(mid)
    if (m.exists() && m.getOwner().getUsername() == user.getUsername()) {
      m.delete(false)
      halt(200)
    } else {
      halt(403)
    }
  } }
  
  put("/mirror-name/:mid/*") { withUser { user =>
    val mid = Integer.parseInt(params("mid"))
    val name = params(THE_REST)
    val m = new Mirror(mid)
    if (m.exists() && m.getOwner().getUsername() == user.getUsername()) {
      m.setName(name)
      halt(200)
    } else {
      halt(403)
    }
  } }
  
  get("/synceds") { withUser { user =>
    user.getOwnedSyncedFolders().toList.map(mirror =>
      write(ReturnSyncedFolder(mirror.getId(), mirror.getName()))
    )
  } }
  
  put("/synced/*") {  withUser { user =>
    val name = params(THE_REST)
    val m = SyncedFolder.create(name, user.getUsername())
    m.getId()
  } }
  
  delete("/synced/:mid") { withUser { user =>
    val mid = Integer.parseInt(params("mid"))
    val m = new SyncedFolder(mid)
    if (m.exists() && m.getOwner().getUsername() == user.getUsername()) {
      m.delete(false)
      halt(200)
    } else {
      halt(403)
    }
  } }
  
  put("/synced-name/:mid") { withUser { user =>
    val mid = Integer.parseInt(params("mid"))
    val name = params(THE_REST)
    val m = new SyncedFolder(mid)
    if (m.exists() && m.getOwner().getUsername() == user.getUsername()) {
      m.setName(name)
      halt(200)
    } else {
      halt(403)
    }
  } }
  
  get("/users") { withUser { _ =>
    User.getAll().toList.map(user =>
      write(ReturnUser(user.getUsername(), user.getUserId(), user.getShownName()))
    )
  } }
  
  put("/user/:username") {
    
  }
  
  delete("/user/:username") {
    halt(500)
  }
  
  put("/user-name/*") { withUser { user =>
  	user.setShownName(params(THE_REST))
  	halt(200)
  } }
  
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
  
  def load_favourites : Map[String,List[String]] = {
    val f = new File(FAVOURITES_PATH)
    if (f.exists()) {
      try {
    	val reader = new FileReader(f)
    	read[Map[String, List[String]]](reader)  
      } catch {
        case e => {
          Console.println(e.getMessage())
          Map()
        }
      }
    } else {
      val initial_favs = Map[String, List[String]]()
      save_favourites(initial_favs)
      initial_favs
    }
  }
  
  def get_favourites_for(user: String) : List[String] = {
    val favs = session.get(FAVS).get.asInstanceOf[Map[String, List[String]]]
    if (favs.contains(user)) {
      favs.get(user).get
    } else {
      val new_favs = favs + (user -> get_initial_favs)
      save_favourites(new_favs)
      session.put(FAVS, new_favs)
      get_initial_favs
    }
  }
  
  def set_favourites_for(user: String, new_fav_list: List[String]) : Unit = {
    val favs = session.get(FAVS).get.asInstanceOf[Map[String, List[String]]]
    if (favs.contains(user)) {
      val no_user_favs = favs - user
      val new_favs = no_user_favs + (user -> new_fav_list)
      save_favourites(new_favs)
      session.put(FAVS, new_favs)
    } else {
      val new_favs = favs + (user -> new_fav_list)
      save_favourites(new_favs)
      session.put(FAVS, new_favs)
    }
  }
  
  def get_initial_favs: List[String] = List("browser", "music", "photos")
  
  def save_favourites(favs: Map[String, List[String]]) : Unit = {
    val f = new File(FAVOURITES_PATH)
    val writer = new FileWriter(f)
    write(favs, writer)
    writer.flush
    writer.close
  }

}
