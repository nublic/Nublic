package com.nublic.app.browser.server.filewatcher

import com.nublic.filewatcher.scala.FileWatcherActor

class FileActor extends FileWatcherActor("Browser") {
  
  val deletion_processor = new DeletionProcessor(this)
  
  val processors = Map("document" -> new DocumentProcessor(this),
                       "deletion" -> deletion_processor)
  
  def getDeletionProcessor = deletion_processor
}