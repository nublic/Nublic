package com.nublic.app.browser.server.filewatcher.workers

import java.io.File
import scala.collection.immutable.List
import org.im4java.core.ConvertCmd
import org.im4java.core.IMOperation
import com.nublic.app.browser.server.filewatcher.DocumentWorker
import com.nublic.app.browser.server.filewatcher.FileFolder

object PdfWorker extends DocumentWorker {

  def supportedMimeTypes(): List[String] = List(
      "application/pdf", "application/x-pdf", 
      "application/acrobat", "applications/vnd.pdf", 
      "text/pdf", "text/x-pdf"  
    )

  def supportedViews(): List[String] = List("pdf")

  def process(file: String, folder: File): Unit = {
    // Now create the thumbnail
    val thumb_file = new File(folder, FileFolder.THUMBNAIL_FILENAME)
    val magick = new ConvertCmd()
    val op = new IMOperation() 
    op.addImage(file + "[0]")
    op.resize(FileFolder.THUMBNAIL_SIZE)
    op.addImage(thumb_file.getAbsolutePath())
    magick.run(op) 
  }

  def getMimeTypeForView(viewName: String): String = viewName match {
    case "pdf" => "application/pdf"
    case _     => null
  }

  def hasView(viewName: String, file: String, mime: String): Boolean = 
    viewName == "pdf" && supportedMimeTypes.contains(mime)

  def getView(viewName: String, file: String): File = new File(file)

}