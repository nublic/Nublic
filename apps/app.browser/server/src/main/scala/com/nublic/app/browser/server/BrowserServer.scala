package com.nublic.app.browser.server

import org.scalatra._
import org.scalatra.liftjson.JsonSupport
import net.liftweb.json._
import net.liftweb.json.Serialization.{read, write}
import com.nublic.app.browser.server.filewatcher.FileActor
import org.apache.commons.httpclient.util.URIUtil
import java.io.File

class BrowserServer extends ScalatraFilter with JsonSupport {
  // JsonSupport adds the ability to return JSON objects
  
  val NUBLIC_DATA_ROOT = "/var/nublic/data/"
  
  val watcher = new FileActor()
  watcher.start()
  
  implicit val formats = Serialization.formats(NoTypeHints)
  
  get("/folders/:depth/*") {
    val depth = Integer.valueOf(params("depth"))
    val path = URIUtil.decode(params("splat"))
    if (path.contains("..") || depth <= 0) {
      // We don't want paths going upwards
      // or depths of less than 1
      JNull
    } else {
      val nublic_path = NUBLIC_DATA_ROOT + path
      val folder = new File(nublic_path)
      if (!folder.exists()) {
        JNull
      } else {
        write(get_subfolders(folder, depth))
      }
    }
  }
  
  get("/files/*") {
    val path = URIUtil.decode(params("splat"))
    if (path.contains("..")) {
      // We don't want paths going upwards
      JNull
    } else {
      val nublic_path = NUBLIC_DATA_ROOT + path
      val folder = new File(nublic_path)
      if (!folder.exists()) {
        JNull
      } else {
        write(get_files(folder))
      }
    }
  }
  
  notFound {  // Executed when no other route succeeds
    JNull
  }
  
  def get_subfolders(folder: File, depth: Int): List[BrowserFolder] = {    
	var subfolders = List[BrowserFolder]()
	for (file <- folder.listFiles()) {
	  if (!is_hidden(file.getName()) && file.isDirectory()) {
	    if (depth == 1) {
	      subfolders ::= BrowserFolder(file.getName(), Nil)
	    } else {
	      subfolders ::= BrowserFolder(file.getName(), get_subfolders(file, depth-1))
	    }
	  }
	}
	subfolders
  }
  
  def get_files(folder: File): List[BrowserFile] = {
    var files = List[BrowserFile]()
	for (file <- folder.listFiles()) {
	  if (!is_hidden(file.getName())) {
	    Solr.getMimeType(file.getPath()) match {
	      case None       => { /* This should not happen */ }
	      case Some(mime) => files ::= BrowserFile(file.getName(), mime)
	    }
	  }
	}
	files
  }
  
  def is_hidden(filename: String) = filename.startsWith(".") || filename.endsWith("~")
}
