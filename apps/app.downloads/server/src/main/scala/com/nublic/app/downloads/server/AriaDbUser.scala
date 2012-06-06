package com.nublic.app.downloads.server

import com.nublic.app.downloads.server.model._
import com.nublic.app.downloads.server.model.Database._
import com.nublic.resource.java.App
import com.nublic.ws.json.Result
import java.io.FileWriter
import java.io.PrintWriter
import java.util.Timer
import java.util.TimerTask
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.Session
import org.squeryl.SessionFactory
import org.squeryl.adapters.PostgreSqlAdapter
import scala.collection.mutable.HashMap
import scala.collection.mutable.MultiMap

class AriaDbUser extends AriaEventHandler {
  
  loadDb()

  private val conn: Aria = new Aria()
  conn.addEventHandler(this)
  conn.connect("ws://localhost:6800/jsonrpc")

  // Keep connection alive
  val timer = new Timer()
  class ShootTask(a: Aria) extends TimerTask {
    def run(): Unit = {
      if (a.connected) {
        a.getVersion.shoot()
      }
    }
  }
  timer.schedule(new ShootTask(conn), 5000, 5000)

  // Information about downloads
  var aria_id_to_db_id = Map[String, Long]()  // aria_id -> db_id
  val downloads_for_uid = new HashMap[Long, scala.collection.mutable.Set[Long]] with MultiMap[Long, Long]  // uid -> { db_id }

  def onConnect(): Unit = {
    // Initialize information
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
    // Save the information in the maps
    for (download_status <- active ++ waiting ++ stopped) {
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

  def onDisconnect(): Unit = {

  }

  def onStop(): Unit = {

  }

  def onDownloadStart(gid: Long): Unit = {

  }

  def getGlobalStats = conn.getGlobalStat()

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
