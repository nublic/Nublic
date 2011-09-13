package com.nublic.app.browser.server.filewatcher

import com.nublic.filewatcher.scala._

class ThumbnailProcessor(watcher: FileWatcherActor) extends Processor("thumbnail", watcher) {

  def process(c: FileChange) = c match {
    case Created(filename, false)  => process_updated_file(filename)
    case Modified(filename, false) => process_updated_file(filename)
    case Moved(from, to, false)    => process_moved_file(from, to)
    case Deleted(filename, false)  => process_deleted_file(filename)
    case _                         => { /* Nothing */ }
  }
  
  def process_updated_file(filename: String) = {
    Console.print(filename + " updated")
  }
  
  def process_moved_file(from: String, to: String) = {
    Console.print(from + " moved to " + to)
  }
  
  def process_deleted_file(filename: String) = {
    Console.print(filename + " deleted")
  }
}