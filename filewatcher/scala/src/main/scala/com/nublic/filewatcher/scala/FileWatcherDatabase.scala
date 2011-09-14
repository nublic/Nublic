package com.nublic.filewatcher.scala

import org.squeryl.KeyedEntity
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.Schema

class FileChangeInDatabase(val id: Long, val ty: String, val pathname: String,
    val src_pathname: String, val isdir: Boolean, var processed: Integer)
    extends KeyedEntity[Long] {
  def this() = this(0, "", "", "", false, 0)
  def this(ty: String, pathname: String, isdir: Boolean) = 
    this(0, ty, pathname, "", isdir, 0)
  def this(ty: String, pathname: String, src_pathname: String, isdir: Boolean) = 
    this(0, ty, pathname, src_pathname, isdir, 0)
  
  def toFileChange = FileChange.parse(ty, pathname, src_pathname, isdir)
}

object FileWatcherDatabase extends Schema {
  val files = table[FileChangeInDatabase]("files")
}