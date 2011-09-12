package com.nublic.app.browser.server.filewatcher

abstract class FileChange(filename: String, isdir: Boolean) {
  def getFileName: String = filename
  def isDirectory: Boolean = isdir
}

case class Created(filename: String, isdir: Boolean) extends FileChange(filename, isdir)
case class Deleted(filename: String, isdir: Boolean) extends FileChange(filename, isdir)
case class Modified(filename: String, isdir: Boolean) extends FileChange(filename, isdir)
case class AttribsChanged(filename: String, isdir: Boolean) extends FileChange(filename, isdir)
case class Moved(from: String, to: String, isdir: Boolean) extends FileChange(to, isdir)

object FileChange {
  def parse(ty: String, pathname: String, src_pathname : String, is_dir: Boolean) = ty match {
    case "create" => Created(pathname, is_dir)
    case "delete" => Deleted(pathname, is_dir)
    case "modify" => Modified(pathname, is_dir)
    case "attrib" => AttribsChanged(pathname, is_dir)
    case "move"   => Moved(src_pathname, pathname, is_dir)
  }
}
