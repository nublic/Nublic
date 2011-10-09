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

object VideoWorker extends DocumentWorker {

  def supportedMimeTypes: List[String] = List(
      /* Obtained looking at:
       * - List of files supported by ffmpeg: `ffmpeg -formats`
       * - Information about file extensions: http://filext.com/
       */
      
      // AVI
      "video/avi", "video/msvideo", "video/x-msvideo",
      "image/avi", "video/xmpg2", "application/x-troff-msvideo",
      // MPEG
      "video/mpeg", "video/mpg", "video/x-mpg", 
      "video/mpeg2", "application/x-pn-mpg", "video/x-mpeg",
      "video/x-mpeg2a",
      // ASF, WMV
      "video/x-ms-asf-plugin", "application/x-mplayer2", "video/x-ms-asf",
      "application/vnd.ms-asf", "video/x-ms-asf-plugin", "video/x-ms-wm",
      "video/x-ms-wmx", "video/x-ms-wmv",
      // FLV
      "video/x-flv",
      // MOV
      "video/quicktime", "video/x-quicktime",
      // DV
      "video/x-dv",
      // MP4
      "video/mp4v-es"
      // MKV
      // no known mime type
      )

  def supportedViews: List[String] = List("flv")
  
  val SAMPLE_FREQ       = 22050
  val FLV_TEMP_FILENAME = "video_temp.flv"
  val FLV_FILENAME      = "video.flv"
      
  def process(file: String, folder: File): Unit = {
    // Run `mencoder -forceidx -of lavf -oac mp3lame -lameopts abr:br=56 -srate 22050 -ovc lavc
    //      -lavcopts vcodec=flv:vbitrate=250:mbd=2:mv0:trell:v4mv:cbp:last_pred=3 -o <temp> <in>`
    // First do it on a temporary file and them move the result
    val flvTempFile = new File(folder, FLV_TEMP_FILENAME)
    val cmd = new ProcessBuilder("mencoder", "-forceidx", "-of", "lavf", "-oac", "mp3lame",
        "-lameopts", "abr:br=56", "-srate", "22050", "-ovc", "lavc", "-lavcopts",
        "vcodec=flv:vbitrate=250:mbd=2:mv0:trell:v4mv:cbp:last_pred=3",
        "-o", flvTempFile.getAbsolutePath(), file)
    cmd.redirectErrorStream(true)
    val process = cmd.start()
    flushActor(process).start
    process.waitFor()
    // Run `flvtool2 -U <temp>` to get metadata
    val cmd2 = new ProcessBuilder("flvtool2", "-U", flvTempFile.getAbsolutePath())
    cmd2.redirectErrorStream(true)
    val process2 = cmd2.start()
    flushActor(process2).start
    process2.waitFor()
    // Now rename the file
    val flvFile = new File(folder, FLV_FILENAME)
    flvTempFile.renameTo(flvFile)
  }
  
  def getMimeTypeForView(viewName: String): String = viewName match {
    case "flv" => "video/x-flv"
    case _     => null
  }
  
  def hasView(viewName: String, file: String): Boolean = {
    val folder = FileFolder.getFolder(file)
    viewName match {
      case "flv" => {
        val flv_file = new File(folder, FLV_FILENAME)
        flv_file.exists()
      }
      case _ => false
    }
  }
  
  def getView(viewName: String, file: String): File = {
    val folder = FileFolder.getFolder(file)
    viewName match {
      case "flv" => {
        val flv_file = new File(folder, FLV_FILENAME)
        if (flv_file.exists()) flv_file else null
      }
      case _ => null
    }
  }

}