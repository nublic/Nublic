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
import org.apache.commons.io.FileUtils

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
            case None => {
              response.setContentType("image/png")
              ImageDatabase.getImageBytes("unknown")
            }
            case Some(mime) => {
              response.setContentType("image/png")
              ImageDatabase.getImageBytes(mime)
            }
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
  
  post("/rename") {
    val from = params("from")
    val to = params("to")
    if (from.contains("..") || to.contains("..")) {
      halt(403)
    } else {
      val from_path = new File(NUBLIC_DATA_ROOT + from)
      val to_path = new File(NUBLIC_DATA_ROOT + to)
      if (from_path.isDirectory()) {
        FileUtils.moveDirectory(from_path, to_path)
      } else {
    	FileUtils.moveFile(from_path, to_path)
      }
      halt(200)
    }
  }
  
  post("/move") {
    val from = params("files").split(":")
    val to = params("target")
    if ( from.exists(_.contains("..")) || to.contains("..") ) {
      halt(403)
    } else {
      val from_paths = from.map(s => new File(NUBLIC_DATA_ROOT + s))
      val to_path = new File(NUBLIC_DATA_ROOT + to)
      from_paths.map(f => FileUtils.moveToDirectory(f, to_path, true))
      halt(200)
    }
  }
  
  post("/copy") {
    val from = params("files").split(":")
    val to = params("target")
    if ( from.exists(_.contains("..")) || to.contains("..") ) {
      halt(403)
    } else {
      val from_paths = from.map(s => new File(NUBLIC_DATA_ROOT + s))
      val to_path = new File(NUBLIC_DATA_ROOT + to)
      from_paths.map(f => 
        if (f.isDirectory) {
          FileUtils.copyDirectoryToDirectory(f, to_path)
        } else {
          FileUtils.copyFileToDirectory(f, to_path)
        }
      )
      halt(200)
    }
  }
  
  post("/delete") {
    val files = params("files").split(":")
    if (files.exists(_.contains(".."))) {
      halt(403)
    } else {
      val files_paths = files.map(s => new File(NUBLIC_DATA_ROOT + s))
      files_paths.map(f => 
        if (f.isDirectory) {
          FileUtils.deleteDirectory(f)
        } else {
          FileUtils.deleteQuietly(f)
        }
      )
      halt(200)
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
  
  post("/zip-set") {
    val files = params("files").split(":").toList
    val filename = params("filename")
    files match {
      case initial :: rest => { 
        if (files.exists(_.contains(".."))) {
          halt(403)
        } else {
          val range = 0 until (initial.length() + 1)
          val prefix_length = range.lastIndexWhere(n => {
            val prefix = initial.substring(0, n)
            if (!prefix.endsWith("/")) {
              false
            } else {
              rest.forall(_.startsWith(prefix))
            }
          })
          val base_path = NUBLIC_DATA_ROOT + initial.substring(0, prefix_length)
          val files_to_add = files.map(s => new File(NUBLIC_DATA_ROOT + s))
          response.setContentType("application/zip")
          response.setHeader("Content-Disposition", "attachment; filename=" + filename )
          Zip.zipFileSeq(files_to_add, base_path).toByteArray()
        }
      }
      case Nil => halt(403)
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
	subfolders.sort((a, b) => a.name.compareToIgnoreCase(b.name) < 0)
  }
  
  def get_files(folder: File): List[BrowserFile] = {
    var files = List[BrowserFile]()
	for (file <- folder.listFiles()) {
	  if (!is_hidden(file.getName())) {
	    Solr.getMimeType(file.getPath()) match {
	      case None       => { /* This should not happen */ }
	      case Some(mime) => 
	        files ::= BrowserFile(file.getName(), mime,
	            find_view(file.getAbsolutePath(), mime),
	            file.length(), file.lastModified())
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
