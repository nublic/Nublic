package com.nublic.app.browser.server.filewatcher

import com.nublic.filewatcher.scala._
import java.io.File
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import com.nublic.app.browser.server.filewatcher.workers.OfficeWorker

class DocumentProcessor(watcher: FileWatcherActor) extends Processor("document", watcher) {

  val ROOT_FOLDER = "/var/nublic/cache/browser"
  
  // List here all available workers in the system
  val workers = List(new OfficeWorker())
  // A map of all workers with their mime types
  var workers_map: Map[String, DocumentWorker] = Map()
  for(worker <- workers) {
    for(mimeType <- worker.supportedMimeTypes) {
      workers_map += (mimeType -> worker)
    }
  }
  
  def process(c: FileChange) = c match {
    // case Created(filename, false)  => process_updated_file(filename)
    case Modified(filename, false) => process_updated_file(filename)
    case Moved(from, to, false)    => process_moved_file(from, to)
    case Moved(from, to, true)     => process_moved_folder(from, to)
    case Deleted(filename, false)  => process_deleted_file(filename)
    case _                         => { /* Nothing */ }
  }
  
  def process_updated_file(filename: String): Unit = {
    val cache_folder = get_folder_for(filename)
    // Create folder if it does not exist
    if (!cache_folder.exists()) {
      cache_folder.mkdirs()
    }
    // Read MIME type from Solr database
    
    // Special case for OOXML formats (MIME type is always "application/zip")
    
    // Send to worker
  }
  
  def process_moved_file(from: String, to: String): Unit = {
    val from_cache_folder = get_folder_for(from)
    val to_cache_folder = get_folder_for(to)
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
    val cache_folder = get_folder_for(filename)
    FileUtils.deleteDirectory(cache_folder)
  }
  
  def get_folder_name(filepath: String): String = DigestUtils.shaHex(filepath)
  def get_folder_for(filepath: String): File = new File(ROOT_FOLDER, get_folder_name(filepath))
  
}