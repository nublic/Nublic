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
import java.util.Date
import scala.util.Random
import org.apache.commons.lang3.StringUtils

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
  
  val ONE_MONTH_IN_MS = 30 * ONE_DAY_IN_MS
  val ONE_DAY_IN_MS = 24 * ONE_HOUR_IN_MS
  val ONE_HOUR_IN_MS = 1 * 3600 * 1000
  
  get("/thumbnail/*") {
    withUserAndRestPath(false) { user => file =>
      if (!file.exists() || !user.canRead(file)) {
        halt(404)
      } else {
        val last_modified = request.getDateHeader("If-Modified-Since")
        if (last_modified == -1 || last_modified < file.lastModified()) {
          response.setContentType("image/png")
          response.setDateHeader("Last-Modified", file.lastModified())
          response.setDateHeader("Expires", file.lastModified() + ONE_HOUR_IN_MS)
          
          val thumb_file = FileFolder.getThumbnail(file.getPath())
          if (thumb_file.exists()) {
            thumb_file
          } else {
            Solr.getMimeType(file.getPath()) match {
              case None => {
                if (file.isDirectory()) {
                  ImageDatabase.getImageBytes(ImageDatabase.DIRECTORY_MIME)
                } else {
                  ImageDatabase.getImageBytes("unknown")
                }
              }
              case Some(mime) => {
                ImageDatabase.getImageBytes(mime)
              }
            }
          }
        } else {
          halt(304)
        }
      }
    }
  }
  
  get("/generic-thumbnail/*") {
    val last_modified = request.getDateHeader("If-Modified-Since")
    if (last_modified == -1 || last_modified < ImageDatabase.LAST_MODIFIED_DATE.getTime()) {
      val name = URIUtil.decode(params(THE_REST))
      response.setContentType("image/png")
      response.setDateHeader("Last-Modified", ImageDatabase.LAST_MODIFIED_DATE.getTime())
      response.setDateHeader("Expires", (new Date()).getTime() + ONE_MONTH_IN_MS)
      ImageDatabase.getImageBytes(name)
    } else {
      halt(304)
    }
  }
  
  def getNextFilenameFor(f: File) : File = {
    if (!f.exists()) {
      f
    } else {
      val parent = f.getParentFile()
      val name = f.getName()
      val splitted_name = name.split('.').toList
      val original_first = splitted_name.head
      val original_tail = splitted_name.tail
      
      var i: Long = 0
      var newF: File = null
      do {
        i = i + 1
        var end_name_s: String = original_tail.foldLeft(original_first + " (" + i + ")")((a, b) => a + "." + b)
        newF = new File(parent, end_name_s)
      } while(newF.exists())
      
      newF
    }
  }
  
  post("/rename") {
    withUser { user =>
      withPath("from", false) { from_path =>
        withPath("to", false) { to_path =>
          if (!user.canWrite(from_path) || !user.canWrite(to_path.getParent())) {
            halt(403)
          } else {
            val real_to_path = getNextFilenameFor(to_path)
            if (from_path.isDirectory()) {
              FileUtils.moveDirectory(from_path, real_to_path)
            } else {
    	      FileUtils.moveFile(from_path, real_to_path)
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
          from_paths.map(f => do_move2(f, to_path, user))
          halt(200)
        }
      }
    }
  }
  
  def do_move2(file: File, target: File, user: User): Unit = {
    if (user.canWrite(target)) {
      if (file.isDirectory()) {
        val final_dir = new File(target, file.getName())
        if (!final_dir.exists()) {
          final_dir.mkdir()
        }
        // Merge contents
        for (child <- file.listFiles) {
          do_move2(child, final_dir, user)
        }
        if (file.listFiles.length == 0) {
          file.delete
        }
      } else {
        val final_file = getNextFilenameFor(new File(target, file.getName()))
        FileUtils.moveFile(file, final_file)
      }
    }
  }
  
  post("/copy") {
    withUser { user =>
      withMultiplePaths("files") { from_paths =>
        withPath("target", false) { to_path =>
          from_paths.map(f => do_copy2(f, to_path, user))
          halt(200)
        }
      }
    }
  }
  
  def do_copy2(file: File, target: File, user: User): Unit = {
    if (user.canWrite(target)) {
      if (file.isDirectory()) {
        val final_dir = new File(target, file.getName())
        if (!final_dir.exists()) {
          final_dir.mkdir()
        }
        do_chown(final_dir, user)
        // Merge contents
        for (child <- file.listFiles) {
          do_copy2(child, final_dir, user)
        }
      } else {
        val final_file = getNextFilenameFor(new File(target, file.getName()))
        FileUtils.copyFile(file, final_file)
        do_chown(final_file, user)
      }
    }
  }
  
  def do_chown(file: File, user: User) = {
    user.assignFile(file, true)
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
        if (name.contains("..")) {
          throw new Exception("name contains ..")
        } else if (!folder.isDirectory()) {
          throw new Exception("folder is not a directory")
        } else if (!user.canWrite(folder)) {
          throw new Exception("user cannot write in that folder")
        } else {
          val new_folder = getNextFilenameFor(new File(folder, name))
          new_folder.mkdir()
          user.assignFile(new_folder, true)
          halt(200)
        } 
      }
    }
  }
  
  def doUpload(user : User) = {
    withPath("path", false) { folder =>
      val name = params("name").trim()
      val file = fileParams("Filedata")
      if (name.contains("..")) {
        throw new Exception("name contains ..")
      } else if (!folder.isDirectory()) {
        throw new Exception("folder is not a directory")
      } else if (!user.canWrite(folder)) {
        throw new Exception("user cannot write in that folder")
      } else {
        val new_file = getNextFilenameFor(new File(folder, name))
        file.write(new_file)
        user.assignFile(new_file, true)
        halt(200)
      } 
    }
  }
  
  post("/upload") {
    withUser(doUpload)
  }
  
  // We use upload in two phases to allow uploding via Flash
  // For that, we use an authenticated "phase1" to generate an unique id
  // and then we use that id to upload one file in "phase2"
  
  var current_upload_keys = scala.collection.mutable.Map[Long, Tuple2[User, Long]]()
  
  def prune_old_upload_keys = {
    // Prune those older than 5 minutes
    val end_time = (new Date()).getTime() - 5 * 3600 * 1000 /* 5 hours */
    current_upload_keys = current_upload_keys.filter(kv => kv._2._2 > end_time)
  }
  
  get("/upload-in-phases/phase1") {
    withUser { user => 
      // Generate new id for new uploading
      val upload_key_id = Random.nextLong().abs
      current_upload_keys += upload_key_id -> ( user, (new Date()).getTime() )
      upload_key_id.toString()
    }
  }
  
  post("/upload-in-phases/phase2/:id") {
    val id = java.lang.Long.parseLong(params("id"))
    // Prune old elements
    prune_old_upload_keys
    // Try to find ours
    current_upload_keys.get(id) match {
      case None    => halt(500)
      case Some(v) => {
        doUpload(v._1)
        current_upload_keys -= id
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
            file.length(), file.lastModified(), user.canWrite(file), false)
        } else {
          // Return unknown as mimetype
          BrowserFile(file.getName(), "unknown", null,
            file.length(), file.lastModified(), user.canWrite(file), false)
        }
      }
      case Some(mime) => {
        val thumb_file = FileFolder.getThumbnail(file.getPath())
        BrowserFile(file.getName(), mime,
            find_view(file.getAbsolutePath(), mime),
            file.length(), file.lastModified(), user.canWrite(file),
            thumb_file.exists())
      }
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
