package com.nublic.app.music.server.filewatcher

import com.nublic.app.music.server.model.Artist
import com.nublic.app.music.server.model.Album
import com.echonest.api.{v4 => E}
import com.echonest.api.v4.util.Commander
import java.net.URL
import org.apache.commons.io.FileUtils
import com.nublic.app.music.server.MusicFolder
import java.io.File
import java.io.FileReader
import org.im4java.core.ConvertCmd
import org.im4java.core.IMOperation
import net.liftweb.json._
import org.apache.commons.io.IOUtils
import java.net.URI
import org.jaudiotagger.audio.AudioFileIO
import java.io.FileOutputStream

object Images {
  
  val ECHONEST_API_KEY = "UR4VKX7JXDXAULIWB"
  implicit val formats = Serialization.formats(NoTypeHints)
  
  def ensure(id: Long, folderer: Long => File, getter: File => Unit): Unit = {
    val folder = folderer(id)
	// Create folder if it does not exist
    if (!folder.exists()) {
      folder.mkdirs()
    }
    // Download original image from web
    val original = new File(folder, MusicFolder.ORIGINAL_FILENAME)
    if (!original.exists()) {
      getter(original)
    }
    // Create thumbnail
    val thumb = new File(folder, MusicFolder.THUMBNAIL_FILENAME)
    if (original.exists() && !thumb.exists()) {
      val magick = new ConvertCmd()
      val op = new IMOperation() 
      op.addImage(original.getAbsolutePath())
      op.resize(MusicFolder.ARTIST_THUMBNAIL_SIZE, MusicFolder.ARTIST_THUMBNAIL_SIZE)
      op.addImage(thumb.getAbsolutePath())
      magick.run(op)
    }
  }
    
  def ensureArtist(a: Artist) = 
    ensure(a.id, l => MusicFolder.getArtistFolder(l), getArtistImage(a))
  
  def getArtistImage(a: Artist)(f: File) = {
    try {
      val api = new E.EchoNestAPI(ECHONEST_API_KEY)
      val artist = api.newArtistByName(a.name)
      val image_infos = artist.getImages()
      if (!image_infos.isEmpty()) {
        val image_url = new URL(image_infos.get(0).getURL())
        FileUtils.copyURLToFile(image_url, f)
      }
    } catch {
      case _: Throwable => { /* Do nothing */ }
    }
  }
  
  def ensureAlbum(mp3_file: File, ab: Album, ar: Option[Artist]) = 
    ensure(ab.id, l => MusicFolder.getAlbumFolder(l), getAlbumImage(mp3_file, ab, ar))
  
  def getAlbumImage(mp3_file: File, ab: Album, ar: Option[Artist])(f: File) {
    for (getter <- List(getAlbumImageFilesystem(mp3_file)_, getAlbumImageEmbedded(mp3_file)_, getAlbumImageDiscogs(ab, ar)_)) {
      if (!f.exists()) {
        getter(f)
      }
    }
  }
    
  def getAlbumImageFilesystem(mp3_f: File)(image_f: File) = {
    val regex = """(cover|folder|front|album|albumart)\.(jpg|jpeg|png|bmp)"""
    mp3_f.getParentFile().listFiles().toList.filterNot(_.isDirectory).filter(f => f.getName().matches(regex)) match {
      case cover_file :: _ => FileUtils.copyFile(cover_file, image_f)
      case _               => { /* Do nothing */ }
    }
  }
  
  def getAlbumImageEmbedded(mp3_f: File)(image_f: File) = {
    try {
      val audio = AudioFileIO.read(mp3_f)
      val tag = audio.getTag()
      val artwork = tag.getFirstArtwork()
      if (artwork != null) {
        val stream = new FileOutputStream(image_f)
        stream.write(artwork.getBinaryData())
        stream.flush()
        stream.close()
      }
    } catch {
      case _: Throwable => { /* Do nothing */ }
    }
  }
  
  def getAlbumImageDiscogs(ab: Album, ar: Option[Artist])(f: File) = {
    try {
      val search = "f=json&type=releases&q=" + ab.name + " " + ar.map(_.name).getOrElse("")
      val json_uri = new URI("http", "api.discogs.com", "/search", search, null)
      val json_url = json_uri.toURL()
      val con = json_url.openConnection()
      con.addRequestProperty("Accept-Encoding", "gzip")
      val in = con.getInputStream()
      var encoding = con.getContentEncoding()
      encoding = if (encoding == null) "UTF-8" else encoding
      val body = IOUtils.toString(in, encoding)
      
      val response = parse(body).extract[DiscogResponse]
      if (!response.resp.search.searchresults.results.isEmpty) {
        val result = response.resp.search.searchresults.results.head
        val image_url = new URL(result.thumb)
        FileUtils.copyURLToFile(image_url, f)
      }
    } catch {
      case _: Throwable => { /* Do nothing */ }
    }
  }
  
  case class DiscogResult(thumb: String, title: String)
  case class DiscogResults(numResults: String, start: String, end: String, results: List[DiscogResult])
  case class DiscogSearch(searchresults: DiscogResults)
  case class DiscogResp(status: Boolean, version: String, search: DiscogSearch)
  case class DiscogResponse(resp: DiscogResp)
}