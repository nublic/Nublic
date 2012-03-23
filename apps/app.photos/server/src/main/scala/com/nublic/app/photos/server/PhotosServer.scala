package com.nublic.app.photos.server

import java.io.File
import java.lang.Long
import org.scalatra._
import org.scalatra.liftjson.JsonSupport
import net.liftweb.json._
import net.liftweb.json.Serialization.{read, write}
import javax.servlet.http.HttpServlet
import com.nublic.app.photos.server.filewatcher.PhotoActor
import com.nublic.app.photos.server.model._
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

class MusicServer extends ScalatraFilter with JsonSupport {
  // JsonSupport adds the ability to return JSON objects
  
  val NUBLIC_DATA_ROOT = "/var/nublic/data/"
  val THE_REST = "splat"
  val ALL_OF_SOMETHING = "all"
  
  val watcher = new PhotoActor(applicationContext)
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
  getUser("/albums") { _ =>
    transaction {
      val albums = Database.albums.toList
      val json_albums = albums.map(c => JsonAlbum(c.id, c.name))
      write(json_albums)
    }
  }
  
  putUser("/albums") { _ =>
    val name = extraParams("name")
    transaction {
      Database.albumByName(name) match {
        case Some(c) => c.id
        case None    => {
          val newAlbum = new Album(name)
          Database.albums.insert(new Album)
          newAlbum.id.toString()
        }
      }
    }
  }
  
  deleteUser("/albums") { _ =>
    val id = Long.parseLong(extraParams("id"))
    transaction {
      Database.albums.lookup(id) match {
        case None       => { /* There is no tag like that */ }
        case Some(coll) => {
          Database.photoAlbums.deleteWhere(st => st.albumId === coll.id)
          Database.albums.deleteWhere(t => t.id === coll.id)
        }
      }
    }
    halt(200)
  }
  
  putUser("/album/:id") { _ =>
    val id = Long.parseLong(params("id"))
    val photos = splitThatRespectsReasonableSemantics(",")(params("photos")).map(Long.parseLong(_))
    transaction {
      Database.albums.lookup(id).map(album =>
        photos.map(photoId => 
          Database.photos.lookup(photoId).map(photo =>
            Database.photoAlbums.lookup(compositeKey(photo.id, album.id)) match {
              case Some(_) => { /* Already exists */ }
              case None    => {
                val photoAlbum = new PhotoAlbum(photo.id, album.id)
                Database.photoAlbums.insert(photoAlbum)
              }
            }
          )
        )
      )
    }
    halt(200)
  }
  
  deleteUser("/album/:id") { _ =>
    val id = Long.parseLong(params("id"))
    val photos = splitThatRespectsReasonableSemantics(",")(params("photos")).map(Long.parseLong(_))
    transaction {
      Database.albums.lookup(id).map(album =>
        Database.photoAlbums.deleteWhere(x =>
          x.albumId === album.id and (x.photoId in photos))
      )
    }
    halt(200)
  }
  
  def parse_album_ids(albumList: String): List[Album] = {
    val ids = splitThatRespectsReasonableSemantics("/")(albumList).map(Long.parseLong(_))
    inTransaction {
      val albumObjects = ids.map(id => Database.albums.lookup(id))
      albumObjects.filter(c => c != None).map(_.get)
    }
  }
  
  // Photos
  // ======
  get("/photos") {
    redirect("photos/")
  }
  
  get("/photos/") {
    // Typical setup: 20 first elements in alphabetical order
    redirect("alpha/asc/0/20/")
  }
  
  get("/songs/:order/:asc/:start/:length") {
    redirect(params("length") + "/")
  }
  
  getUser("/songs/:order/:asc/:start/:length/*") { _ =>
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
    val order: Photo => SelectState[Photo] => QueryYield[Photo] = params("order") match {
      case "title" => ( (p: Photo) => q =>
        (q.orderBy(asc_desc(p.title))) )
      case "date" => ( (p: Photo) => q =>
        (q.orderBy(asc_desc(p.date), asc_desc(p.title))) )
    }
    // Get tags query
    val album_param = params(THE_REST)
    val albums = parse_album_ids(album_param).map(_.id)
    val photos = transaction {
      val query = if (album_param.isEmpty()) {
        from(Database.photos)(p =>
          order(p)(select(p))
        )
      } else {
        from(Database.photos, Database.photoAlbums)((p, pa) =>
          order(p)(
            where((pa.photoId === p.id) and (pa.albumId in albums))
            select(p))
        )
      }
      (query.count(_ => true), query.page(start, length).toList)
    }
    val json_photos = photos._2.map(photo_to_json(_))
    write(JsonPhotosWithCount(photos._1, json_photos))
  }
  
  def photo_to_json(p: Photo) =
	JsonPhoto(p.id, p.title, p.date)
	
  // Photo properties
  // ================
	
  getUser("/photo-info/:photoid") { _ =>
    val photo_id = Long.parseLong(params("photoid"))
    transaction {
      Database.photos.lookup(photo_id) match {
        case None    => halt(404)
        case Some(p) => write(photo_to_json(p))
      }
    }
  }
  
  postUser("/photo-title/:photoid") { _ =>
    val photo_id = Long.parseLong(params("photoid"))
    val title = params("title")
    transaction {
      update(Database.photos)(p =>
        where(p.id === photo_id.~)
        set(p.title := title)
      )
    }
  }
  
  // Image files
  // ===========
  
  getUser("/raw/:photoid") { _ =>
    val photo_id = Long.parseLong(params("photoid"))
    val photo = transaction { Database.photos.lookup(photo_id) }
    photo match {
      case None    => halt(404)
      case Some(p) => new File(p.file)
    }
  }
  
  val ONE_MONTH_IN_MS = 30 * ONE_DAY_IN_MS
  val ONE_DAY_IN_MS = 24 * ONE_HOUR_IN_MS
  val ONE_HOUR_IN_MS = 1 * 3600 * 1000
  
  def sendImageIfNewer(place: File, last_modified: Long) = {
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
    halt(500)
  }
  
  def get_image_using(photo: Option[Photo], last_modified: Long, f: String => File) = {
    photo match {
      case None    => halt(404)
      case Some(p) => {
        val file = f(p.file)
        sendImageIfNewer(file, last_modified)
      }
    }
  }
  
  getUser("/view/:photoid.mp3") { _ =>
    val photo_id = Long.parseLong(params("photoid"))
    val last_modified = request.getDateHeader("If-Modified-Since")
    val photo: Option[Photo] = transaction { Database.photos.lookup(photo_id) }
    get_image_using(photo, last_modified, BrowserFolder.getImage)
  }
  
  getUser("/thumbnail/:photoid.mp3") { _ =>
    val photo_id = Long.parseLong(params("photoid"))
    val last_modified = request.getDateHeader("If-Modified-Since")
    val photo: Option[Photo] = transaction { Database.photos.lookup(photo_id) }
    get_image_using(photo, last_modified, BrowserFolder.getThumbnail)
  }
  
  notFound {  // Executed when no other route succeeds
    JNull
  }
}