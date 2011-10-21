package com.nublic.app.browser.server

import java.io.File
import org.scalatra._
import org.scalatra.liftjson.JsonSupport
import net.liftweb.json._
import net.liftweb.json.Serialization.{read, write}
import org.apache.commons.httpclient.util.URIUtil
import org.apache.commons.io.FilenameUtils
import com.nublic.app.browser.server.filewatcher.FileActor
import com.nublic.app.browser.server.filewatcher.FileFolder
import com.nublic.app.browser.server.filewatcher.workers.Workers
import javax.servlet.http.HttpServlet
import org.apache.commons.io.output.ByteArrayOutputStream
import org.apache.commons.io.CopyUtils

class BrowserServer extends ScalatraFilter with JsonSupport {
  // JsonSupport adds the ability to return JSON objects
  
  val NUBLIC_DATA_ROOT = "/var/nublic/data/"
  val THE_REST = "splat"
  
  val watcher = new FileActor()
  watcher.start()
  
  implicit val formats = Serialization.formats(NoTypeHints)
  
  get("/folders/:depth/*") {
    val depth = Integer.valueOf(params("depth"))
    val path = URIUtil.decode(params(THE_REST))
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
    val path = URIUtil.decode(params(THE_REST))
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
  
  get("/raw/*") {
    val path = URIUtil.decode(params(THE_REST))
    if (path.contains("..")) {
      // We don't want paths going upwards
      halt(403)
    } else {
      val nublic_path = NUBLIC_DATA_ROOT + path
      val file = new File(nublic_path)
      if (!file.exists() || file.isDirectory()) {
        halt(403)
      } else {
        Solr.getMimeType(nublic_path) match {
          case None       => { }
          case Some(mime) => response.setContentType(mime)
        }
        file
      }
    }
  }
  
  get("/view/*") { // Where * = :file.:type
    val rest = URIUtil.decode(params(THE_REST))
    if (rest.contains("..") || !rest.contains(".")) {
      // We don't want paths going upwards
      halt(403)
    } else {
      // Separate view from rest of file name
      val index_of_point = rest.lastIndexOf('.')
      val path = rest.substring(0, index_of_point)
      val viewName = rest.substring(index_of_point + 1)
      // Find in file system
      val nublic_path = NUBLIC_DATA_ROOT + path
      val file = new File(nublic_path)
      if (!file.exists() || file.isDirectory()) {
        halt(403)
      } else {
        var found: Option[Tuple2[File, String]] = None
        for (worker <- Workers.byViewName.getOrElse(viewName, Nil)) {
          if (worker.hasView(viewName, nublic_path, Solr.getMimeType(nublic_path).getOrElse("unknown"))) {
            found = Some((worker.getView(viewName, nublic_path), worker.getMimeTypeForView(viewName)))
          }
        }
        found match {
          case None    => halt(404)
          case Some((file, mime)) => {
            response.setContentType(mime)
            file
          }
        }
      }
    }
  }
  
  get("/zip/*") {
    val path = URIUtil.decode(params(THE_REST))
    if (path.contains("..")) {
      // We don't want paths going upwards
      halt(403)
    } else {
      val nublic_path = NUBLIC_DATA_ROOT + path
      val file = new File(nublic_path)
      if (!file.exists()) {
        halt(404)
      } else {
        val zip_name = FilenameUtils.getBaseName(path) + ".zip"
        response.setContentType("application/zip")
        response.setHeader("Content-Disposition", "attachment; filename=" + zip_name)
        Zip.zip(nublic_path).toByteArray()
      }
    }
  }
  
  get("/thumbnail/*") {
    val path = URIUtil.decode(params(THE_REST))
    if (path.contains("..")) {
      // We don't want paths going upwards
      halt(403)
    } else {
      val nublic_path = NUBLIC_DATA_ROOT + path
      val file = new File(nublic_path)
      if (!file.exists()) {
        halt(404)
      } else {
        val thumb_file = FileFolder.getThumbnail(nublic_path)
        if (thumb_file.exists()) {
          response.setContentType("image/png")
          thumb_file
        } else {
          Solr.getMimeType(nublic_path) match {
            case None       => redirect(request.getContextPath() + "/generic-thumbnail/unknown")
            case Some(mime) => redirect(request.getContextPath() + "/generic-thumbnail/" + mime)
          }
        }
      }
    }
  }
  
  get("/generic-thumbnail/*") {
    val name = URIUtil.decode(params(THE_REST))
    response.setContentType("image/png")
    ImageDatabase.getImageBytes(name)
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
	subfolders.sort((a, b) => a.name.compareToIgnoreCase(b.name) < 0)
  }
  
  def get_files(folder: File): List[BrowserFile] = {
    var files = List[BrowserFile]()
	for (file <- folder.listFiles()) {
	  if (!is_hidden(file.getName())) {
	    Solr.getMimeType(file.getPath()) match {
	      case None       => { /* This should not happen */ }
	      case Some(mime) => files ::= BrowserFile(file.getName(), mime, find_view(file.getAbsolutePath(), mime))
	    }
	  }
	}
	files.sort(fileLt)
  }
  
  def fileLt(a: BrowserFile, b: BrowserFile) = {
    (a.isDirectory, b.isDirectory) match {
      case (true, false) => true
      case (false, true) => false
      case _             => a.name.compareToIgnoreCase(b.name) < 0
    }
  }
  
  def find_view(file: String, mime: String): String = {
    for(view <- Workers.byViewName.keys) {
      for(worker <- Workers.byViewName.getOrElse(view, Nil)) {
        if(worker.hasView(view, file, mime)) {
          return view
        }
      }
    }
    return null
  }
  
  def is_hidden(filename: String) = filename.startsWith(".") || filename.endsWith("~")
}
