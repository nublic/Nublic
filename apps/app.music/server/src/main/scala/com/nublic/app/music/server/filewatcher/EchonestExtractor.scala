package com.nublic.app.music.server.filewatcher

import java.io.File
import org.apache.commons.io.output.ByteArrayOutputStream
import scala.actors.Actor._
import com.echonest.api.{v4 => E}
import com.echonest.api.v4.util.Commander
import java.io.BufferedInputStream

// Extracting info from Echonest
// =============================
object EchonestExtractor {
  
  val ECHONEST_API_KEY = "UR4VKX7JXDXAULIWB"
  
  def from(filename: String): Option[SongInfo] = {
    try {
      from_internal(filename)
    } catch {
      case _: Throwable => None
    }
  }
 
  def from_internal(filename: String): Option[SongInfo] = {
    val file = new File(filename)
    // Get fingerprint
    val cmd = new ProcessBuilder("echoprint-codegen", filename, "10", "30")
    val process = cmd.start()
    val byteStream = new ByteArrayOutputStream()
    actor {
      // Read file
      val buffer = new Array[Byte](1024)
      val buffered_in_stream = new BufferedInputStream(process.getInputStream())
      var bytes_read = buffered_in_stream.read(buffer, 0, buffer.length)
      while(bytes_read != -1) {
        byteStream.write(buffer, 0, bytes_read)
        bytes_read = buffered_in_stream.read(buffer, 0, buffer.length)
      }
      // Get bytes
      byteStream.flush()
      byteStream.close()
    }.start
    process.waitFor()
    // Send query
    val query = byteStream.toString()
    val commander = new Commander("Nublic")
    val params = new E.Params()
    params.set("api_key", ECHONEST_API_KEY)
    params.set("query", query)
    val result = commander.sendCommand("song/identify", params, true)
    val response = result.get("response").asInstanceOf[java.util.Map[String, _]]
    val songList = response.get("songs").asInstanceOf[java.util.List[_]]
    if (songList.size() > 0) {
      val songMap = songList.get(0).asInstanceOf[java.util.Map[String, _]]
      val title = songMap.get("title").asInstanceOf[String]
      val artist = songMap.get("artist_name").asInstanceOf[String]
      Some(SongInfo(Some(title), Some(artist),None,  None, None, None, None))
    } else {
      None
    }
  }
}