package com.nublic.filewatcher.scala

import scala.actors.Actor
import scala.actors.Actor._
import org.freedesktop.dbus._
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import org.squeryl.adapters.DerbyAdapter
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.Session
import org.squeryl.SessionFactory
import FileWatcherDatabase._
import java.util.HashMap

object FileWatcherActor extends DBusSigHandler[FileWatcher.file_changed] {
  var lock: AnyRef = new Object()  
  var actors_map: Map[String, FileWatcherActor] = Map()
  val bus_name = "com.nublic.filewatcher"
  val connection = DBusConnection.getConnection(DBusConnection.SYSTEM)
  connection.addSigHandler(classOf[FileWatcher.file_changed], this)
  
  def handle(change: FileWatcher.file_changed): Unit = {
    // Console.print("Something arrived on " + change.getObjectPath)
    lock.synchronized {
      actors_map.get(change.getObjectPath) match {
        case Some(actor) => actor ! change.getChange
        case None        => { /* Nothing */ }
      }
    }
  }
  
  def addActor(object_path: String, actor: FileWatcherActor) = {
    lock.synchronized {
      actors_map += (object_path -> actor)
    }
  }
}

abstract class FileWatcherActor(val app_name: String) extends Actor {
  val processors: Map[String, Processor]
  
  val object_path = "/com/nublic/filewatcher/" + app_name
  val derby_db_file = "/var/nublic/cache/" + app_name + ".filewatcher"
  val derby_db = "jdbc:derby:" + derby_db_file + ";create=true"
  var processor_numbers = Map[String, Integer]()
  var all_processors_number: Integer = -1

  def act() = {
    // Start processors
    computeProcessorNumbers()
    for(processor <- processors.values) {
      processor.start()
    }
    // Manage initial steps in Derby database
    loadSqueryl()
    executeNotFinishedActions()
    // Create D-Bus connection
    FileWatcherActor.addActor(object_path, this)
    // Start actor loop
    loop {
      react {
        case c : FileChange => {
          // Save in Derby database
          val dbChange = c match {
            case Moved(from, to, context, isdir) => new FileChangeInDatabase(c.getType, to, from, context, isdir)
            case _: FileChange => new FileChangeInDatabase(c.getType, c.getFileName, c.getContext, c.isDirectory)
          }
          transaction {
            FileWatcherDatabase.files.insert(dbChange)
          }
          // Send to processors
          val objectId = dbChange.id
          for(processor <- processors.values) {
            processor ! ForwardFileChange(objectId, c)
          }
        }
        case BackFileChange(name, id, _) => {
          // Get the database object
          transaction {
            FileWatcherDatabase.files.lookup(id) match {
              case None => { /* This should not happen */ }
              case Some(dbChange) => {
                // Set the processor
                setProcessorFinished(dbChange, name)
                FileWatcherDatabase.files.update(dbChange)
                // Delete is everything finished
                if (hasAllProcessorsFinished(dbChange)) {
                  FileWatcherDatabase.files.delete(id)
                }
              }
            }
          }
        }
      }
    }
  }
  
  def loadSqueryl(): Unit = {
    // Load Derby driver and create connection
    Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
    SessionFactory.concreteFactory = Some(() => Session.create(
      java.sql.DriverManager.getConnection(derby_db),
      new DerbyAdapter)
    )
    // Create tables if they do not exist
    transaction {
      try {
        FileWatcherDatabase.create
      } catch {
        case e: Throwable => { 
          Console.println(e.getMessage())
        }
      }
    }
  }
  
  def executeNotFinishedActions() = {
    // Check every row
    transaction {
      for(dbChange <- FileWatcherDatabase.files) {
        for((field, processor) <- processors) {
          if(!hasProcessorFinished(dbChange, field)) {
            processor ! ForwardFileChange(dbChange.id, dbChange.toFileChange)
          }
        }
      }
    }
  }
  
  def computeProcessorNumbers() = {
    // Create new map
    processor_numbers = Map()
    // Generate numbers
    var i = 1
    all_processors_number = 0
    for(processor_name <- processors.keys) {
      processor_numbers += processor_name -> i
      all_processors_number += i
      i *= 2
    }
  }
  
  def hasProcessorFinished(dbChange: FileChangeInDatabase, processor_name: String): Boolean = {
    processor_numbers.get(processor_name) match {
      case None         => false
      case Some(number) => (dbChange.processed & number) > 0
    }
  }
  
  def hasAllProcessorsFinished(dbChange: FileChangeInDatabase): Boolean = dbChange.processed == all_processors_number
  
  def setProcessorFinished(dbChange: FileChangeInDatabase, processor_name: String) = {
     processor_numbers.get(processor_name) match {
      case None         => ()
      case Some(number) => dbChange.processed |= number
    }
  }
}
