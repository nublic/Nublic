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
import scala.collection.JavaConversions
import scala.collection.JavaConversions._
import javax.servlet.http.HttpUtils
import java.util.Hashtable
import org.scalatra.util.MapWithIndifferentAccess
import org.scalatra.util.MultiMapHeadView
import scala.util.Random
import java.util.Date
import java.io.PrintWriter

class ManagerServer extends ScalatraFilter with JsonSupport {
  // JsonSupport adds the ability to return JSON objects
  
  val NUBLIC_APP_DATA_ROOT = "/var/lib/nublic/apps"
  val FAVOURITES_PATH = "/var/nublic/cache/manager.favourites"
  val THE_REST = "splat"
  
  val APPS = "apps"
  val FAVS = "favs"
  
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
  
  get("/app-image/:style/:id/:size") { withUser { _ =>
    val apps = session.get(APPS).get.asInstanceOf[Map[String, AppData]]
    val id = params("id")
    val size = params("size")
    if (apps.contains(id)) {
      val app = apps.get(id).get
      val icons = params.get("style") match {
        case Some("dark")  => Some(app.getDarkIcon)
        case Some("light") => Some(app.getLightIcon)
        case Some("color") => Some(app.color_icon)
        case _             => None
      }
      if (icons != None && icons.get.contains(size)) {
        response.setContentType("image")
        new File(icons.get.get(size).get)
      } else {
        halt(404)
      }
    } else {
      halt(404)
    }
  } }
  
  put2("/favourite") { withUser { user =>
    val id = extraParams("id")
    val apps = session.get(APPS).get.asInstanceOf[Map[String, AppData]]
    val username = user.getUsername()
    val user_favs = get_favourites_for(username)
    if (apps.contains(id) && !user_favs.contains(id)) {
      set_favourites_for(username, id :: user_favs)
    }
    halt(200)
  } }
  
  delete2("/favourite") { withUser { user =>
    val id = extraParams("id")
    val apps = session.get(APPS).get.asInstanceOf[Map[String, AppData]]
    val username = user.getUsername()
    val user_favs = get_favourites_for(username)
    if (apps.contains(id) && user_favs.contains(id)) {
      set_favourites_for(username, user_favs.remove(_ == id))
    }
    halt(200)
  } }
  
  get("/mirrors") { withUser { user =>
    write(user.getOwnedMirrors().toList.map(mirror =>
      ReturnMirror(mirror.getId(), mirror.getName())
    ).sortBy(_.name))
  } }
  
  put2("/mirrors") { withUser { user =>
    val name = extraParams("name")
    val m = Mirror.create(name, user.getUsername())
    m.getId().toString()
  } }
  
  delete2("/mirrors") { withUser { user =>
    val mid = Integer.parseInt(extraParams("id"))
    val m = new Mirror(mid)
    if (m.exists() && m.getOwner().getUsername() == user.getUsername()) {
      m.delete(false)
      halt(200)
    } else {
      halt(403)
    }
  } }
  
  put2("/mirror-name") { withUser { user =>
    val mid = Integer.parseInt(extraParams("id"))
    val name = extraParams("name")
    val m = new Mirror(mid)
    if (m.exists() && m.getOwner().getUsername() == user.getUsername()) {
      m.setName(name)
      halt(200)
    } else {
      halt(403)
    }
  } }
  
  get("/synceds") { withUser { user =>
    write(user.getOwnedSyncedFolders().toList.map(mirror =>
      ReturnSyncedFolder(mirror.getId(), mirror.getName())
    ).sortBy(_.name))
  } }
  
  put2("/synceds") {  withUser { user =>
    val name = extraParams("name")
    val m = SyncedFolder.create(name, user.getUsername())
    m.getId().toString()
  } }
  
  delete2("/synceds") { withUser { user =>
    val mid = Integer.parseInt(extraParams("id"))
    val m = new SyncedFolder(mid)
    if (m.exists() && m.getOwner().getUsername() == user.getUsername()) {
      m.delete(false)
      halt(200)
    } else {
      halt(403)
    }
  } }
  
  var current_upload_keys = scala.collection.mutable.Map[Long, Tuple2[User, Long]]()
  
  def prune_old_upload_keys = {
    // Prune those older than 5 minutes
    val end_time = (new Date()).getTime() - 5 * 60 * 1000 /* 5 minutes */
    current_upload_keys = current_upload_keys.filter(kv => kv._2._2 > end_time)
  }
  
  get("/synced-upload-key/:id") {
    write("Use POST for uploading keys")
  }
  
  post("/synced-upload-key/:id") {
    val id = java.lang.Long.parseLong(params("id"))
    val pubkey = params("pubkey")
    // Prune old elements
    prune_old_upload_keys
    // Try to find ours
    current_upload_keys.get(id) match {
      case None    => halt(500)
      case Some(v) => {
        val user = v._1
        // Append the key to authorized_keys
        val akeys_file = "/home/" + user.getUsername() + "/.ssh/authorized_keys"
        val fw = new FileWriter(akeys_file, true)
        val pw = new PrintWriter(fw)
        pw.println(pubkey)
        pw.close()
        fw.close()
      }
    }
  }
  
  get("/synced-invite/:id") { withUser { user =>
    val mid = Integer.parseInt(params("id"))
    val m = new SyncedFolder(mid)
    if (m.exists() && user.canRead(m)) {
      // Generate new id for key uploading
      val upload_key_id = Random.nextLong().abs
      current_upload_keys += upload_key_id -> ( user, (new Date()).getTime() )
      // Generate invitation
      val server = request.getServerName()
      val xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<sparkleshare>\n" +
                "  <invite>\n" +
                "    <address>ssh://" + user.getUsername() + "@" + server + "/</address>\n" +
                "    <remote_path>/var/nublic/work-folders/" + mid.toString() + "</remote_path>\n" +
                "    <accept_url>http://" + server + "/manager/server/synced-upload-key/" + upload_key_id.toString() + "</accept_url>\n" +
                "  </invite>\n" +
                "</sparkleshare>"
      xml
    } else {
      halt(500)
    }
  } }
  
  put2("/synced-name") { withUser { user =>
    val mid = Integer.parseInt(extraParams("id"))
    val name = extraParams("name")
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
  
  put2("/users") {
    
  }
  
  delete2("/users") {
    halt(500)
  }
  
  get("/user-name") { withUser { user =>
    user.getShownName()
  } }
  
  get("/user-info") { withUser { user =>
    write(ReturnUser(user.getUsername(), user.getUserId(), user.getShownName()))
  } }
  
  put2("/user-info") { withUser { user =>
    if (!extraParams.contains("name")) {
      write(extraParams)
      halt(500)
    } else {
      user.setShownName(extraParams("name"))
      halt(200)
    }
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
