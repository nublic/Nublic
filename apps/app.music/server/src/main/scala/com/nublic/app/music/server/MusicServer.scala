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
import javax.servlet.http.HttpUtils
import scala.collection.JavaConversions
import java.util.Hashtable
import org.scalatra.util.MapWithIndifferentAccess
import org.scalatra.util.MultiMapHeadView
import com.nublic.filesAndUsers.java._
import com.nublic.app.music.server.model._

class MusicServer extends ScalatraFilter with JsonSupport {
  // JsonSupport adds the ability to return JSON objects
  
  val NUBLIC_DATA_ROOT = "/var/nublic/data/"
  val THE_REST = "splat"
  val ALL_OF_SOMETHING = "all"
    
  val TRUE: LogicalBoolean = 1 === 1
  
  val watcher = new MusicActor(applicationContext)
  watcher.start()
  
  implicit val formats = Serialization.formats(NoTypeHints)
  
  var __extraParams : Option[scala.collection.immutable.Map[String, Seq[String]]] = None
  
  def _extraParams : Map[String, Seq[String]] = {
    if (__extraParams == None) {
      val ht = HttpUtils.parsePostData(request.getContentLength(),
          request.getInputStream())
      __extraParams = Some(JavaConversions.mapAsScalaMap(ht.asInstanceOf[Hashtable[String, Array[String]]]).toMap.map(f => (f._1, f._2.toSeq)))
    }
    __extraParams.get
  }
  
  protected val extraParams = new MultiMapHeadView[String, String] with MapWithIndifferentAccess[String] {
    protected def multiMap = _extraParams
  }
  
  def put2(routeMatchers: org.scalatra.RouteMatcher)(action: =>Any) = put(routeMatchers) {
    __extraParams = None
    action
  }
  
  def delete2(routeMatchers: org.scalatra.RouteMatcher)(action: =>Any) = delete(routeMatchers) {
    __extraParams = None
    action
  }
  
  def withUser(action: User => Any) : Any = {
    val user = new User(request.getRemoteUser())
    action(user)
  }
  
  def getUser(routeMatchers: org.scalatra.RouteMatcher)(action: User => Any) = get(routeMatchers) {
    withUser(action)
  }
  
  def postUser(routeMatchers: org.scalatra.RouteMatcher)(action: User => Any) = post(routeMatchers) {
    withUser(action)
  }
  
  def putUser(routeMatchers: org.scalatra.RouteMatcher)(action: User => Any) = put2(routeMatchers) {
    withUser(action)
  }
  
  def deleteUser(routeMatchers: org.scalatra.RouteMatcher)(action: User => Any) = delete2(routeMatchers) {
    withUser(action)
  }
  
  def splitThatRespectsReasonableSemantics(sep: String)(s: String) : List[String] = {
    def v = s.trim()
    if (v == "") {
      List()
    } else {
      v.split(sep).toList.map(_.trim()).filter(_ != "")
    }
  }
  
  // Collections
  // ===========
  getUser("/collections") { _ =>
    transaction {
      val colls = Database.collections.toList
      val json_colls = colls.map(c => JsonCollection(c.id, c.name))
      write(json_colls)
    }
  }
  
  putUser("/collections") { _ =>
    val name = extraParams("name")
    transaction {
      Database.collectionByName(name) match {
        case Some(c) => c.id
        case None    => {
          val newCollection = new Collection(name)
          Database.collections.insert(newCollection)
          newCollection.id.toString()
        }
      }
    }
  }
  
  deleteUser("/collections") { _ =>
    val id = Long.parseLong(extraParams("id"))
    transaction {
      Database.collections.lookup(id) match {
        case None       => { /* There is no tag like that */ }
        case Some(coll) => {
          Database.songCollections.deleteWhere(st => st.collectionId === coll.id)
          Database.collections.deleteWhere(t => t.id === coll.id)
        }
      }
    }
    halt(200)
  }
  
  putUser("/collection/:id") { _ =>
    val id = Long.parseLong(params("id"))
    val songs = splitThatRespectsReasonableSemantics(",")(params("songs")).map(Long.parseLong(_))
    transaction {
      Database.collections.lookup(id).map(coll =>
        songs.map(songId => 
          Database.songs.lookup(songId).map(song =>
            Database.songCollections.lookup(compositeKey(song.id, coll.id)) match {
              case Some(_) => { /* Already exists */ }
              case None    => {
                val songTag = new SongCollection(song.id, coll.id)
                Database.songCollections.insert(songTag)
              }
            }
          )
        )
      )
    }
    halt(200)
  }
  
  deleteUser("/collection/:id") { _ =>
    val id = Long.parseLong(params("id"))
    val songs = splitThatRespectsReasonableSemantics(",")(params("songs")).map(Long.parseLong(_))
    transaction {
      Database.collections.lookup(id).map(coll =>
        Database.songCollections.deleteWhere(x =>
          x.collectionId === coll.id and (x.songId in songs))
      )
    }
    halt(200)
  }
  
  def parse_collection_ids(collList: String): List[Collection] = {
    val ids = splitThatRespectsReasonableSemantics("/")(collList).map(Long.parseLong(_))
    inTransaction {
      val collObjects = ids.map(id => Database.collections.lookup(id))
      collObjects.filter(c => c != None).map(_.get)
    }
  }
  
  // Playlists
  // =========
  
  getUser("/playlists") { user =>
    transaction {
      val pls = Database.playlistsByUser(user.getUsername())
      val json_pls = pls.map(c => JsonPlaylist(c.id, c.name))
      write(json_pls)
    }
  }
  
  def addSongsToPlaylist(songsP: String, pl: Playlist) = {
    val db_pos = pl.songs.count(_ => true)
    val songs = splitThatRespectsReasonableSemantics(",")(songsP).map(Long.parseLong(_))
    val songs_filtered = songs.filter(sid => Database.songs.lookup(sid).isDefined)
    val song_pos = songs_filtered.zip(new Range(db_pos, db_pos + songs_filtered.length, 1))
    song_pos.map(s => {
      val newIntro = new SongPlaylist(s._1, pl.id, s._2)
      Database.songPlaylists.insert(newIntro)
    })
  }
  
  putUser("/playlists") { user =>
    val name = extraParams("name")
    val songsP = extraParams("songs")
    transaction {
      // Create playlist
      val newPlaylist = new Playlist(name, user.getUsername())
      Database.playlists.insert(newPlaylist)
      // Add songs
      addSongsToPlaylist(songsP, newPlaylist)
      // Return new playlist id
      newPlaylist.id.toString
    }
  }
  
  deleteUser("/playlists") { user =>
    val id = Long.parseLong(extraParams("id"))
    val is_of_user = transaction {
      Database.isPlaylistOfUser(id, user.getUsername())
    }
    if (!is_of_user) {
      halt(500)
    } else {
      transaction {
        Database.collections.lookup(id) match {
          case None     => { /* There is no playlist like that */ }
          case Some(pl) => {
            Database.songPlaylists.deleteWhere(sp => sp.playlistId === pl.id)
            Database.playlists.deleteWhere(p => p.id === pl.id)
          }
        }
      }
      halt(200)
    }
  }
  
  putUser("/playlist/:id") { user =>
    val id = Long.parseLong(params("id"))
    val is_of_user = transaction {
      Database.isPlaylistOfUser(id, user.getUsername())
    }
    if (!is_of_user) {
      halt(500)
    } else {
      val songsP = extraParams("songs")
      transaction {
    	  addSongsToPlaylist(songsP, Database.playlists.lookup(id).get)
      }
      halt(200)
    }
  }
  
  deleteUser("/playlist/:id") { user =>
    val id = Long.parseLong(params("id"))
    val is_of_user = transaction {
      Database.isPlaylistOfUser(id, user.getUsername())
    }
    if (!is_of_user) {
      halt(500)
    } else {
      val position = Long.parseLong(extraParams("position"))
      transaction {
        // Delete the song
        Database.songPlaylists.deleteWhere(x =>
          x.playlistId === id and x.position === position)
        // Update the position of following songs
        update(Database.songPlaylists)(sp =>
          where(sp.playlistId === id.~ and sp.position > position.~)
          set(sp.position := sp.position - 1)
        )
      }
      halt(200)
    }
  }
  
  postUser("/playlist/order/:id") { user =>
    val id = Long.parseLong(params("id"))
    val is_of_user = transaction {
      Database.isPlaylistOfUser(id, user.getUsername())
    }
    val from = Long.parseLong(params("from"))
    val to = Long.parseLong(params("to"))
    val db_count = transaction {
      Database.playlists.lookup(id).get.songs.count(_ => true)
    }
    if (!is_of_user || from < 0 || to < 0 || from >= db_count || to >= db_count) {
      halt(500)
    } else {
      transaction {
        // Delete the song
        Database.songPlaylists.deleteWhere(x =>
          x.playlistId === id and x.position === from)
        // Different cases
        if (from < to) {
          // In [from + 1, to] put one position up
          update(Database.songPlaylists)(sp =>
            where((sp.playlistId === id.~) and (sp.position > from.~) and (sp.position <= to.~))
            set(sp.position := sp.position - 1)
          )
        } else if (from > to) {
          // In [from, to - 1] put one position down
          update(Database.songPlaylists)(sp =>
            where((sp.playlistId === id.~) and (sp.position >= from.~) and (sp.position < to.~))
            set(sp.position := sp.position + 1)
          )
        } else {
          // from == to => Do nothing
        }
      }
      halt(200)
    }
  }
  
  // Artists
  // =======
  get("/artists") {
    redirect("artists/asc/0/20/")
  }
  
  get("/artists/:asc-desc/:start/:length") {
    redirect(params("length") + "/")
  }
  
  getUser("/artists/:asc/:start/:length/*") { _ =>
    // Get start and length
    val start = Integer.parseInt(params("start"))
    val length = Integer.parseInt(params("length"))
    // Get collections to browser
    val coll_param = params(THE_REST)
    val collections = parse_collection_ids(coll_param).map(_.id)
    
    // Get ascending or descending
    val asc_desc = if (params("asc") == "desc") {
      ((t: OrderByArg) => new OrderByExpression(t desc))
    } else {
      ((t: OrderByArg) => new OrderByExpression(t asc))
    }
    
    val artists = transaction {
      val query = if (coll_param.isEmpty()) {
        from(Database.artists, Database.songs)((a, s) =>
          where(a.id === s.artistId)
          groupBy(a.id)
          compute(a.name, countDistinct(s.id), countDistinct(s.albumId))
          // orderBy(asc_desc(a.name))
        )
      } else {
        from(Database.artists, Database.songs, Database.songCollections)((a, s, st) =>
          where((a.id === s.artistId) and (st.songId === s.id) and (st.collectionId in collections))
          groupBy(a.id)
          compute(a.name, countDistinct(s.id), countDistinct(s.albumId))
          // orderBy(asc_desc(a.name))
        )
      }
      (query.count(_ => true), query.page(start, length).toList)
    }
    val json_artists = artists._2.map(artist_to_json(_))
    write(JsonArtistWithCount(artists._1, json_artists))
  }
  
  getUser("/artist-info/:artistid") { _ =>
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
  
  get("/albums/:artistid/") {
    redirect("asc/0/20/")
  }
  
  get("/albums/:artistid/:asc-desc/:start/:length/*") {
    redirect(params("length") + "/")
  }
  
  getUser("/albums/:artistid/:asc/:start/:length/*") { _ =>
    // Get start and length
    val start = Integer.parseInt(params("start"))
    val length = Integer.parseInt(params("length"))
    
    // Get query about artists
    val artistid = params("artistid")
    val artist_query: Song => LogicalBoolean = if(artistid == ALL_OF_SOMETHING) {
      s: Song => 1 === 1
    } else {
      val artistN = Long.parseLong(artistid)
      s: Song => s.artistId === artistN
    }
    
    // Get collections to browser
    val coll_param = params(THE_REST)
    val collections = parse_collection_ids(coll_param).map(_.id)
    
    // Get ascending or descending
    val asc_desc = if (params("asc") == "desc") {
      ((t: OrderByArg) => new OrderByExpression(t desc))
    } else {
      ((t: OrderByArg) => new OrderByExpression(t asc))
    }
    
    val albums = transaction {
      val query = if (coll_param.isEmpty()) {
        from(Database.albums, Database.songs)((a, s) =>
          where(a.id === s.albumId and artist_query(s))
          groupBy(a.id)
          compute(a.name, countDistinct(s.id))
          orderBy(asc_desc(a.name))
        )
      } else {
        from(Database.albums, Database.songs, Database.songCollections)((a, s, st) =>
          where((a.id === s.albumId) and artist_query(s) and (st.songId === s.id) and (st.collectionId in collections))
          groupBy(a.id)
          compute(a.name, countDistinct(s.id))
          orderBy(asc_desc(a.name))
        )
      }
      (query.count(_ => true), query.page(start, length).toList)
    }
    val json_albums = albums._2.map(album_to_json(_))
    write(JsonAlbumsWithCount(albums._1, json_albums))
  }
  
  getUser("/album-info/:albumid") { _ =>
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
  
  def album_to_json(a: GroupWithMeasures[LongType, Product2[StringType, LongType]]) = {
    val artists = transaction {
      val query = from(Database.songs, Database.artists)((s, ar) =>
      	where(s.albumId === a.key and s.artistId === ar.id)
      	select(ar.id)
      )
      query.toList.distinct
    }
    JsonAlbum(a.key, a.measures._1, a.measures._2, artists)
  }
  
  // Getting images
  // ==============
  
  val ONE_MONTH_IN_MS = 30 * ONE_DAY_IN_MS
  val ONE_DAY_IN_MS = 24 * ONE_HOUR_IN_MS
  val ONE_HOUR_IN_MS = 1 * 3600 * 1000
  
  def sendImageIfNewer(place: File, last_modified: Long): Any = {
    if (!place.exists()) {
      halt(404)
    } else {
      if (last_modified == -1 || last_modified < place.lastModified()) {
        response.setContentType("image/png")
        response.setDateHeader("Last-Modified", place.lastModified())
        response.setDateHeader("Expires", place.lastModified() + ONE_DAY_IN_MS)
        
        place
      } else {
        halt(304)
      }
    }
  }
  
  getUser("/artist-art/:artistid.png") { _ =>
    val artist_id = Long.parseLong(params("artistid"))
    val last_modified = request.getDateHeader("If-Modified-Since")
    val place = new File(MusicFolder.getArtistFolder(artist_id), MusicFolder.THUMBNAIL_FILENAME)
    
    sendImageIfNewer(place, last_modified)
  }
  
  getUser("/album-art/:albumid.png") { _ =>
    val album_id = Long.parseLong(params("albumid"))
    val last_modified = request.getDateHeader("If-Modified-Since")
    val place = new File(MusicFolder.getAlbumFolder(album_id), MusicFolder.THUMBNAIL_FILENAME)
    
    sendImageIfNewer(place, last_modified)
  }
  
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
  
  get("/songs/:artistid/:albumid/:order/:asc/:start/:length") {
    redirect(params("length") + "/")
  }
  
  getUser("/songs/:artistid/:albumid/:order/:asc/:start/:length/*") { _ =>
    // Get start and length
    val start = Integer.parseInt(params("start"))
    val length = Integer.parseInt(params("length"))
    // Get query about artists
    val artistid = params("artistid")
    val artist_query: Song => LogicalBoolean = if(artistid == ALL_OF_SOMETHING) {
      s: Song => TRUE
    } else {
      val artistN = Long.parseLong(artistid)
      s: Song => s.artistId === artistN
    }
    // Get query about albums
    val albumid = params("albumid")
    val album_query: Song => LogicalBoolean = if(albumid == ALL_OF_SOMETHING) {
      s: Song => TRUE
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
    val coll_param = params(THE_REST)
    val collections = parse_collection_ids(coll_param).map(_.id)
    val songs = transaction {
      val query = if (coll_param.isEmpty()) {
        from(Database.songs, Database.artists, Database.albums)((s, ar, ab) =>
          order(s, ar, ab)(
            where(s.artistId === ar.id and s.albumId === ab.id and artist_query(s) and album_query(s))
            select(s))
        )
      } else {
        from(Database.songs, Database.artists, Database.albums, Database.songCollections)((s, ar, ab, st) =>
          order(s, ar, ab)(
            where(s.artistId === ar.id and s.albumId === ab.id and artist_query(s) and album_query(s)
                  and (st.songId === s.id) and (st.collectionId in collections))
            select(s))
        )
      }
      (query.count(_ => true), query.page(start, length).toList)
    }
    val json_songs = songs._2.map(song_to_json(_))
    write(JsonSongsWithCount(songs._1, json_songs))
  }
  
  getUser("/playlist/:id/:order/:asc/:start/:length") { user =>
    // Get playlist id
    val id = Long.parseLong(params("id"))
    // Cehck if user has access
    val is_of_user = transaction {
      Database.isPlaylistOfUser(id, user.getUsername())
    }
    if (!is_of_user) {
      halt(500)
    } else {
      // Get start and length
      val start = Integer.parseInt(params("start"))
      val length = Integer.parseInt(params("length"))
      // Get ascending or descending
      val asc_desc = if (params("asc") == "desc") {
        ((t: OrderByArg) => new OrderByExpression(t desc))
      } else {
        ((t: OrderByArg) => new OrderByExpression(t asc))
      }
      // Get order
      val order: (SongPlaylist, Song, Artist, Album) => SelectState[Song] => QueryYield[Song] = params("order") match {
        case "playlist" => ( (sp: SongPlaylist, s: Song, ar: Artist, ab: Album) => q =>
          (q.orderBy(asc_desc(sp.position))) )
        case "alpha" => ( (_: SongPlaylist, s: Song, ar: Artist, ab: Album) => q =>
          (q.orderBy(asc_desc(s.title))) )
        case "album" => ( (_: SongPlaylist, s: Song, ar: Artist, ab: Album) => q =>
          (q.orderBy(asc_desc(ab.name), asc_desc(s.disc_no), asc_desc(s.track))) )
        case "artist_alpha" => ( (_: SongPlaylist, s: Song, ar: Artist, ab: Album) => q =>
          (q.orderBy(asc_desc(ar.name), asc_desc(s.title))) )
        case "artist_album" => ( (_: SongPlaylist, s: Song, ar: Artist, ab: Album) => q =>
          (q.orderBy(asc_desc(ar.name), asc_desc(ab.name), asc_desc(s.disc_no), asc_desc(s.track))) )
      }
      
      val songs = transaction {
        val query = 
          from(Database.songPlaylists, Database.songs, Database.artists, Database.albums)((sp, s, ar, ab) =>
            order(sp, s, ar, ab)(
              where(sp.playlistId === id and sp.songId === s.id and s.artistId === ar.id and s.albumId === ab.id)
              select(s))
          )
        (query.count(_ => true), query.page(start, length).toList)
      }
      val json_songs = songs._2.map(song_to_json(_))
      write(JsonSongsWithCount(songs._1, json_songs))
    }
  }
  
  getUser("/song-info/:songid") { _ =>
    val song_id = Long.parseLong(params("songid"))
    transaction {
      Database.songs.lookup(song_id) match {
        case None    => halt(404)
        case Some(s) => write(song_to_json(s))
      }
    }
  }
  
  def song_to_json(s: Song) =
	JsonSong(s.id, s.title, s.artistId, s.albumId, s.length, s.disc_no, s.track)
  
  // Song files
  // ==========
  
  getUser("/raw/:songid") { _ =>
    val song_id = Long.parseLong(params("songid"))
    val song = transaction { Database.songs.lookup(song_id) }
    song match {
      case None    => halt(404)
      case Some(s) => new File(s.file)
    }
  }
  
  getUser("/view/:songid.mp3") { _ =>
    val song_id = Long.parseLong(params("songid"))
    val song = transaction { Database.songs.lookup(song_id) }
    song match {
      case None    => halt(404)
      case Some(s) => {
        val mp3 = BrowserFolder.getMp3(s.file)
        if (!mp3.exists()) {
          halt(404)
        } else {
          response.setContentType("audio/mpeg")
          mp3
        }
      }
    }
  }
  
  notFound {  // Executed when no other route succeeds
    JNull
  }
}
