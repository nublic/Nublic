package com.nublic.app.browser.server.filewatcher

import java.io.File

abstract class DocumentWorker {
  def supportedMimeTypes: List[String]
  def supportedViews: List[String]
  def process(file: String, folder: File): Unit
  def getMimeTypeForView(viewName: String): String
  def hasView(viewName: String, file: String): Boolean
  def getView(viewName: String, file: String): File
}