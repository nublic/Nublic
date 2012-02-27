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
      "application/x-wmf", "image/wmf", "image/x-wmf" ,
      "image/x-ms-bmp"
    )

  def supportedViews(): List[String] = List("png")
  
  val PNG_FILENAME = "image.png"

  def process(file: String, folder: File): Unit = {
    // Now create the thumbnail
    val thumb_file = new File(folder, FileFolder.THUMBNAIL_FILENAME)
    val magick = new ConvertCmd()
    val op = new IMOperation() 
    op.addImage(file)
    op.resize(FileFolder.THUMBNAIL_SIZE, FileFolder.THUMBNAIL_SIZE)
    op.interlace("plane")
    op.quality(5); // Huffmann compresion + adaptive filter
    op.addImage(thumb_file.getAbsolutePath())
    magick.run(op)
    
    // And now the same image in png
    val png_file = new File(folder, PNG_FILENAME)
    val magick2 = new ConvertCmd()
    val op2 = new IMOperation() 
    op2.addImage(file)
    op.resize(FileFolder.MAX_IMAGE_WIDTH, FileFolder.MAX_IMAGE_HEIGHT, '>')
    op2.interlace("plane")
    op2.quality(5); // Huffmann compresion + adaptive filter
    op2.addImage(png_file.getAbsolutePath())
    magick2.run(op2)
  }

  def getMimeTypeForView(viewName: String): String = viewName match {
    case "png" => "image/png"
    case _       => null
  }

  def hasView(viewName: String, file: String, mime: String): Boolean = {
    if (!supportedMimeTypes.contains(mime)) {
      false
    } else {
      val folder = FileFolder.getFolder(file)
      viewName match {
        case "png" => {
          val png_file = new File(folder, PNG_FILENAME)
          png_file.exists()
        }
        case _ => false
      }
    }
  }

  def getView(viewName: String, file: String): File =  {
    val folder = FileFolder.getFolder(file)
    viewName match {
      case "png" => {
        val png_file = new File(folder, PNG_FILENAME)
        if (png_file.exists()) png_file else null
      }
      case _ => null
    }
  }

}