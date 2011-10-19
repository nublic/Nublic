package com.nublic.filewatcher.scala

abstract class FileChange(filename: String, context: String, isdir: Boolean, ty: String) {
  def getFileName: String = filename
  def getContext: String = context
  def getType: String = ty
  def isDirectory: Boolean = isdir
}

case class Created(filename: String, context: String, isdir: Boolean) extends FileChange(filename, context, isdir, "create")
case class Deleted(filename: String, context: String, isdir: Boolean) extends FileChange(filename, context, isdir, "delete")
case class Modified(filename: String, context: String, isdir: Boolean) extends FileChange(filename, context, isdir, "modify")
case class AttribsChanged(filename: String, context: String, isdir: Boolean) extends FileChange(filename, context, isdir, "attrib")
case class Moved(from: String, to: String, context: String, isdir: Boolean) extends FileChange(to, context, isdir, "move")
case class ScanRepeated(filename: String, context: String, isdir: Boolean) extends FileChange(filename, context, isdir, "repeat")

object FileChange {
  def parse(ty: String, pathname: String, src_pathname : String, context: String, is_dir: Boolean): FileChange = ty match {
    case "create" => Created(pathname, context, is_dir)
    case "delete" => Deleted(pathname, context, is_dir)
    case "modify" => Modified(pathname, context, is_dir)
    case "attrib" => AttribsChanged(pathname, context, is_dir)
    case "move"   => Moved(src_pathname, pathname, context, is_dir)
    case "repeat" => ScanRepeated(pathname, context, is_dir)
    case _        => throw new IllegalArgumentException()
  }
}

case class ForwardFileChange(int: Long, change: FileChange)
case class BackFileChange(processor: String, int: Long, change: FileChange)
