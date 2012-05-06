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
import com.drew.imaging.ImageMetadataReader
import com.drew.metadata.exif.ExifDirectory
import java.util.Date

class PhotoProcessor(watcher: FileWatcherActor) extends Processor("photos", watcher, true) {
  
  val GLOBAL_LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
  
  def process(c: FileChange) = {
    GLOBAL_LOGGER.severe("Filewatcher: Processing " + c.toString())
    c match {
      case Created(filename, context, false)  => process_updated_file(filename, context)
      case Modified(filename, context, false) => process_updated_file(filename, context)
      case Moved(from, to, context, false)    => process_moved_file(from, to, context)
      case Moved(from, to, context, true)     => process_moved_folder(from, to, context)
      case Deleted(filename, _, false)        => process_deleted_file(filename)
      case _                                  => { /* Nothing */ }
    }
  }
  
  def now() = new Date().getTime()
  
  def process_updated_file(filename: String, context: String): Unit = {    
    if (Solr.getMimeType(filename).startsWith("image/")) {
      inTransaction {
        val img_file = new File(filename)
        val metadata = ImageMetadataReader.readMetadata(img_file)
        val exif_dir = metadata.getDirectory(classOf[ExifDirectory])
        val original_date = 
          if (exif_dir.containsTag(ExifDirectory.TAG_DATETIME_ORIGINAL)) {
            exif_dir.getDate(ExifDirectory.TAG_DATETIME_ORIGINAL).getTime()
          } else {
            img_file.lastModified()
          }
        Database.photoByFilename(filename) match {
          case Some(photo) => {
            photo.date = original_date
            photo.lastModified = now()
            Database.photos.update(photo)
          }
          case None       => { 
            // Add to database
            val photo = new Photo()
            photo.file = filename
            photo.date = original_date
            photo.lastModified = now()
            photo.title = FilenameUtils.getBaseName(filename)
            Database.photos.insert(photo)
            // Create initial album
            // Get album name
            val ctx = new File(context)
            val parent = img_file.getParentFile()
            val album_name = 
              if (parent.equals(ctx)) {
                None
              } else if (parent.getParentFile().equals(ctx)) {
                Some(parent.getName())
              } else {
                Some(parent.getParentFile().getName() + "/" + parent.getName())
              }
            if (album_name.isDefined) {
              val album = Database.getOrCreateAlbum(album_name.get)
              Database.photoAlbums.insert(new PhotoAlbum(photo.id, album.id))
            }
          }
        }
      }
    }
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

  def process_moved_folder(from: String, to: String, context: String): Unit = inTransaction {
    // Let's move all the contents
    val folder = new File(to)
    for (file <- folder.listFiles()) {
      // Get new filename
      val path = file.getPath()
      val old_path = path.replaceFirst(to, from)
      if (file.isDirectory()) {
        process_moved_folder(old_path, path, context)
      } else {
        // Try to update song
        Database.photoByFilename(old_path) match {
          case None       => { /* Do nothing */ }
          case Some(photo) => {
            photo.file = path
            Database.photos.update(photo)
          }
        }
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
