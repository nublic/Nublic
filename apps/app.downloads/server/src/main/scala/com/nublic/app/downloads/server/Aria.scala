package com.nublic.app.downloads.server

import com.nublic.ws.json.Method0
import com.nublic.ws.json.Method1
import com.nublic.ws.json.Method2
import com.nublic.ws.json.Method3
import com.nublic.ws.json.Method4
import com.nublic.ws.json.Method5
import com.nublic.ws.json.WebSocketJsonRpc
import net.liftweb.json._

class AriaEventHandler {
  def onConnect(): Unit
  def onDisconnect(): Unit
  def onStop(): Unit
  def onDownloadStart(gid: Long): Unit
}

class Aria extends WebSocketJsonRpc {

  var handlers = List[AriaEventHandler]()
  def addEventHandler(h: AriaEventHandler) = {
    handlers ::= h
  }

  var connected = false

  def onConnect(): Unit = {
    connected = true
    handlers.map(_.onConnect())
  }

  def onDisconnect(): Unit = {
    connected = false
    handlers.map(_.onDisconnect())
  }

  def onStop(): Unit = {
    connected = false
    handlers.map(_.onStop())
  }

  def onError(e: Throwable): Unit = {
    Console.err.println(e.getMessage())
  }

  val addUri = new Method1[Array[String], String]("aria2.addUri", this)
  val addUriOptions = new Method2[Array[String], JValue, String]("aria2.addUri", this)
  val addTorrent = new Method1[String, String]("aria2.addTorrent", this)
  val addTorrentOptions = new Method3[String, Array[String], JValue, String]("aria2.addTorrent", this)
  val addMetalink = new Method1[String, String]("aria2.addMetalink", this)
  val addMetalinkOptions = new Method2[String, JValue, String]("aria2.addMetalink", this)

  val remove = new Method1[String, String]("aria2.remove", this)
  val forceRemove = new Method1[String, String]("aria2.forceRemove", this)

  val pause = new Method1[String, String]("aria2.pause", this)
  val pauseAll = new Method0[String]("aria2.pauseAll", this)
  val forcePause = new Method1[String, String]("aria2.forcePause", this)
  val forcePauseAll = new Method0[String]("aria2.forcePauseAll", this)
  val unpause = new Method1[String, String]("aria2.unpause", this)
  val unpauseAll = new Method0[String]("aria2.unpauseAll", this)

  val tellStatus = new Method1[String, AriaDownloadStatus]("aria2.tellStatus", this)
  val tellStatusKeys = new Method2[String, List[String], AriaDownloadStatus]("aria2.tellStatus", this)  
  val tellActive = new Method0[List[AriaDownloadStatus]]("aria2.tellActive", this)
  val tellActiveKeys = new Method1[List[String], List[AriaDownloadStatus]]("aria2.tellActive", this)
  val tellWaiting = new Method2[Long, Long, List[AriaDownloadStatus]]("aria2.tellWaiting", this)
  val tellWaitingKeys = new Method3[Long, Long, List[String], List[AriaDownloadStatus]]("aria2.tellWaiting", this)
  val tellStopped = new Method2[Long, Long, List[AriaDownloadStatus]]("aria2.tellStopped", this)
  val tellStoppedKeys = new Method3[Long, Long, List[String], List[AriaDownloadStatus]]("aria2.tellStopped", this)

  val getUris = new Method1[String, List[AriaUri]]("aria2.getUris", this)
  val getFiles = new Method1[String, List[AriaFile]]("aria2.getFiles", this)
  val getPeers = new Method1[String, List[AriaPeer]]("aria2.getPeers", this)
  val getServers = new Method1[String, List[AriaIndexedServers]]("aria2.getServers", this)

  val changePosition = new Method3[String, Long, String, Long]("aria2.changePosition", this)
  val changeUri = new Method5[String, Long, List[String], List[String], Long, Tuple2[Long, Long]]("aria2.changeUri", this)

  val getOption = new Method1[String, JValue]("aria2.getOption", this)
  val changeOption = new Method2[String, JValue, String]("aria2.changeOption", this)
  
  val getGlobalOption = new Method0[JValue]("aria2.getGlobalOption", this)
  val changeGlobalOption = new Method1[JValue, String]("aria2.changeGlobalOption", this)
  val getGlobalStat = new Method0[AriaGlobalStat]("aria2.getGlobalStat", this)

  val getVersion = new Method0[JValue]("aria2.getVersion", this)

  val purgeDownloadResult = new Method0[String]("aria2.purgeDownloadResult", this)
  val removeDownloadResult = new Method1[String, String]("aria2.removeDownloadResult", this)

  val shutdown = new Method0[String]("aria2.shutdown", this)
  val forceShutdown = new Method0[String]("aria2.forcerShutdown", this)

  def notificationTypes = Map("onDowloadStart" -> Array(manifest[Long]))

  def unknownNotification(method: String, params: Array[JValue]): Unit = {
    Console.err.println("Received unknown notification " + method)
  }

  def onDownloadStart(gid: Long) = {
    handlers.map(_.onDownloadStart(gid))
  }
}
object Aria {
  val POSITION_SET     = "POS_SET"
  val POSITION_CURRENT = "POS_CUR"
  val POSITION_END     = "POS_END"
}

case class AriaDownloadStatus(val gid: Option[String],
                              val status: Option[String],
                              val totalLength: Option[String],
                              val completedLength: Option[String],
                              val uploadLength: Option[String],
                              val bitfield: Option[String],
                              val downloadSpeed: Option[String],
                              val uploadSpeed: Option[String],
                              val infoHash: Option[String],
                              val numSeeders: Option[String],
                              val pieceLength: Option[String],
                              val connections: Option[String],
                              val errorCode: Option[String],
                              val followedBy: Option[List[String]],
                              val belongsTo: Option[String],
                              val dir: Option[String],
                              val files: Option[List[AriaFile]],
                              val bittorrent: Option[AriaBitTorrent])
object AriaDownloadStatus {
  val STATUS_ACTIVE   = "active"
  val STATUS_WAITING  = "waiting"
  val STATUS_PAUSED   = "paused"
  val STATUS_ERROR    = "error"
  val STATUS_COMPLETE = "complete"
  val STATUS_REMOVED  = "removed"

  val EXIT_SUCCESSFUL               = "0"
  val EXIT_UNKNOWN_ERROR            = "1"
  val EXIT_TIMEOUT_ERROR            = "2"
  val EXIT_RESOURCE_NOT_FOUND       = "3"
  val EXIT_MAX_FILE_NOT_FOUND       = "4"
  val EXIT_SPEED_TOO_SLOW           = "5"
  val EXIT_NETWORK_PROBLEM          = "6"
  val EXIT_UNFINISHED_DOWNLOADS     = "7"
  val EXIT_RESUME_NOT_SUPPORTED     = "8"
  val EXIT_NOT_ENOUGH_DISK_SPACE    = "9"
  val EXIT_PIECE_LENGTH_DIFFERENT   = "10"
  val EXIT_DOWNLOADING_SAME_FILE    = "11"
  val EXIT_DOWNLOADING_SAME_TORRENT = "12"
  val EXIT_FILE_ALREADY_EXISTED     = "13"
  val EXIT_RENAMING_FAILED          = "14"
  val EXIT_COULD_NOT_OPEN           = "15"
  val EXIT_COULD_NOT_CREATE_FILE    = "16"
  val EXIT_IO_ERROR                 = "17"
  val EXIT_COULD_NOT_CREATE_DIR     = "18"
  val EXIT_NAME_RESOLUTION_FAILED   = "19"
  val EXIT_COULD_NOT_PARSE_METALINK = "20"
  val EXIT_FTP_COMMAND_FAILED       = "21"
  val EXIT_BAD_HTTP_RESPONSE_HEADER = "22"
  val EXIT_TOO_MANY_REDIRECTIONS    = "23"
  val EXIT_HTTP_AUTH_FAILED         = "24"
  val EXIT_COULD_NOT_PARSE_BENCODED = "25"
  val EXIT_TORRENT_CORRUPTED        = "26"
  val EXIT_BAD_MAGNED_URI           = "27"
  val EXIT_BAD_OPTION               = "28"
  val EXIT_OVERLOAD_OR_MAINTENANCE  = "29"
  val EXIT_COULD_NOT_PARSE_JSON_RPC = "30"
}

case class AriaBitTorrent(val announceList: List[String],
                          val comment: String,
                          val creationDate: String,
                          val mode: String,
                          val info: AriaBitTorrentInfo)
object AriaBitTorrent {
  val MODE_SINGLE = "single"
  val MODE_MULTI  = "multi"
}
                          
case class AriaBitTorrentInfo(val name: String)   

case class AriaUri(val uri: String, val status: String)
object AriaUri {
  val STATUS_USED    = "used"
  val STATUS_WAITING = "waiting"
}

case class AriaFile(val index: String,
                    val path: String,
                    val length: Long,
                    val completedLength: String,
                    val selected: String,
                    val uris: List[AriaUri])

case class AriaPeer(val peerId: String,
                    val ip: String,
                    val port: String,
                    val bitfield: String,
                    val amChoking: String,
                    val peerChoking: String,
                    val downloadSpeed: String,
                    val uploadSpeed: String,
                    val seeder: String)

case class AriaIndexedServers(val index: String,
                              val servers: List[AriaServer])

case class AriaServer(val uri: String,
                      val currentUri: String,
                      val downloadSpeed: String)

case class AriaGlobalStat(val downloadSpeed: String,
                          val uploadSpeed: String,
                          val numActive: String,
                          val numWaiting: String,
                          val numStopped: String)
 
