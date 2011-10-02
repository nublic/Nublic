package com.nublic.app.browser.server.filewatcher

import java.io.File

abstract class DocumentWorker {
  def supportedMimeTypes: List[String]
  def process(file: String, folder: File): Unit
}