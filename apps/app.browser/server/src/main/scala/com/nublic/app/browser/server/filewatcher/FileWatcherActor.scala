package com.nublic.app.browser.server.filewatcher

import scala.actors.Actor
import scala.actors.Actor._
import org.freedesktop.dbus.DBusConnection
import org.freedesktop.dbus.DBusSigHandler

class FileWatcherActor extends Actor {
  val bus_name = "com.nublic.filewatcher"
  val object_path = "/com/nublic/filewatcher/Browser"

  def act() = {
    val conn = DBusConnection.getConnection(DBusConnection.SYSTEM);
    val watcher = conn.getRemoteObject(bus_name, object_path)
    conn.addSigHandler(classOf[FileWatcher.file_changed], watcher, new DBusHandler(this))
    loop {
      react {
        case Created(fname, _) => Console.print("created " + fname)
        case c : FileChange => Console.print("other thing " + c.getFileName)
      }
    }
  }

  class DBusHandler(actor: FileWatcherActor) extends DBusSigHandler[FileWatcher.file_changed] {
    def handle(change: FileWatcher.file_changed): Unit = {
      actor ! change.getChange
    }
  }
}