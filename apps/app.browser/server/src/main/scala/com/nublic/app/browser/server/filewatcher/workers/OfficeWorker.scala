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

object OfficeWorker extends DocumentWorker {

  /* Obtained looking at:
   * - List of files supported by unoconv: `unoconv --show`
   * - Information about file extensions: http://filext.com/
   */
  
  def generalOfficeMimeTypes: List[String] = List(
      // Microsoft Office
      "application/vnd.ms-office",
      // General for old StarOffice
      "application/x-staroffice", "application/soffice", "application/x-soffice"
      )
  
  def wordProcessorMimeTypes: List[String] = List(
      // Word .doc
      "application/msword", "application/doc",
      "application/vnd.msword", "application/vnd.ms-word", 
      "application/winword", "application/word", 
      "application/x-msw6", "application/x-msword",
      // Word .docx
      "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
      // Writer .odt
      "application/vnd.oasis.opendocument.text",
      "application/x-vnd.oasis.opendocument.text",
      // Writer .sxw
      "application/vnd.sun.xml.writer",
      // Writer .sdw
      "application/x-swriter", "application/vnd.stardivision.writer",
      // Rich Text Format .rtf
      "application/rtf", "application/x-rtf", "text/rtf", "text/richtext"
      )
  
  def spreadsheetMimeTypes: List[String] = List(
      // Excel .xls
      "application/vnd.ms-excel", "application/msexcel", 
      "application/x-msexcel", "application/x-ms-excel", 
      "application/vnd.ms-excel", "application/x-excel", 
      "application/x-dos_ms_excel", "application/xls",
      // Excel .xlsx
      "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
      // Calc .ods
      "application/vnd.oasis.opendocument.spreadsheet",
      "application/x-vnd.oasis.opendocument.spreadsheet",
      // Calc .sxc
      "application/vnd.sun.xml.calc",
      // Calc .sdc
      "application/vnd.stardivision.calc"
      )
      
  def presentationMimeTypes: List[String] = List(
      // PowerPoint .ppt
      "application/vnd.ms-powerpoint", "application/mspowerpoint",
      "application/ms-powerpoint", "application/mspowerpnt", 
      "application/vnd-mspowerpoint", "application/powerpoint", 
      "application/x-powerpoint",
      // PowerPoint .pptx
      "application/vnd.openxmlformats-officedocument.presentationml.presentation",
      // Impress .odp
      "application/vnd.oasis.opendocument.presentation",
      "application/x-vnd.oasis.opendocument.presentation",
      // Impress .sxi
      "application/vnd.sun.xml.impress",
      // Impress .sdd
      "application/vnd.stardivision.impress"
      )
      
  def drawingMimeTypes: List[String] = List(
      // Draw .odg
      "application/vnd.oasis.opendocument.graphics",
      "application/x-vnd.oasis.opendocument.graphics",
      // Draw .sxd
      "application/vnd.sun.xml.draw",
      // Draw .sda
      "application/x-sdraw", "application/x-sda", "application/vnd.stardivision.draw"
      )
  
  def supportedMimeTypes: List[String] = generalOfficeMimeTypes ++ wordProcessorMimeTypes ++
    spreadsheetMimeTypes ++ presentationMimeTypes ++ drawingMimeTypes

  def supportedViews: List[String] = List("pdf")
      
  val PDF_FILENAME = "doc.pdf"
  val LOG_FILENAME = "unoconv.log"
  
  def process(file: String, folder: File): Unit = {
    // Run `unoconv --stdout -f pdf ${file} > ${folder}/doc.pdf`
    val cmd = new ProcessBuilder("unoconv", "--stdout", "-f", "pdf", file)
    var env = cmd.environment()
    env.put("HOME", "/tmp")
    val process = cmd.start()
    val pdfFile = new File(folder, PDF_FILENAME)
    val logFile = new File(folder, LOG_FILENAME)
    actor {
      val pdfStream = new FileOutputStream(pdfFile)
      // Read file
      val buffer = new Array[Byte](1024)
      val buffered_in_stream = new BufferedInputStream(process.getInputStream())
      var bytes_read = buffered_in_stream.read(buffer, 0, buffer.length)
      while(bytes_read != -1) {
        pdfStream.write(buffer, 0, bytes_read)
        bytes_read = buffered_in_stream.read(buffer, 0, buffer.length)
      }
      // Close stream
      pdfStream.flush()
      pdfStream.close()
    }.start
    actor {
      val logStream = new FileOutputStream(logFile)
      // Read file
      val buffer = new Array[Byte](1024)
      val buffered_err_stream = new BufferedInputStream(process.getErrorStream())
      var bytes_read = buffered_err_stream.read(buffer, 0, buffer.length)
      while(bytes_read != -1) {
        logStream.write(buffer, 0, bytes_read)
        bytes_read = buffered_err_stream.read(buffer, 0, buffer.length)
      }
      // Close stream
      logStream.flush()
      logStream.close()
    }.start
    // Create the "pipe" to get the file
    process.waitFor()
    
    // Now create the thumbnail
    val thumb_file = new File(folder, FileFolder.THUMBNAIL_FILENAME)
    val magick = new ConvertCmd()
    val op = new IMOperation() 
    op.addImage(pdfFile.getAbsolutePath() + "[0]")
    op.resize(FileFolder.THUMBNAIL_SIZE, FileFolder.THUMBNAIL_SIZE)
    op.interlace("plane")
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