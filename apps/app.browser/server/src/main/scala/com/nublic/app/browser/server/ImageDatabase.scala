package com.nublic.app.browser.server

import java.io.InputStream
import com.nublic.app.browser.server.filewatcher.workers._
import org.apache.commons.io.output.ByteArrayOutputStream
import org.apache.commons.io.CopyUtils
import java.util.Date

object ImageDatabase {
  
  val DIRECTORY_MIME = "application/x-directory"
  
  val LAST_MODIFIED_DATE = new Date(112, 1, 22)
  
  def getImageBytes(mime: String): Array[Byte] = {
    val input = classOf[BrowserServer].getResourceAsStream(getImagePath(mime))
    val output = new ByteArrayOutputStream()
    CopyUtils.copy(input, output)
    output.toByteArray()
  }
  
  def getImagePath(mime: String): String = {
    if (mime == DIRECTORY_MIME) {
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