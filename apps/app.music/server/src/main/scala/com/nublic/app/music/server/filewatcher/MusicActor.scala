package com.nublic.app.music.server.filewatcher

import com.nublic.filewatcher.scala.FileWatcherActor
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.Session
import org.squeryl.SessionFactory
import org.squeryl.adapters.PostgreSqlAdapter
import com.nublic.app.music.server.model.Database
import com.nublic.resource.java.App

class MusicActor extends FileWatcherActor("Music") {  

  loadMusicDb()
  
  val processors = Map("music" -> new MusicProcessor(this))
  
  def loadMusicDb(): Unit = {
    // Get information from nublic-resource
    val app = new App("nublic_app_music")
    val key = app.getKey("db")
    val postgreDb = "jdbc:postgresql://localhost:5432/" + key.getValue("database")
    val user = key.getValue("user")
    val password = key.getValue("password")
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
  }
}