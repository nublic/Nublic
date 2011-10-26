package com.nublic.app.music.server

import java.io.File
import org.scalatra._
import org.scalatra.liftjson.JsonSupport
import net.liftweb.json._
import net.liftweb.json.Serialization.{read, write}
import javax.servlet.http.HttpServlet
import org.squeryl.PrimitiveTypeMode._
import com.nublic.app.music.server.filewatcher.MusicActor
import com.nublic.app.music.server.model._
import java.lang.Long

class MusicServer extends ScalatraFilter with JsonSupport {
  // JsonSupport adds the ability to return JSON objects
  
  val NUBLIC_DATA_ROOT = "/var/nublic/data/"
  val THE_REST = "splat"
  
  val watcher = new MusicActor()
  watcher.start()
  
  implicit val formats = Serialization.formats(NoTypeHints)
  
  // Tags
  // ====
  
  get("/tags") {
    transaction {
      val tagsQuery = from(Database.tags)(t => select(t.name))
      val tags = tagsQuery.toList
      write(tags)
    }
  }
  
  put("/tag/:name") {
    val name = params("name")
    transaction {
      Database.tagByName(name) match {
        case Some(_) => { /* It's already there */ }
        case None    => {
          val newTag = new Tag(name)
          Database.tags.insert(newTag)
        }
      }
    }
    halt(200)
  }
  
  delete("/tag/:name") {
    val name = params("name")
    transaction {
      Database.tagByName(name) match {
        case None      => { /* There is no tag like that */ }
        case Some(tag) => Database.tags.deleteWhere(t => t.id === tag.id)
      }
    }
    halt(200)
  }
  
  put("/tag/:name/:song-id") {
    val name = params("name")
    val songId = Long.parseLong(params("song-id"))
    transaction {
      Database.tagByName(name).map(tag =>
        Database.songs.lookup(songId).map(song =>
          Database.songTags.lookup(compositeKey(song.id, tag.id)) match {
            case Some(_) => { /* Already exists */ }
            case None    => {
              val songTag = new SongTag(song.id, tag.id)
              Database.songTags.insert(songTag)
            }
          }
        )
      )
    }
    halt(200)
  }
  
  delete("/tag/:name/:song-id") {
    val name = params("name")
    val songId = Long.parseLong(params("song-id"))
    transaction {
      Database.tagByName(name).map(tag =>
        Database.songs.lookup(songId).map(song =>
          Database.songTags.lookup(compositeKey(song.id, tag.id)) match {
            case None     => { /* Already deleted */ }
            case Some(st) => Database.songTags.deleteWhere(x =>
              x.songId === st.songId and x.tagId === st.tagId)
          }
        )
      )
    }
    halt(200)
  }
  
  notFound {  // Executed when no other route succeeds
    JNull
  }
}
