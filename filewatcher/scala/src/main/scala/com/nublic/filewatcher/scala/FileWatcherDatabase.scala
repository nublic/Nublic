package com.nublic.filewatcher.scala

import org.squeryl.KeyedEntity
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.Schema

class FileChangeInDatabase(val id: Long, val ty: String, val pathname: String,
    val src_pathname: String, val context: String, val isdir: Short, var processed: Integer)
    extends KeyedEntity[Long] {
  def this() = this(0, "", "", "", "", 0, 0)
  def this(ty: String, pathname: String, context: String, isdir: Boolean) = 
    this(0, ty, pathname, "", context, if (isdir) { 1 } else { 0 }, 0)
  def this(ty: String, pathname: String, src_pathname: String, context: String, isdir: Boolean) = 
    this(0, ty, pathname, src_pathname, context, if (isdir) { 1 } else { 0 }, 0)
  
  def toFileChange = FileChange.parse(ty, pathname, src_pathname, context, isdir != 0)
}

object FileWatcherDatabase extends Schema {
  val files = table[FileChangeInDatabase]("files")
  
  on(files)(f => declare(
    f.pathname     is (dbType("varchar(32672)")),
    f.src_pathname is (dbType("varchar(32672)")),
    f.context      is (dbType("varchar(32672)"))
  ))
}