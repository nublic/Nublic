package com.nublic.app.photos.server.filewatcher

import java.io.File
import org.apache.commons.io.FilenameUtils
import org.squeryl.PrimitiveTypeMode._
import com.nublic.filewatcher.scala._
import com.nublic.app.photos.server.Solr
import com.nublic.app.photos.server.model._
import java.io.FileWriter
import java.io.PrintWriter
import java.util.logging.Logger

class PhotoProcessor(watcher: FileWatcherActor) extends Processor("photos", watcher, true) {
  
  val GLOBAL_LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
  
  def process(c: FileChange) = {
    GLOBAL_LOGGER.severe("Filewatcher: Processing " + c.toString())
    c match {
      // case Created(filename, false)  => process_updated_file(filename)
      case Modified(filename, context, false) => process_updated_file(filename, context)
      case Moved(from, to, context, _)        => process_moved_file(from, to, context)
      case Deleted(filename, _, false)        => process_deleted_file(filename)
      case _                                  => { /* Nothing */ }
    }
  }
  
  def process_updated_file(filename: String, context: String): Unit = {
    
  }
  
  def process_moved_file(from: String, to: String, context: String): Unit = inTransaction {
    Database.photoByFilename(from) match {
      case None       => process_updated_file(to, context)
      case Some(photo) => {
        photo.file = to
        Database.photos.update(photo)
      }
    }
  }
  
  def process_deleted_file(filename: String): Unit = inTransaction {
    Database.photoByFilename(filename).map(photo => {
      Database.photoAlbums.deleteWhere(pa => pa.photoId === photo.id)
      Database.photos.deleteWhere(p => p.id === photo.id)
    })
  }

}
