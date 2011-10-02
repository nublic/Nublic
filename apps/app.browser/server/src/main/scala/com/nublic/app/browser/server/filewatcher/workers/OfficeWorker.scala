package com.nublic.app.browser.server.filewatcher.workers

import com.nublic.app.browser.server.filewatcher.DocumentWorker
import scala.collection.immutable.List

class OfficeWorker extends DocumentWorker {

  def supportedMimeTypes: List[String] = List(
      /* Obtained looking at:
       * - List of files supported by unoconv: `unoconv --show`
       * - Information about file extensions: http://filext.com/
       */
      
      // MICROSOFT OFFICE
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

  def process(file: String, folder: String): Unit = {
    
  }

}