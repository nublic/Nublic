package com.nublic.app.browser.server.filewatcher

abstract class DocumentWorker {
  def supportedMimeTypes: List[String]
  def process(file: String, folder: String): Unit
}