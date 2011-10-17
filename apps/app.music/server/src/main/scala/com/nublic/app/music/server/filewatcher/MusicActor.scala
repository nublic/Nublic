package com.nublic.app.music.server.filewatcher

import com.nublic.filewatcher.scala.FileWatcherActor

class MusicActor extends FileWatcherActor("Music") {
  val processors = Map("music" -> new MusicProcessor(this)) 
}