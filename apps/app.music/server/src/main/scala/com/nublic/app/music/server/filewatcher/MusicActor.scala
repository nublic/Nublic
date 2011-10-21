package com.nublic.app.music.server.filewatcher

import com.nublic.filewatcher.scala.FileWatcherActor
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.Session
import org.squeryl.SessionFactory
import org.squeryl.adapters.PostgreSqlAdapter
import com.nublic.app.music.server.model.Database

class MusicActor extends FileWatcherActor("Music") {  
  // To create the database
  // # sudo -u postgres createuser nublic
  // # sudo -u postgres psql
  // postgre> \password nublic (insert "nublic" as password)
  // postgre> CREATE DATABASE music OWNER nublic;
  val postgreDb = "jdbc:postgresql://localhost:5432/music"
  val user = "nublic"
  val password = "nublic"
  loadMusicDb()
  
  val processors = Map("music" -> new MusicProcessor(this))
  
  def loadMusicDb(): Unit = {
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