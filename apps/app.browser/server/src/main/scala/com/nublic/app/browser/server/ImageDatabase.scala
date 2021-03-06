package com.nublic.app.browser.server

import java.io.InputStream
import com.nublic.app.browser.server.filewatcher.workers._
import org.apache.commons.io.output.ByteArrayOutputStream
import org.apache.commons.io.CopyUtils
import java.util.Date
import java.util.GregorianCalendar
import java.util.Calendar

object ImageDatabase {
  
  val DIRECTORY_MIME1 = "application/x-directory"
  val DIRECTORY_MIME2 = "inode/directory"
  
  def LAST_MODIFIED_DATE = new GregorianCalendar(2012, Calendar.FEBRUARY, 23).getTime()
  
  def getImageBytes(mime: String): Array[Byte] = {
    val input = classOf[BrowserServer].getResourceAsStream(getImagePath(mime))
    val output = new ByteArrayOutputStream()
    CopyUtils.copy(input, output)
    output.toByteArray()
  }
  
  def getImagePath(mime: String): String = {
    if (mime == DIRECTORY_MIME1 || mime == DIRECTORY_MIME2) {
      "/images/folder.png"
    } else if (PdfWorker.supportedMimeTypes.contains(mime) ||
        OfficeWorker.wordProcessorMimeTypes.contains(mime)) {
      "/images/document.png"
    } else if (OfficeWorker.spreadsheetMimeTypes.contains(mime)) {
      "/images/spreadsheet.png"
    } else if (OfficeWorker.presentationMimeTypes.contains(mime)) {
      "/images/presentation.png"
    } else if (OfficeWorker.drawingMimeTypes.contains(mime)) {
      "/images/drawing.png"
    } else if (AudioWorker.supportedMimeTypes.contains(mime) ||
        mime.startsWith("audio/")) {
      "/images/audio.png"
    } else if (VideoWorker.supportedMimeTypes.contains(mime) ||
        mime.startsWith("video/")) {
      "/images/video.png"
    } else if (ImageWorker.supportedMimeTypes.contains(mime) ||
        mime.startsWith("image/")) {
      "/images/image.png"
    } else {
      "/images/file.png"
    }
  }
}
