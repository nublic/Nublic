package com.nublic.app.music.server.filewatcher

object FilenameExtractor {
  
  def from(filename: String, context: String) = {
    val fname = filename.replaceFirst(context, "")
  }
}