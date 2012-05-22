package com.nublic.ws.json

import com.ning.http.client.AsyncHttpClient
import com.ning.http.client.AsyncHttpClientConfig
import com.ning.http.client.providers.grizzly.GrizzlyAsyncHttpProvider
import com.ning.http.client.websocket.WebSocket
import com.ning.http.client.websocket.WebSocketTextListener
import com.ning.http.client.websocket.WebSocketUpgradeHandler
import java.net.URI
import net.liftweb.json._
import net.liftweb.json.Extraction.decompose
import net.liftweb.json.Serialization.{ read, write }
import net.liftweb.util.ClassHelpers


abstract class WebSocketJsonRpcCallback {
  def execute(result: Response[JValue]): Unit
}

abstract class Callback[R](implicit m: Manifest[R]) extends WebSocketJsonRpcCallback {
  implicit val formats = Serialization.formats(NoTypeHints)
  def response(result: Response[R]): Unit
  def execute(result: Response[JValue]): Unit = response(result.map(_.extract[R]))
}

sealed abstract class Response[R] {
  def map[S](f: R => S): Response[S] = this match {
    case Result(result) => Result(f(result))
    case Error(c, m, e) => Error(c, m, e)
  }
}
case class Result[R](val result: R) extends Response[R]
case class Error[R](val code: Long, val message: String, val error: Option[JValue]) extends Response[R]

abstract class WebSocketJsonRpc {

  implicit val formats = Serialization.formats(NoTypeHints)

  private var _client: WebSocket = null
  private var _connected: Boolean = false
  // private val client = new WebSocketClient(new EventHandler(this))

  // Connecting
  // ==========

  def connect(url: String): Unit = {
    val config = new AsyncHttpClientConfig.Builder().build();
    val c = new AsyncHttpClient(new GrizzlyAsyncHttpProvider(config), config);
    val listener = new Listener(this)
    val handler = (new WebSocketUpgradeHandler.Builder()).addWebSocketListener(listener).build()
    _client = c.prepareGet(url).execute(handler).get()
  }

  /* Callback when connecting */
  def onConnect(): Unit

  private def _onConnect(): Unit = {
    _connected = true
    onConnect()
  }

  /* Callback when disconnecting */
  def onDisconnect(): Unit

  private def _onDisconnect(): Unit = {
    _connected = false
    onDisconnect()
  }

  /* Callback when error arises */
  def onError(e: Throwable): Unit

  // Sending
  // =======

  private val _globalLock: Object = new Object()
  private var _nextId: Long = 1
  private var _locks: scala.collection.mutable.Map[Long, Object] = 
    new scala.collection.mutable.HashMap()
  private var _results: scala.collection.mutable.Map[Long, Response[JValue]] = 
    new scala.collection.mutable.HashMap()
  private var _callbacks: scala.collection.mutable.Map[Long, WebSocketJsonRpcCallback] =
    new scala.collection.mutable.HashMap()

  private def _toJson(x: Any): JValue = {
    if (x.isInstanceOf[JValue]) {
      x.asInstanceOf[JValue]
    } else {
      decompose(x)
    }
  }

  private def _withNextId[R](f: Long => R): R = {
    // Obtain method id
    val id = _globalLock.synchronized {
      _nextId += 1
      _nextId
    }
    f(id)
  }

  private def _createMessage(id: Long, method: String, params: List[JValue]): String = {
    // Create the JSON-RPC message...
    val message = JObject(List(
      JField("jsonrpc", JString("2.0")),
      JField("method",  JString(method)),
      JField("params",  JArray(params)),
      JField("id",      JInt(id))
    ))
    // ...and send it
    return compact(render(message))
  }

  private def _send(method: String, params: List[JValue]): Response[JValue] = {
    _withNextId { id =>
      // Create object to wait on...
      val waiter = new Object()
      // ...and add it to wait list
      _globalLock.synchronized {
	_locks += id -> waiter
      }

      // Send the JSON-RPC message
      _client.sendTextMessage(_createMessage(id, method, params))
  
      // Now wait to its conclusion
      waiter.synchronized {
        waiter.wait()
      }

      // Once we've been notified, we should have
      // the result in the _results map
      val result = _results.remove(id).get // Returns the removed value
      _locks.remove(id)

      return result
    }
  }

  private def _asyncSend[R](method: String, params: List[JValue], cb: WebSocketJsonRpcCallback): Unit = {
    _withNextId { id =>
      // Add callabck to the list
      _globalLock.synchronized {
        _callbacks += id -> cb
      }

      // Send the JSON-RPC message
      _client.sendTextMessage(_createMessage(id, method, params))
    }
  }

  private def _notify(method: String, params: List[JValue]): Unit = {
    _withNextId { id =>
      _client.sendTextMessage(_createMessage(id, method, params))
    }
  }

  def send(method: String, params: List[Any]): Response[JValue] = _send(method, params.map(_toJson))

  def sendAndParse[A](method: String, params: List[Any])(implicit mf: Manifest[A]): Response[A] =
    send(method, params).map(_.extract[A])

  def asyncSend(method: String, params: List[Any], cb: WebSocketJsonRpcCallback): Unit = 
    _asyncSend(method, params.map(_toJson), cb)

  def shoot(method: String, params: List[Any]): Unit = _notify(method, params.map(_toJson))

  // Notifications
  // =============

  def notificationTypes: Map[String, Array[Manifest[_]]]

  private def _eraseType(m: Manifest[_]): Class[_] = m.erasure

  private def _onNotification(method: String, params: Array[JValue]): Unit = {
    if (notificationTypes.get(method).isDefined) {
      // Get method types
      val types: Array[Manifest[_]] = notificationTypes(method)
      // Get parameters
      val valuesAndTypes: Array[(JValue, Manifest[_])] = params.zip(notificationTypes(method))
      val values: Array[AnyRef] = valuesAndTypes.map(x => x._1.extract)
      // Now call the method
      ClassHelpers.invokeMethod(this.getClass(), this, method, values, types.map(_eraseType))
    } else {
      unknownNotification(method, params)
    }
  }

  def unknownNotification(method: String, params: Array[JValue]): Unit

  // Message reception
  // =================

  class JsonParseException(message: String) extends Exception(message)

  private def _isNotificationMessage(fields: List[JField]) = fields.exists(f => f.name == "method")
  private def _isResultMessage(fields: List[JField]) = fields.exists(f => f.name == "result")

  private def handleMessageReceived(json: JValue) = json match {
    case JObject(fields) => {
      if (_isNotificationMessage(fields)) {
	handleNotificationMessage(fields)
      } else if (_isResultMessage(fields)) {
	handleResultMessage(fields)
      } else {
	onError(new JsonParseException("Received JSON value is not in correct format"))
      }
    }
    case _ => onError(new JsonParseException("Received JSON value is not an object"))
  }

  private def handleNotificationMessage(fields: List[JField]) = {
    // Get method name
    val method = fields.find(f => f.name == "method") match {
      case Some(JField(_, JString(s))) => Some(s)
      case _                           => None
    }
    // Get parameters
    val params = fields.find(f => f.name == "params") match {
      case Some(JField(_, JArray(p))) => Some(p)
      // params may not be included
      case _                          => Some(List())
    }
    // Send notification
    if (method.isDefined && params.isDefined) {
      _onNotification(method.get, params.get.toArray)
    } else {
      onError(new JsonParseException("Received notification with bad format"))
    }
  }

  private def handleResultMessage(fields: List[JField]): Unit = {
    // Get id
    val msgId = fields.find(f => f.name == "id") match {
      case Some(JField(_, JInt(i))) => Some(i)
      case _                        => None
    }
    // Get result
    val result = fields.find(f => f.name == "result") match {
      case Some(JField(_, JNull)) => None
      case Some(JField(_, r))     => Some(r)
      case _                      => None
    }
    // Get error
    val error = fields.find(f => f.name == "error") match {
      case Some(JField(_, JNull))      => None
      case Some(JField(_, JObject(f))) => Some(f)
      case _                           => None
    }
    // Check we have all the fields
    val correctResponse = (result.isDefined && !error.isDefined) || (!result.isDefined && error.isDefined) 
    if (msgId.isDefined && correctResponse) {
      // Create response
      val response: Response[JValue] = if (result.isDefined) {
        Result(result.get)
      } else {
        val error_fields = error.get
        val e_code: Long = error_fields.find(f => f.name == "code") match {
          case Some(JField(_, JInt(c))) => c.asInstanceOf[Long]
          case _                        => -1
        }
        val e_message = error_fields.find(f => f.name == "message") match {
          case Some(JField(_, JString(s))) => s
          case _                           => ""
        }
        val e_data = error_fields.find(f => f.name == "data") match {
          case Some(JField(_, d)) => Some(d)
          case _                  => None
        }
        Error(e_code, e_message, e_data)
      }
      // Tell the corresponding manager
      val id = msgId.get.asInstanceOf[Long]
      _globalLock.synchronized {
	if (_locks.contains(id)) {
	  // We have a sync request waiting
	  // So, put result on result map...
	  _results += id -> response
	  // ...and notify the waiting element
          val lock = _locks.get(id).get
          lock.synchronized {
	    lock.notify()
          }
	} else if (_callbacks.contains(id)) {
	  // We have an async request
	  // So, call the callback...
	  _callbacks.get(id).get.execute(response)
	  // ...and delete the callback
	  _callbacks.remove(id)
	} else {
	  // We had a no-response request
	}
      }
    } else {
      onError(new JsonParseException("Received result with bad format"))
    }
  }

  // Listener
  // ========

  class Listener(val rpc: WebSocketJsonRpc) extends WebSocketTextListener {

    def onOpen(websocket: WebSocket): Unit = {
      // Call handler
      rpc._onConnect()
    }

    def onClose(websocket: WebSocket): Unit = {
      // Call handler
      rpc._onDisconnect()
    }

    def onError(e: Throwable) = {
      // Call handler
      rpc.onError(e)
    }
    
    def onMessage(message: String) = {
      // The difficult things happen here...
      try {
	rpc.handleMessageReceived(parse(message))
      } catch {
	case e: Throwable => rpc.onError(e)
      }
    }

    def onFragment(fragment: String, last: Boolean) = {
      // Do nothing
    }
  }
}
