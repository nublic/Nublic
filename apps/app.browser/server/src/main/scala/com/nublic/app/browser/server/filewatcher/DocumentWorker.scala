package com.nublic.app.browser.server.filewatcher

import java.io.File
import java.io.FileOutputStream
import java.io.BufferedInputStream
import scala.actors.Actor
import scala.actors.Actor._

abstract class DocumentWorker {
  def supportedMimeTypes: List[String]
  def supportedViews: List[String]
  def process(file: String, folder: File): Unit
  def getMimeTypeForView(viewName: String): String
  def hasView(viewName: String, file: String): Boolean
  def getView(viewName: String, file: String): File
  
  /**
   * Utility function or processes that require the
   * entire output to be read to finish.
   */
  def flushActor(process: Process): Actor = actor {
      val nullStream = new FileOutputStream("/dev/null")
      // val nullStream = System.out
      // Read file
      val buffer = new Array[Byte](1024)
      val buffered_in_stream = new BufferedInputStream(process.getInputStream())
      var bytes_read = buffered_in_stream.read(buffer, 0, buffer.length)
      while(bytes_read != -1) {
        nullStream.write(buffer, 0, bytes_read)
        bytes_read = buffered_in_stream.read(buffer, 0, buffer.length)
      }
      // Close stream
      nullStream.flush()
      nullStream.close()
    }
}