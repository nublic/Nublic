package com.nublic.app.music.server.filewatcher

import com.nublic.filewatcher.scala._
import java.io.File

class MusicProcessor(watcher: FileWatcherActor) extends Processor("music", watcher) {
  
  def process(c: FileChange) = c match {
    // case Created(filename, false)  => process_updated_file(filename)
    case Modified(filename, false) => process_updated_file(filename)
    case Deleted(filename, false)  => process_deleted_file(filename)
    case _                         => { /* Nothing */ }
  }
  
  def process_updated_file(filename: String): Unit = {
    
  }
  
  def process_deleted_file(filename: String): Unit = {
    
  }
}