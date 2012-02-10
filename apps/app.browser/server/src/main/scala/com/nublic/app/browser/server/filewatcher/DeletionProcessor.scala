package com.nublic.app.browser.server.filewatcher

import com.nublic.filewatcher.scala._
import scala.collection.JavaConversions._
import java.util.Date
import java.util.ArrayList

class DeletionProcessor(watcher: FileWatcherActor) extends Processor("deletion", watcher, false) {

  def TIME_TO_WAIT = 5 /* min */ * 60 /* sec */ * 1000 /* ms */
  
  var new_files_list     = new ArrayList[Tuple2[String, Long]]()
  var deleted_files_list = new ArrayList[Tuple2[String, Long]]()
  
  private def get_files_since(lst: ArrayList[Tuple2[String, Long]], path: String, moment: Long) : List[String] =
    lst.filter(f => is_in_folder(f._1, path) && f._2 > moment).map(_._1).toList
  
  private def is_in_folder(filename: String, folder: String) = {
    if (filename.startsWith(folder + "/")) {
      val just_file = filename.substring(folder.length() + 1)
      !just_file.contains('/')
    } else {
      false;
    }
  }
  
  def getNewFilesSince(path: String, moment: Long) : List[String] =
    get_files_since(new_files_list, path, moment)
    
  def getDeletedFilesSince(path: String, moment: Long) : List[String] = 
    get_files_since(deleted_files_list, path, moment)
  
  def process(c: FileChange): Unit =  {
    val now = (new Date()).getTime()
    c match {
      case Created(filename, _, _)  => process_new(filename, now)
      case Modified(filename, _, _) => process_new(filename, now)
      case AttribsChanged(fn, _, _) => process_new(fn, now)
      case Moved(from, to, _, _)    => {
        process_deleted(from, now)
        process_new(to, now)
      }
      case Deleted(filename, _, _)  => process_deleted(filename, now)
      case _                        => { /* Do nothing */ }
    }
  }
  
  def process_new(filename: String, now: Long) = process_in(new_files_list, deleted_files_list, filename, now)
  
  def process_deleted(filename: String, now: Long) = process_in(deleted_files_list, new_files_list, filename, now)

  def process_in(lst: ArrayList[Tuple2[String, Long]], other: ArrayList[Tuple2[String, Long]], filename: String, now: Long) = {
    val t = Tuple2(filename, now)
    // Delete previous events of the same path
    val to_remove = lst.filter(f => f._1 == filename).toList
    lst.removeAll(to_remove)
    // Add the current event
    lst.add(t)
    // Remove from the other list (if it was created and later removed, we only tell about removing)
    val to_remove_from_other = other.filter(f => f._1 == filename).toList
    other.remove(t)
    // Remove very old notifications
    delete_old(lst, now)
  }
  
  def delete_old(lst: ArrayList[Tuple2[String, Long]], now: Long) = {
    val threshold = now - TIME_TO_WAIT
    val to_remove = lst.filter(f => f._2 < threshold).toList
    lst.removeAll(to_remove)
  }
}