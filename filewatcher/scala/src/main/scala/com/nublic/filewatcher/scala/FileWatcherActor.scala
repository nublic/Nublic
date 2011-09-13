package com.nublic.filewatcher.scala

import scala.actors.Actor
import scala.actors.Actor._
import org.freedesktop.dbus.DBusConnection
import org.freedesktop.dbus.DBusSigHandler
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

abstract class FileWatcherActor extends Actor {
  val app_name: String
  val processors: Map[String, Processor]
  
  val bus_name = "com.nublic.filewatcher"
  val object_path = "/com/nublic/filewatcher/" + app_name
  val derby_db = "jdbc:derby:/var/nublic/cache/" + app_name + ".filewatcher;create=true"
  var derby_connection: Connection = null

  def act() = {
    // Create D-Bus connection
    val conn = DBusConnection.getConnection(DBusConnection.SYSTEM);
    val watcher = conn.getRemoteObject(bus_name, object_path)
    conn.addSigHandler(classOf[FileWatcher.file_changed], watcher, new DBusHandler(this))
    // Start processors
    for(processor <- processors.values) {
      processor.start()
    }
    // Manage initial steps in Derby database
    loadDerby()
    executeNotFinishedActions()
    // Start actor loop
    loop {
      react {
        case c : FileChange => {
          // Save in Derby database
          var insert_text = "INSERT INTO files VALUES(DEFAULT, ?, ?, ?, ?"
          for (field <- processors.keys)
            insert_text += ", FALSE"
          insert_text += ")"
          val insert_statement = derby_connection.prepareStatement(insert_text)
          insert_statement.setString(1, c.getFileName)
          insert_statement.setBoolean(3, c.isDirectory)
          insert_statement.setString(4, c.getType)
          c match {
            case m @ Moved(from, to, isdir) => insert_statement.setString(2, from)
            case _ => insert_statement.setString(2, "")
          }
          insert_statement.executeUpdate()
          // Find last value
          val max_statement = derby_connection.prepareStatement("SELECT MAX(id) FROM files")
          val max_results = max_statement.executeQuery()
          max_results.next()
          val id = max_results.getInt(1)
          // Send to processors
          for(processor <- processors.values) {
            processor ! ForwardFileChange(id, c)
          }
        }
        case BackFileChange(name, id, _) => {
          // Remove that processor from Derby database
          val update_statement = derby_connection.prepareStatement("UPDATE files SET " + name + " = TRUE WHERE id = ?")
          update_statement.setLong(1, id)
          update_statement.executeUpdate()
          // Remove all statements with all finished
          var delete_statement = "DELETE FROM files WHERE "
          var first = true
          for(field <- processors.keys) {
            if (!first) {
              delete_statement += ", "
            } else {
              first = false
            }
            delete_statement += field + " = TRUE"
          }
          derby_connection.prepareStatement(delete_statement).executeUpdate()
        }
      }
    }
  }
  
  def loadDerby() = {
    // Load Derby driver
    Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
    // Create or load database
    derby_connection = DriverManager.getConnection(derby_db)
    // Create table if it does not exist
    // To do that, try to execute the query
    try {
      var query = derby_connection.prepareStatement("SELECT * FROM files");
      query.executeQuery();
    } catch {
      case e: SQLException => {
        var table_creation_statement = 
          "CREATE TABLE files(id BIGINT GENERATED ALWAYS AS IDENTITY, pathname VARCHAR(5000) NOT NULL, " + 
          "src_pathname VARCHAR(5000), isdir BOOLEAN, type VARCHAR(20) NOT NULL"
        for (field <- processors.keys)
          table_creation_statement += ", " + field + " BOOLEAN DEFAULT FALSE"
        table_creation_statement += ")"
        val table_creation = derby_connection.prepareStatement(table_creation_statement)
        table_creation.execute()
      }
    }
  }
  
  def executeNotFinishedActions() = {
    // Create and execute statement
    var query_statement = "SELECT id, pathname, src_pathname, isdir, type"
    for (field <- processors.keys)
      query_statement += ", " + field
    query_statement += " FROM files"
    val query = derby_connection.prepareStatement(query_statement)
    val results = query.executeQuery()
    // Loop in the results
    while(results.next()) {
      // Check every processor
      for ((field, processor) <- processors) {
        val is_finished = results.getBoolean(field)
        if (!is_finished) {
          // Get file change information
          val id = results.getLong("id")
          val pathname = results.getString("pathname")
          val src_pathname = results.getString("src_pathname")
          val isdir = results.getBoolean("isdir")
          val ty = results.getString("type")
          // Send message to processor
          val file_change = FileChange.parse(ty, pathname, src_pathname, isdir)
          processor ! ForwardFileChange(id, file_change)
        }
      }
    }
  }

  class DBusHandler(actor: FileWatcherActor) extends DBusSigHandler[FileWatcher.file_changed] {
    var lock : AnyRef = new Object()
    
    def handle(change: FileWatcher.file_changed): Unit = {
      lock.synchronized {
        actor ! change.getChange
      }
    }
  }
}