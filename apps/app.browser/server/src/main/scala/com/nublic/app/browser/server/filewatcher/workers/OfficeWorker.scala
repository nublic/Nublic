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

  def supportedMimeTypes: List[String] = List(
      /* Obtained looking at:
       * - List of files supported by unoconv: `unoconv --show`
       * - Information about file extensions: http://filext.com/
       */
      
      // MICROSOFT OFFICE
      "application/vnd.ms-office",
      // Word .doc
      "application/msword", "application/doc",
      "application/vnd.msword", "application/vnd.ms-word", 
      "application/winword", "application/word", 
      "application/x-msw6", "application/x-msword",
      // Excel .xls
      "application/vnd.ms-excel", "application/msexcel", 
      "application/x-msexcel", "application/x-ms-excel", 
      "application/vnd.ms-excel", "application/x-excel", 
      "application/x-dos_ms_excel", "application/xls",
      // PowerPoint .ppt
      "application/vnd.ms-powerpoint", "application/mspowerpoint",
      "application/ms-powerpoint", "application/mspowerpnt", 
      "application/vnd-mspowerpoint", "application/powerpoint", 
      "application/x-powerpoint",
      
      // MICROSOFT OFFICE XML
      // Word .docx
      "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
      // Excel .xlsx
      "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
      // PowerPoint .pptx
      "application/vnd.openxmlformats-officedocument.presentationml.presentation",
      
      // LIBREOFFICE
      // Writer .odt
      "application/vnd.oasis.opendocument.text",
      "application/x-vnd.oasis.opendocument.text",
      // Calc .ods
      "application/vnd.oasis.opendocument.spreadsheet",
      "application/x-vnd.oasis.opendocument.spreadsheet",
      // Impress .odp
      "application/vnd.oasis.opendocument.presentation",
      "application/x-vnd.oasis.opendocument.presentation",
      // Draw .odg
      "application/vnd.oasis.opendocument.graphics",
      "application/x-vnd.oasis.opendocument.graphics",
      
      // OLD OPENOFFICE
      // Writer .sxw
      "application/vnd.sun.xml.writer",
      // Calc .sxc
      "application/vnd.sun.xml.calc",
      // Impress .sxi
      "application/vnd.sun.xml.impress",
      // Draw .sxd
      "application/vnd.sun.xml.draw",
      
      // STAROFFICE
      // General for old StarOffice
      "application/x-staroffice", "application/soffice", "application/x-soffice",
      // Writer .sdw
      "application/x-swriter", "application/vnd.stardivision.writer",
      // Calc .sdc
      "application/vnd.stardivision.calc",
      // Impress .sdd
      "application/vnd.stardivision.impress",
      // Draw .sda
      "application/x-sdraw", "application/x-sda", "application/vnd.stardivision.draw",
      
      // OTHER
      // Rich Text Format .rtf
      "application/rtf", "application/x-rtf", "text/rtf", "text/richtext"
      )

  def supportedViews: List[String] = List("pdf")
      
  val PDF_FILENAME = "doc.pdf"
  
  def process(file: String, folder: File): Unit = {
    // Run `unoconv --stdout -f pdf ${file} > ${folder}/doc.pdf`
    val cmd = new ProcessBuilder("unoconv", "--stdout", "-f", "pdf", file)
    val process = cmd.start()
    val pdfFile = new File(folder, PDF_FILENAME)
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
    // Create the "pipe" to get the file
    process.waitFor()
    
    // Now create the thumbnail
    val thumb_file = new File(folder, FileFolder.THUMBNAIL_FILENAME)
    val magick = new ConvertCmd()
    val op = new IMOperation() 
    op.addImage(pdfFile.getAbsolutePath() + "[0]")
    op.resize(FileFolder.THUMBNAIL_SIZE)
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