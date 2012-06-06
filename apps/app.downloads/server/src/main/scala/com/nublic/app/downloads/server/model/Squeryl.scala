package com.nublic.app.downloads.server.model

import org.squeryl.Query
import org.squeryl.Schema
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.KeyedEntity
import org.squeryl.Table

class Download(val id: Long, val source: String, var target: String, val uid: Long) extends KeyedEntity[Long] {
  def this() = this(0, "", "", 0)
}

object Database extends Schema {
  val downloads = table[Download]
  
  on(downloads)(f => declare(
    f.source is (dbType("varchar(32672)")),
    f.target is (dbType("varchar(32672)"))
  ))

  def downloadBySource(source: String) = maybe(downloads.where(d => d.source === source))
  def downloadByTarget(target: String) = maybe(downloads.where(d => d.target === target))
  
  def maybe[R](q: Query[R]): Option[R] = q.headOption
}
