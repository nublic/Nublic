package com.nublic.app.browser.server.filewatcher.workers

import java.io.File
import scala.collection.immutable.List
import org.im4java.core.ConvertCmd
import org.im4java.core.IMOperation
import com.nublic.app.browser.server.filewatcher.DocumentWorker
import com.nublic.app.browser.server.filewatcher.FileFolder

object PlainTextWorker extends DocumentWorker {

  def supportedMimeTypes(): List[String] = List(
      "text/plain", "text/troff",
      "text/html", "text/css", "text/xml", "text/sgml",
      "text/javascript", "application/json"
    ) ++ supportedLanguages.map("text/x-" + _)
  
  def supportedLanguages(): List[String] = List(
      "java", "python", "h", "c", "fortran", "script", "pascal", "perl",
      "scheme", "lisp", "haskell", "guile", "diff", "clojure", "scala",
      "javascript", "js", "lua", "r", "ruby", "smalltak", "tex", "stex",
      "csrc", "c++src", "groovy", "httpd-php", "php", "plsql", "strc",
      "smalltalk", "yaml",
      "script.perl", "script.python", "script.scheme", "script.guile",
      "script.sh", "script.tcl", "script.tcsh", "script.zsh"
    )

  def supportedViews(): List[String] = List("txt")

  def process(file: String, folder: File): Unit = {
    // Do nothing
  }

  def getMimeTypeForView(viewName: String): String = viewName match {
    case "txt" => "text/plain"
    case _     => null
  }

  def hasView(viewName: String, file: String, mime: String): Boolean = 
    viewName == "txt" && supportedMimeTypes.contains(mime)

  def getView(viewName: String, file: String): File = new File(file)

}