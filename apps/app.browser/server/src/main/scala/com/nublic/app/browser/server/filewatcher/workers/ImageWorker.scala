package com.nublic.app.browser.server.filewatcher.workers

import java.io.File
import scala.collection.immutable.List
import org.im4java.core.ConvertCmd
import org.im4java.core.IMOperation
import com.nublic.app.browser.server.filewatcher.DocumentWorker
import com.nublic.app.browser.server.filewatcher.FileFolder

object ImageWorker extends DocumentWorker {

  def supportedMimeTypes(): List[String] = List(
      "image/bmp", "image/gif", "image/png",
      "image/jpg", "image/jpeg", "image/pjpeg",
      "image/svg", "image/x-icon", "image/x-pict",
      "image/x-pcx", "image/pict", "image/x-portable-bitmap",
      "image/tiff", "image/x-tiff", "image/x-xbitmap",
      "image/x-xbm", "image/xbm", "application/wmf", 
      "application/x-wmf", "image/wmf", "image/x-wmf" 
    )

  def supportedViews(): List[String] = List("image")

  def process(file: String, folder: File): Unit = {
    // Now create the thumbnail
    val thumb_file = new File(folder, FileFolder.THUMBNAIL_FILENAME)
    val magick = new ConvertCmd()
    val op = new IMOperation() 
    op.addImage(file)
    op.resize(FileFolder.THUMBNAIL_SIZE)
    op.addImage(thumb_file.getAbsolutePath())
    magick.run(op)
  }

  def getMimeTypeForView(viewName: String): String = viewName match {
    case "image" => "image"
    case _       => null
  }

  def hasView(viewName: String, file: String, mime: String): Boolean = 
    viewName == "image" && supportedMimeTypes.contains(mime)

  def getView(viewName: String, file: String): File = new File(file)

}