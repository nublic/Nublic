package com.nublic.app.browser.server.filewatcher.workers

import com.nublic.app.browser.server.filewatcher.DocumentWorker
import scala.collection.immutable.List
import scala.actors.Actor._
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import org.apache.commons.io.FilenameUtils
import com.nublic.app.browser.server.filewatcher.FileFolder
import org.im4java.core.ConvertCmd
import org.im4java.core.IMOperation

object DviPsWorker extends DocumentWorker {
  
  def supportedMimeTypes: List[String] = List(
      "application/postscript", "application/ps",
      "application/x-postscript", "application/x-ps",
      "application/dvi", "application/x-dvi")

  def supportedViews: List[String] = List("pdf")
  
  val PDF_FILENAME = "doc.pdf"
  
  def process(file: String, folder: File): Unit = {
    val pdfFile = new File(folder, PDF_FILENAME)
    
    val command = if (file.endsWith("dvi")) { "dvipdf" } else { "ps2pdf" }
    val cmd = new ProcessBuilder(command, file, pdfFile.getAbsolutePath())
    cmd.redirectErrorStream(true)
    val process = cmd.start()
    flushActor(process).start
    process.waitFor()
    
    // Now create the thumbnail
    val thumb_file = new File(folder, FileFolder.THUMBNAIL_FILENAME)
    val magick = new ConvertCmd()
    val op = new IMOperation() 
    op.addImage(pdfFile.getAbsolutePath() + "[0]")
    op.resize(FileFolder.THUMBNAIL_SIZE, FileFolder.THUMBNAIL_SIZE)
    op.interlace("plane")
    op.quality(5); // Huffmann compresion + adaptive filter
    op.addImage(thumb_file.getAbsolutePath())
    magick.run(op)
  }
  
  def getMimeTypeForView(viewName: String): String = viewName match {
    case "pdf" => "application/pdf"
    case _     => null
  }
  
  def hasView(viewName: String, file: String, mime: String): Boolean = {
    if (!supportedMimeTypes.contains(mime)) {
      false
    } else {
      val folder = FileFolder.getFolder(file)
      viewName match {
        case "pdf" => {
          val pdf_file = new File(folder, PDF_FILENAME)
          pdf_file.exists()
        }
        case _ => false
      }
    }
  }
  
  def getView(viewName: String, file: String): File = {
    val folder = FileFolder.getFolder(file)
    viewName match {
      case "pdf" => {
        val pdf_file = new File(folder, PDF_FILENAME)
        if (pdf_file.exists()) pdf_file else null
      }
      case _ => null
    }
  }
}