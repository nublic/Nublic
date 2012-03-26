package com.nublic.app.photos.server.filewatcher

import com.nublic.filewatcher.scala.FileWatcherActor
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.Session
import org.squeryl.SessionFactory
import org.squeryl.adapters.PostgreSqlAdapter
import com.nublic.app.photos.server.model.Database
import com.nublic.resource.java.App
import java.util.logging.Logger
import javax.servlet.ServletContext
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
//import java.util.logging.Logger

class PhotoActor extends FileWatcherActor("Photos") {  
  
  loadPhotosDb()
  
  val processors = Map("photos" -> new PhotoProcessor(this))

  def loadPhotosDb(): Unit = {
    try {
      // Get information from nublic-resource
      val app = new App("nublic_app_photos")
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
        val f = new FileWriter("/var/nublic/log/nublic-app-photos-server.log", true)
        val pw = new PrintWriter(f)
        pw.print(e.getMessage() + "\n" + e.getStackTraceString)
        f.close()
      }
    }
  }
}