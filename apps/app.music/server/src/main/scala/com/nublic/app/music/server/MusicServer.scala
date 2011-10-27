package com.nublic.app.music.server

import java.io.File
import java.lang.Long
import org.scalatra._
import org.scalatra.liftjson.JsonSupport
import net.liftweb.json._
import net.liftweb.json.Serialization.{read, write}
import javax.servlet.http.HttpServlet
import com.nublic.app.music.server.filewatcher.MusicActor
import com.nublic.app.music.server.model._
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl.GroupWithMeasures
import org.squeryl.dsl.ast.LogicalBoolean
import org.squeryl.dsl.ast.OrderByArg
import org.squeryl.dsl.ast.OrderByExpression
import org.squeryl.dsl.boilerplate.OrderBySignatures
import org.squeryl.dsl.QueryYield
import org.squeryl.dsl.fsm.BaseQueryYield
import org.squeryl.dsl.ast.ExpressionNode
import org.squeryl.dsl.fsm.SelectState

class MusicServer extends ScalatraFilter with JsonSupport {
  // JsonSupport adds the ability to return JSON objects
  
  val NUBLIC_DATA_ROOT = "/var/nublic/data/"
  val THE_REST = "splat"
  val ALL_OF_SOMETHING = "all"
  
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
  
  def parse_tags(tagList: String): List[Tag] = {
    val splitString = tagList.split("/").toList
    inTransaction {
      val tagObjects = splitString.map(name => Database.tagByName(name))
      tagObjects.filter(t => t != None).map(_.get)
    }
  }
  
  // Artists
  // =======
  get("/artists") {
    redirect("artists/")
  }
  
  get("/artists/*") {
    val tag_param = params(THE_REST)
    val tags = parse_tags(tag_param).map(_.id)
    val artists = transaction {
      val query = if (tag_param.isEmpty()) {
        from(Database.artists, Database.songs)((a, s) =>
          where(a.id === s.artistId)
          groupBy(a.id)
          compute(a.name, countDistinct(s.id), countDistinct(s.albumId))
        )
      } else {
        from(Database.artists, Database.songs, Database.songTags)((a, s, st) =>
          where((a.id === s.artistId) and (st.songId === s.id) and (st.tagId in tags))
          groupBy(a.id)
          compute(a.name, countDistinct(s.id), countDistinct(s.albumId))
        )
      }
      query.toList
    }
    val json_artists = artists.map(artist_to_json(_))
    write(json_artists)
  }
  
  get("/artist-info/:artistid") {
    val artist_id = Long.parseLong(params("artistid"))
    val artist = transaction {
      val query = from(Database.artists, Database.songs)((a, s) =>
        where(a.id === s.artistId and a.id === artist_id)
        groupBy(a.id)
        compute(a.name, countDistinct(s.id), countDistinct(s.albumId))
      )
      Database.maybe(query)
    }
    artist match {
      case None    => halt(404)
      case Some(a) => write(artist_to_json(a))
    }
  }
  
  def artist_to_json(a: GroupWithMeasures[LongType, Product3[StringType, LongType, LongType]]) =
    JsonArtist(a.key, a.measures._1, a.measures._2, a.measures._3)
  
  // Albums
  // ======
  get("/albums") {
    redirect("albums/" + ALL_OF_SOMETHING +  "/")
  }
  
  get("/albums/:artistid") {
    redirect(params("artistid") + "/")
  }
  
  get("/albums/:artistid/*") {
    // Get query about artists
    val artistid = params("artistid")
    val artist_query: Song => LogicalBoolean = if(artistid == ALL_OF_SOMETHING) {
      s: Song => true
    } else {
      val artistN = Long.parseLong(artistid)
      s: Song => s.artistId === artistN
    }
    // Get tags query
    val tag_param = params(THE_REST)
    val tags = parse_tags(tag_param).map(_.id)
    val albums = transaction {
      val query = if (tag_param.isEmpty()) {
        from(Database.albums, Database.songs)((a, s) =>
          where(a.id === s.albumId and artist_query(s))
          groupBy(a.id)
          compute(a.name, countDistinct(s.id))
        )
      } else {
        from(Database.albums, Database.songs, Database.songTags)((a, s, st) =>
          where((a.id === s.albumId) and artist_query(s) and (st.songId === s.id) and (st.tagId in tags))
          groupBy(a.id)
          compute(a.name, countDistinct(s.id))
        )
      }
      query.toList
    }
    val json_albums = albums.map(album_to_json(_))
    write(json_albums)
  }
  
  get("/album-info/:albumid") {
    val album_id = Long.parseLong(params("albumid"))
    val album = transaction {
      val query = from(Database.albums, Database.songs)((a, s) =>
        where(a.id === s.albumId and a.id === album_id)
        groupBy(a.id)
        compute(a.name, countDistinct(s.id))
      )
      Database.maybe(query)
    }
    album match {
      case None    => halt(404)
      case Some(a) => write(album_to_json(a))
    }
  }
  
  def album_to_json(a: GroupWithMeasures[LongType, Product2[StringType, LongType]]) =
    JsonAlbum(a.key, a.measures._1, a.measures._2)
  
  // Songs
  // ======
  get("/songs") {
    redirect("songs/" + ALL_OF_SOMETHING + "/" + ALL_OF_SOMETHING + "/")
  }
  
  get("/songs/:artistid") {
    redirect(params("artistid") + "/" + ALL_OF_SOMETHING + "/")
  }
  
  get("/songs/:artistid/:albumid") {
    redirect(params("albumid") + "/")
  }
  
  get("/songs/:artistid/:albumid/") {
    // Typical setup: 20 first elements in alphabetical order
    redirect("alpha/asc/0/20/")
  }
  
  get("/songs/:artistid/:albumid/:order/:asc/:start/:length/*") {
    // Get query about artists
    val artistid = params("artistid")
    val artist_query: Song => LogicalBoolean = if(artistid == ALL_OF_SOMETHING) {
      s: Song => true
    } else {
      val artistN = Long.parseLong(artistid)
      s: Song => s.artistId === artistN
    }
    // Get query about albums
    val albumid = params("albumid")
    val album_query: Song => LogicalBoolean = if(albumid == ALL_OF_SOMETHING) {
      s: Song => true
    } else {
      val albumN = Long.parseLong(albumid)
      s: Song => s.albumId === albumN
    }
    // Get ascending or descending
    val asc_desc = if (params("asc") == "desc") {
      ((t: OrderByArg) => new OrderByExpression(t desc))
    } else {
      ((t: OrderByArg) => new OrderByExpression(t asc))
    }
    // Get order
    val order: (Song, Artist, Album) => SelectState[Song] => QueryYield[Song] = params("order") match {
      case "alpha" => ( (s: Song, ar: Artist, ab: Album) => q =>
        (q.orderBy(asc_desc(s.title))) )
      case "album" => ( (s: Song, ar: Artist, ab: Album) => q =>
        (q.orderBy(asc_desc(ab.name), asc_desc(s.disc_no), asc_desc(s.track))) )
      case "artist_alpha" => ( (s: Song, ar: Artist, ab: Album) => q =>
        (q.orderBy(asc_desc(ar.name), asc_desc(s.title))) )
      case "artist_album" => ( (s: Song, ar: Artist, ab: Album) => q =>
        (q.orderBy(asc_desc(ar.name), asc_desc(ab.name), asc_desc(s.disc_no), asc_desc(s.track))) )
    }
    // Get tags query
    val tag_param = params(THE_REST)
    val tags = parse_tags(tag_param).map(_.id)
    val songs = transaction {
      val query = if (tag_param.isEmpty()) {
        from(Database.songs, Database.artists, Database.albums)((s, ar, ab) =>
          order(s, ar, ab)(
            where(s.artistId === ar.id and s.albumId === ab.id and artist_query(s) and album_query(s))
            select(s))
        )
      } else {
        from(Database.songs, Database.artists, Database.albums, Database.songTags)((s, ar, ab, st) =>
          order(s, ar, ab)(
            where(s.artistId === ar.id and s.albumId === ab.id and artist_query(s) and album_query(s)
                  and (st.songId === s.id) and (st.tagId in tags))
            select(s))
        )
      }
      query.toList
    }
    val json_songs = songs.map(song_to_json(_))
    write(json_songs)
  }
  
  get("/song-info/:songid") {
    val song_id = Long.parseLong(params("songid"))
    transaction {
      Database.songs.lookup(song_id) match {
        case None    => halt(404)
        case Some(s) => write(song_to_json(s))
      }
    }
  }
  
  def song_to_json(s: Song) =
    JsonSong(s.id, s.title, s.artistId, s.albumId, s.disc_no, s.track)
  
  notFound {  // Executed when no other route succeeds
    JNull
  }
}
