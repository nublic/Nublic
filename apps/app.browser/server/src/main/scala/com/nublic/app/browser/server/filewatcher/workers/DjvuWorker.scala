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

object DjvuWorker extends DocumentWorker {
  
  def supportedMimeTypes: List[String] = List("image/vnd.djvu")

  def supportedViews: List[String] = List("pdf")
      
  val TIFF_FILENAME = "doc.tiff"
  val PDF_FILENAME = "doc.pdf"
  
  def process(file: String, folder: File): Unit = {
    val tiffFile = new File(folder, TIFF_FILENAME)
    val pdfFile = new File(folder, PDF_FILENAME)
    
    // Run `ddjvu -format=tiff {djvu} {tiff}`
    val cmd = new ProcessBuilder("ddjvu", "-format=tiff", file, tiffFile.getAbsolutePath())
    cmd.redirectErrorStream(true)
    val process = cmd.start()
    flushActor(process).start
    process.waitFor()
    
    // Run `tiff2pdf -j -o {pdf} {tiff}`
    val cmd2 = new ProcessBuilder("tiff2pdf", "-j", "-o",
        pdfFile.getAbsolutePath(), tiffFile.getAbsolutePath())
    cmd2.redirectErrorStream(true)
    val process2 = cmd2.start()
    flushActor(process2).start
    process2.waitFor()
    
    // Remove temporary tiff
    tiffFile.delete()
    
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