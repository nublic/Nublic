package com.nublic.app.browser.server.filewatcher

abstract class FileChange(filename: String, isdir: Boolean, ty: String) {
  def getFileName: String = filename
  def getType: String = ty
  def isDirectory: Boolean = isdir
}

case class Created(filename: String, isdir: Boolean) extends FileChange(filename, isdir, "create")
case class Deleted(filename: String, isdir: Boolean) extends FileChange(filename, isdir, "delete")
case class Modified(filename: String, isdir: Boolean) extends FileChange(filename, isdir, "modify")
case class AttribsChanged(filename: String, isdir: Boolean) extends FileChange(filename, isdir, "attrib")
case class Moved(from: String, to: String, isdir: Boolean) extends FileChange(to, isdir, "move")
case class ScanRepeated(filename: String, isdir: Boolean) extends FileChange(filename, isdir, "repeat")

object FileChange {
  def parse(ty: String, pathname: String, src_pathname : String, is_dir: Boolean): FileChange = ty match {
    case "create" => Created(pathname, is_dir)
    case "delete" => Deleted(pathname, is_dir)
    case "modify" => Modified(pathname, is_dir)
    case "attrib" => AttribsChanged(pathname, is_dir)
    case "move"   => Moved(src_pathname, pathname, is_dir)
    case "repeat" => ScanRepeated(pathname, is_dir)
    case _        => throw new IllegalArgumentException()
  }
}

case class ForwardFileChange(int: Long, change: FileChange)
case class BackFileChange(processor: String, int: Long, change: FileChange)
