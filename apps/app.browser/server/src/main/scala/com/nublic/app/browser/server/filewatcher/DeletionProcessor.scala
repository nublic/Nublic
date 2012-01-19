package com.nublic.app.browser.server.filewatcher

import com.nublic.filewatcher.scala._

class DeletionProcessor(watcher: FileWatcherActor) extends Processor("deletion", watcher) {

  def TIME_TO_WAIT = 1 /* min */ * 60 /* sec */ * 1000 /* ms */
  
  var deleted_files_list = List[Tuple2[String, Long]]()
  
  def getDeletedFilesSince(path : String, moment : Long) : List[String] = 
    deleted_files_list.filter(f => f._1.startsWith(path + "/") && f._2 > moment).map(_._1)
  
  def process(c: FileChange): Unit = c match {
    case Deleted(filename, _, _)  => process_deletion(filename)
    case _                        => { /* Do nothing */ }
  }

  def process_deletion(filename: String) = {
    val now = System.currentTimeMillis()
    deleted_files_list ::= Tuple2(filename, now)
    delete_old_deleted_files()
  }
  
  def delete_old_deleted_files() = {
    val now = System.currentTimeMillis()
    val threshold = now - TIME_TO_WAIT
    deleted_files_list = deleted_files_list.filter(f => f._2 < threshold)
  }
}