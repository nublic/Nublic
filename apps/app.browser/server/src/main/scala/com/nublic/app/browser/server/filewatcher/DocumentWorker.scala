package com.nublic.app.browser.server.filewatcher

import java.io.File

abstract class DocumentWorker {
  def supportedMimeTypes: List[String]
  def supportedViews: List[String]
  def process(file: String, folder: File): Unit
  def hasView(file: String): Boolean
  def getView(file: String): File
}