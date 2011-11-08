package com.nublic.app.browser.server.filewatcher

import com.nublic.filewatcher.scala._
import java.io.File
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import com.nublic.app.browser.server.filewatcher.workers.OfficeWorker
import com.nublic.app.browser.server.Solr
import com.nublic.app.browser.server.filewatcher.workers.Workers

class DocumentProcessor(watcher: FileWatcherActor) extends Processor("document", watcher) {
  
  def process(c: FileChange) = c match {
    // case Created(filename, false)  => process_updated_file(filename)
    case Modified(filename, _, false) => process_updated_file(filename)
    case Moved(from, to, _, false)    => process_moved_file(from, to)
    case Moved(from, to, _, true)     => process_moved_folder(from, to)
    case Deleted(filename, _, false)  => process_deleted_file(filename)
    case _                            => { /* Nothing */ }
  }
  
  def process_updated_file(filename: String): Unit = {
    val cache_folder = FileFolder.getFolder(filename)
    // Create folder if it does not exist
    if (!cache_folder.exists()) {
      cache_folder.mkdirs()
    }
    // Read MIME type from Solr database
    Solr.getMimeType(filename) match {
      case None       => { /* Do nothing */ }
      case Some(mime) => {
        // Send to worker
        Workers.byMimeType.get(mime) match {
          case None         => { /* Do nothing */ }
          case Some(worker) => worker.process(filename, cache_folder)
        }
      }
    }
  }
  
  def process_moved_file(from: String, to: String): Unit = {
    val from_cache_folder = FileFolder.getFolder(from)
    val to_cache_folder = FileFolder.getFolder(to)
    if (from_cache_folder.exists()) {
      from_cache_folder.renameTo(to_cache_folder)
    }
  }
  
  def process_moved_folder(from: String, to: String): Unit = {
    // Recursively visit all elements in the new folder
    val folder = new File(to)
    for (file <- folder.listFiles()) {
      // Get new filename
      val path = file.getPath()
      val old_path = path.replaceFirst(to, from)
      // Recursively visit all folders
      if (file.isDirectory()) {
        process_moved_folder(old_path, path)
      }
      process_moved_file(from, to)
    }
  }
  
  def process_deleted_file(filename: String): Unit = {
    val cache_folder = FileFolder.getFolder(filename)
    FileUtils.deleteDirectory(cache_folder)
  }
}