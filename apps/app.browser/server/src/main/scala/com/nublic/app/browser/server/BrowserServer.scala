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
import com.nublic.filesAndUsers.java._
import java.nio.file.FileSystems
import java.nio.file.Files
import scala.collection.JavaConversions._
import org.scalatra.fileupload.FileUploadSupport

class BrowserServer extends ScalatraFilter with JsonSupport with FileUploadSupport {
  // JsonSupport adds the ability to return JSON objects
  
  val NUBLIC_DATA_ROOT = "/var/nublic/data/"
  val THE_REST = "splat"
  val SEPARATOR = ":"
  
  val watcher = new FileActor()
  watcher.start()
  
  implicit val formats = Serialization.formats(NoTypeHints)
  
  def withUser(action: User => Any) : Any = {
    val user = new User(request.getRemoteUser())
    action(user)
  }
  
  def withRestPath(allowBlank: Boolean)(action: File => Any) : Any = withPath(THE_REST, allowBlank)(action)
  
  def withUserAndRestPath(allowBlank: Boolean)(action: User => File => Any) : Any = withUser {
    user => withRestPath(allowBlank) {
      path => action(user)(path)
    }
  }
  
  def withPath(param_name: String, allowBlank: Boolean)(action: File => Any) : Any = {
    val path = URIUtil.decode(params(param_name))
    if ((path.isEmpty && !allowBlank) || path.contains("..")) {
      halt(403)
    } else {
      val nublic_path = NUBLIC_DATA_ROOT + path
      action(new File(nublic_path))
    }
  }
  
  def withRestMultiplePaths(action: List[File] => Any) : Any = withMultiplePaths(THE_REST)(action)
  
  def withMultiplePaths(param_name: String)(action: List[File] => Any) : Any = {
    val paths = params(param_name).split(SEPARATOR).toList
    if (paths.exists(p => p.isEmpty || p.contains(".."))) {
      halt(403)
    } else {
      val file_paths = paths.map(s => new File(NUBLIC_DATA_ROOT + s))
      action(file_paths)
    }
  }
  
  get("/devices") {
    withUser { user =>
      val mirrors = user.getAccessibleMirrors().toList.map {
        m: Mirror => BrowserDevice(m.getId(), BrowserDevice.MIRROR, m.getName(), user.isOwner(m))
      }
      val synceds = user.getAccessibleSyncedFolders().toList.map {
        m: SyncedFolder => BrowserDevice(m.getId(), BrowserDevice.SYNCED_FOLDER, m.getName(), user.isOwner(m))
      }
      write(mirrors ++ synceds)
    }
  }
  
  get("/folders/:depth/*") {
    withUserAndRestPath(true) { user => folder =>
      val depth = Integer.valueOf(params("depth"))
      if (depth <= 0) {
        halt(500)
      } else {
        if (!folder.exists() || !user.canRead(folder)) {
          JNull
        } else {
          write(get_subfolders(folder, depth, user))
        }
      }
    }
  }
  
  get("/files/*") {
    withUserAndRestPath(true) { user => folder =>
      if (!folder.exists() || !user.canRead(folder)) {
        JNull
      } else {
        write(get_files(folder, user))
      }
    }
  }
  
  get("/changes/:since/*") {
    withUserAndRestPath(false) { user => folder => 
      val since = java.lang.Long.valueOf(params("since"))
      val new_files = watcher.getDeletionProcessor.getNewFilesSince(folder.getAbsolutePath(), since)
      val new_files_info = new_files.map(f => get_one_file(new File(f), user))
      val deleted_files = watcher.getDeletionProcessor.getDeletedFilesSince(folder.getAbsolutePath(), since)
      val deleted_files_names = deleted_files.map(f => {
        val i = f.lastIndexOf("/")
        f.substring(i + 1)
      })
      write(BrowserPoll(new_files_info, deleted_files_names))
    }
  }
  
  get("/raw/*") {
    withUserAndRestPath(false) { user => file =>
      if (!file.exists() || file.isDirectory() || !user.canRead(file)) {
        halt(403)
      } else {
        Solr.getMimeType(file.getPath()) match {
          case None       => { }
          case Some(mime) => response.setContentType(mime)
        }
        response.setHeader("Content-Disposition", "attachment; filename=" + file.getName())
        file
      }
    }
  }
  
  get("/view/*") { // Where * = :file.:type
    withUser { user =>
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
        if (!file.exists() || file.isDirectory() || !user.canRead(file)) {
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
  }
  
  get("/thumbnail/*") {
    withUserAndRestPath(false) { user => file =>
      if (!file.exists() || !user.canRead(file)) {
        halt(404)
      } else {
        val thumb_file = FileFolder.getThumbnail(file.getPath())
        if (thumb_file.exists()) {
          response.setContentType("image/png")
          thumb_file
        } else {
          Solr.getMimeType(file.getPath()) match {
            case None => {
              response.setContentType("image/png")
              if (file.isDirectory()) {
                ImageDatabase.getImageBytes(ImageDatabase.DIRECTORY_MIME)
              } else {
                ImageDatabase.getImageBytes("unknown")
              }
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
    withUser { user =>
      withPath("from", false) { from_path =>
        withPath("to", false) { to_path =>
          if (to_path.exists() || !user.canWrite(from_path)) {
            halt(403)
          } else {
            if (from_path.isDirectory()) {
              FileUtils.moveDirectory(from_path, to_path)
            } else {
    	      FileUtils.moveFile(from_path, to_path)
            }
            halt(200)
          }
        }
      }
    }
  }
  
  post("/move") {
    withUser { user =>
      withMultiplePaths("files") { from_paths =>
        withPath("target", false) { to_path =>
          from_paths.map(f => do_move(f, to_path, user))
          halt(200)
        }
      }
    }
  }
  
  def do_move(file: File, target: File, user: User): Unit = {
    val final_file = new File(target, file.getName())
    if (!final_file.exists() && user.canRead(file) && user.canWrite(target)) {
      if (!file.isDirectory) {
        FileUtils.moveToDirectory(file, target, true)
        // do_chown(final_file, user)
      } else {
        final_file.mkdir()
        // do_chown(final_file, user)
        for (child <- file.listFiles) {
          do_move(child, final_file, user)
        }
        if (file.listFiles.length == 0) {
          file.delete()
        }
      }
    }
  }
  
  post("/copy") {
    withUser { user =>
      withMultiplePaths("files") { from_paths =>
        withPath("target", false) { to_path =>
          from_paths.map(f => do_copy(f, to_path, user))
          halt(200)
        }
      }
    }
  }
  
  def do_copy(file: File, target: File, user: User): Unit = {
    val final_file = new File(target, file.getName())
    if (!final_file.exists() && user.canRead(file) && user.canWrite(target)) {
      if (!file.isDirectory) {
        FileUtils.copyFileToDirectory(file, target)
        do_chown(final_file, user)
      } else {
        final_file.mkdir()
        do_chown(final_file, user)
        for (child <- file.listFiles) {
          do_copy(child, final_file, user)
        }
      }
    }
  }
  
  def do_chown(file: File, user: User) = {
    user.assignFile(file)
  }
  
  post("/delete") {
    withUser { user =>
      withMultiplePaths("files") { files_paths =>
        files_paths.map(f => do_delete(f, user))
        halt(200)
      }
    }
  }
  
  def do_delete(file: File, user: User): Unit = {
    if (user.canWrite(file)) {
      if (!file.isDirectory) {
        FileUtils.deleteQuietly(file)
      } else {
        // Delete everything recursively
        for (child <- file.listFiles) {
          do_delete(child, user)
        }
        // If empty, remove directory
        if (file.listFiles.length == 0) {
          file.delete()
        }
      }
    }
  }
  
  get("/zip/*") {
    withUserAndRestPath(false) { user => file =>
      if (!file.exists() || !user.canRead(file)) {
        halt(404)
      } else {
        val zip_name = FilenameUtils.getBaseName(file.getPath()) + ".zip"
        response.setContentType("application/zip")
        response.setHeader("Content-Disposition", "attachment; filename=" + zip_name)
        Zip.zip(file, user).toByteArray()
      }
    }
  }
  
  post("/zip-set") {
    withUser { user =>
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
            Zip.zipFileSeq(files_to_add, base_path, user).toByteArray()
          }
        }
        case Nil => halt(403)
      }
    }
  }
  
  post("/new-folder") {
    withUser { user =>
      withPath("path", false) { folder =>
        val name = params("name").trim()
        if (name.contains("..") || !folder.isDirectory() || !user.canWrite(folder)) {
          halt(500)  // Cannot write in that folder
        } else {
          val new_folder = new File(folder, name)
          if (new_folder.exists()) {
            halt(500)
          } else {
            new_folder.mkdir()
            user.assignFile(new_folder)
          }
        } 
      }
    }
  }
  
  post("/upload") {
    withUser { user =>
      withPath("path", false) { folder =>
        val name = params("name").trim()
        val file = fileParams("contents")
        if (name.contains("..") || folder.isDirectory() || !user.canWrite(folder)) {
          halt(500)  // Cannot write in that folder
        } else {
          val new_file = new File(folder, name)
          if (new_file.exists()) {
            halt(500)
          } else {
            file.write(new_file)
            user.assignFile(new_file)
          }
        } 
      }
    }
  }
  
  notFound {  // Executed when no other route succeeds
    JNull
  }
  
  def get_subfolders(folder: File, depth: Int, user: User): List[BrowserFolder] = {    
	var subfolders = List[BrowserFolder]()
	for (file <- folder.listFiles()) {
	  if (!is_hidden(file.getName()) && file.isDirectory() && user.canRead(file)) {
	    if (depth == 1) {
	      subfolders ::= BrowserFolder(file.getName(), Nil, user.canWrite(file))
	    } else {
	      subfolders ::= BrowserFolder(file.getName(),
	          get_subfolders(file, depth-1, user),user.canWrite(file))
	    }
	  }
	}
	subfolders.sort((a, b) => a.name.compareToIgnoreCase(b.name) < 0)
  }
  
  def get_files(folder: File, user: User): List[BrowserFile] = {
    folder.listFiles().filter(f => !is_hidden(f.getName()) && user.canRead(f))
                      .map(f => get_one_file(f, user))
                      .toList
                      .sort(fileLt)
  }
  
  def get_one_file(file: File, user: User): BrowserFile = {
    Solr.getMimeType(file.getPath()) match {
      case None       => {
        // We need to get the mime type correctly
        // Tell filewatcher
        try {
          FileUtils.touch(file)
        } catch {
          case _ => { /* Nothing in special */ }
        }
        if (file.isDirectory()) {
          BrowserFile(file.getName(), "application/x-directory", null,
            file.length(), file.lastModified(), user.canWrite(file))
        } else {
          // Return unknown as mimetype
          BrowserFile(file.getName(), "unknown", null,
            file.length(), file.lastModified(), user.canWrite(file))
        }
      }
      case Some(mime) => 
        BrowserFile(file.getName(), mime,
            find_view(file.getAbsolutePath(), mime),
            file.length(), file.lastModified(), user.canWrite(file))
    }
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
