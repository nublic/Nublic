package com.nublic.app.browser.server.filewatcher

import com.nublic.filewatcher.scala.FileWatcherActor

class FileActor extends FileWatcherActor("Browser") {
  val processors = Map("document" -> new DocumentProcessor(this)) 
}