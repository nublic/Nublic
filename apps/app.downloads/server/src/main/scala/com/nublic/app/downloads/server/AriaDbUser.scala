package com.nublic.app.downloads.server

import com.nublic.app.downloads.server.model._
import com.nublic.app.downloads.server.model.Database._
import com.nublic.filesAndUsers.java.User
import com.nublic.resource.java.App
import com.nublic.ws.json.Result
import java.io.FileWriter
import java.io.PrintWriter
import java.util.Timer
import java.util.TimerTask
import net.liftweb.json.NoTypeHints
import net.liftweb.json.Serialization
import net.liftweb.json.Serialization.{read, write}
import net.zschech.gwt.comet.server.CometServletResponse
import net.zschech.gwt.comet.server.CometSession
import org.apache.commons.codec.binary.Base64
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.Session
import org.squeryl.SessionFactory
import org.squeryl.adapters.PostgreSqlAdapter
import scala.collection.mutable.HashMap
import scala.collection.mutable.MultiMap
import scala.io.Source

object AriaDbUser {
  var _instance: Option[AriaDbUser] = None
  def get: AriaDbUser = _instance match {
    case None => {
      val v = new AriaDbUser()
      _instance = Some(v)
      v
    }
    case Some(d) => d
  }
}

class AriaDbUser extends AriaEventHandler {
  
  implicit val formats = Serialization.formats(NoTypeHints)

  loadDb()

  private val conn: Aria = new Aria()
  conn.addEventHandler(this)
  conn.connect("ws://localhost:6800/jsonrpc")
  // Keep connection alive
  val timer = new Timer()
  class ShootTask(a: Aria) extends TimerTask {
    def run(): Unit = {
      if (a.connected) {
        Console.print("sending heartbeat\n")
        a.getVersion.shoot()
      }
    }
  }
  timer.schedule(new ShootTask(conn), 10000, 10000)

  def connected = conn.connected

  // Information about downloads
  var aria_id_to_db_id = Map[String, Long]()  // aria_id -> db_id
  val downloads_for_uid = new HashMap[Int, scala.collection.mutable.Set[Long]] with MultiMap[Int, Long]  // uid -> { db_id }

  // Information about connected users
  var uids_to_connections = new HashMap[Int, scala.collection.mutable.Set[CometSession]]
    with MultiMap[Int, CometSession] // uid -> { conn }

  /* ARIA HANDLING */
  def onConnect(): Unit = {
    def task = new TimerTask() {
      def run(): Unit = {
        // Save the information in the maps
        for (download_status <- getAllFilesInAria) {
          val download_gid = download_status.gid
          conn.getUris(download_gid.get) match {
            case Result(uris) => Database.downloadBySource(uris(0).uri) match {
              case Some(down_db) => {
                aria_id_to_db_id += download_gid.get -> down_db.id
                downloads_for_uid.addBinding(down_db.uid, down_db.id)
              }
              case None          => { /* Do nothing */ }
            }
            case _ => { /* Can't do nothing */ }
          }
        }
      }
    }
    (new Timer()).schedule(task, 2000)
  }

  def onDisconnect(): Unit = {

  }

  def onStop(): Unit = {

  }

  def onDownloadStart(gid: String): Unit = {
    
  }

  def onDownloadError(gid: String): Unit = {

  }

  // Periodically send information
  val info_timer = new Timer()
  class SendInfoTask(a: AriaDbUser) extends TimerTask {
    def run(): Unit = {
      if (a.connected) {
        a.sendInfo()
      }
    }
  }
  info_timer.schedule(new SendInfoTask(this), 10000, 10000)

  def sendInfo(): Unit = {
    for (u <- uids_to_connections.keys) {
      sendInfo(u)
    }
  }

  def sendInfo(u: User): Unit = sendInfo(u.getUserId())
  def sendInfo(u: Int): Unit = {
    Console.println("Sending all files")
    val files = getAllFilesInAriaJson.filter { ds =>
        downloads_for_uid.get(u).isDefined &&
        downloads_for_uid.get(u).get.contains(ds.id)
    }
    Console.println(write(files))
    for (conn <- uids_to_connections.getOrElse(u, Set())) {
      conn.enqueue(write(files))
    }
  }

  /* COMET SESSION HANDLING */
  def addUserConnection(u: User, session: CometSession) = {
    uids_to_connections.addBinding(u.getUserId(), session)
  }

  def removeUserConnection(u: User, session: CometSession) = {
    uids_to_connections.removeBinding(u.getUserId(), session)
  }

  /* DOWNLOADS HANDLING */

  def getAllFilesInAria: List[AriaDownloadStatus] = {
    // Get list of files in Aria
    val active = conn.tellActive() match {
      case Result(a) => a
      case _         => List()
    }
    val waiting = conn.tellWaiting(0, 1000) match {
      case Result(a) => a
      case _         => List()
    }
    val stopped = conn.tellStopped(0, 1000) match {
      case Result(a) => a
      case _         => List()
    }
    active ++ waiting ++ stopped
  }

  def getAllFilesInAriaJson: List[JsonDownload] = {
    getAllFilesInAria.map { ds =>
      if (ds.gid.isDefined && ds.status.isDefined) {
        val gid = ds.gid.get
        val status = ds.status.get
        val dbid = aria_id_to_db_id.get(gid)
        if (dbid.isDefined) {
          val dbd = Database.downloads.get(dbid.get)
          Some(JsonDownload(dbid.get, dbd.source, dbd.target, status,
                       ds.downloadSpeed.map(java.lang.Long.parseLong(_)),
                       ds.uploadSpeed.map(java.lang.Long.parseLong(_)),
                       ds.totalLength.map(java.lang.Long.parseLong(_)),
                       ds.completedLength.map(java.lang.Long.parseLong(_)),
                       ds.uploadLength.map(java.lang.Long.parseLong(_))))
        } else {
          None
        }
      } else {
        None
      }
    }.filter(_.isDefined).map(_.get)
  }

  def isOf(u : User, id: Long): Boolean = {
    downloads_for_uid.get(u.getUserId()) match {
      case None    => false
      case Some(d) => d.contains(id)
    }
  }
 
  def addDownload(u: User, src: String, target: String): Option[String] = {
    // Add to Aria2
    if (src.endsWith(".torrent")) {
      // It's a torrent file
      val source = Source.fromURL(src)
      val file = source.map(_.toByte).toArray
      source.close()
      val base64 = Base64.encodeBase64String(file)
      conn.addTorrent(base64) match {
        case Result(s) => Some(s)
        case _         => None
      }
    } else {
      conn.addUri(Array(src)) match {
        case Result(s) => Some(s)
        case _         => None
      }
    } match {
      case Some(s) => {
        // Add to database
        //Console.println("Added aria id " + s)
        // 1. Get info from source
        conn.getUris(s) match {
          case Result(uris) => {
            val uri = uris(0).uri
            // 2. Add to database
            val d = new Download()
            d.source = uri
            d.target = target
            d.uid = u.getUserId()
            Database.downloads.insert(d)
            // 3. Add to internal info
            aria_id_to_db_id += s -> d.id
            downloads_for_uid.addBinding(u.getUserId(), d.id)
            // 4. Send new info
            Console.println("Sending info")
            sendInfo(u)
            // 5. Finish
            Some(s)
          }
          case _ => {
            Console.println("this uri is not here")
            None // Strange error
          }
        }
      }
      case None    => None
    }
  }

  /* ARIA COMMANDS */
  def getGlobalStats = conn.getGlobalStat()
  def getVersion = conn.getVersion()

  /* DATABASE HANDLING */
  def loadDb(): Unit = {
    try {
      // Get information from nublic-resource
      val app = new App("nublic_app_downloads")
      val key = app.getKey("db")
      val postgreDb = "jdbc:postgresql://localhost:5432/" + key.getValue("database")
      val user = key.getValue("user")
      val password = key.getValue("pass")
      // Load PostgreSQL driver and create connection
      Class.forName("org.postgresql.Driver").newInstance();
      SessionFactory.concreteFactory = Some(() => Session.create(
        java.sql.DriverManager.getConnection(postgreDb, user, password),
        new PostgreSqlAdapter)
      )
      // Create tables if they do not exist
      transaction {
        try {
          Database.create
        } catch {
          case _ => { /* Table is already created */ }
        }
      }
    } catch {
      case e => {
        val f = new FileWriter("/var/nublic/log/nublic-app-downloads-server.log", true)
        val pw = new PrintWriter(f)
        pw.print(e.getMessage() + "\n" + e.getStackTraceString)
        f.close()
      }
    }
  }
}
