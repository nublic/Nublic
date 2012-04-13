package com.nublic.ws.json

import com.nublic.ws.WebSocketEventHandler
import java.net.URI
import net.liftweb.json._
import net.liftweb.json.Extraction.decompose
import net.liftweb.json.Serialization.{ read, write }
import net.liftweb.util.ClassHelpers
import com.nublic.ws.WebSocketClient

abstract class WebSocketJsonRpcCallback {
  def execute(method: String, result: Response[JValue]): Unit
}

abstract class Callback[R](implicit m: Manifest[R]) extends WebSocketJsonRpcCallback {
  implicit val formats = Serialization.formats(NoTypeHints)
  def response(method: String, result: Response[R]): Unit
  def execute(method: String, result: Response[JValue]): Unit = response(method, result.map(_.extract[R]))
}

sealed abstract class Response[R] {
  def map[S](f: R => S): Response[S] = this match {
    case Result(result) => Result(f(result))
    case Error(error)   => Error(error)
  }
}
case class Result[R](val result: R) extends Response[R]
case class Error[R](val error: JValue) extends Response[R]

abstract class WebSocketJsonRpc {

  implicit val formats = Serialization.formats(NoTypeHints)
  private val client = new WebSocketClient(new EventHandler(this))

  // Connecting
  // ==========

  def connect(uri: URI): Unit = {
    client.connect(uri)
  }

  /* Callback when connecting */
  def onConnect(): Unit

  /* Callback when disconnecting */
  def onDisconnect(): Unit

  /* Callback when stopping */
  def onStop(): Unit

  /* Callback when error arises */
  def onError(e: Throwable): Unit

  // Sending
  // =======

  private val _globalLock: Object = new Object()
  private var _nextId: Long = 1
  private var _locks: scala.collection.mutable.Map[Long, Object] = 
    new scala.collection.mutable.HashMap()
  private var _results: scala.collection.mutable.Map[Long, Result[JValue]] = 
    new scala.collection.mutable.HashMap()
  private var _callbacks: scala.collection.mutable.Map[Long, WebSocketJsonRpcCallback] =
    new scala.collection.mutable.HashMap()

  private def _toJson(x: AnyRef): JValue = {
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
      JField("method", JString(method)),
      JField("params", JArray(params)),
      JField("id", JInt(id))
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
      client.send(_createMessage(id, method, params))
  
      // Now wait to its conclusion
      waiter.wait()

      // Once we've been notified, we should have
      // the result in the _results map
      val result = _globalLock.synchronized {
        _locks.remove(id)
        _results.remove(id).get // Returns the removed value
      }

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
      client.send(_createMessage(id, method, params))
    }
  }

  private def _notify(method: String, params: List[JValue]): Unit = {
    _withNextId { id =>
      client.send(_createMessage(id, method, params))
    }
  }

  def send(method: String, params: List[AnyRef]): Response[JValue] = _send(method, params.map(_toJson))

  def sendAndParse[A](method: String, params: List[AnyRef])(implicit mf: Manifest[A]): Response[A] =
    send(method, params).map(_.extract[A])

  def asyncSend(method: String, params: List[AnyRef], cb: WebSocketJsonRpcCallback): Unit = 
    _asyncSend(method, params.map(_toJson), cb)

  def notify(method: String, params: List[AnyRef]): Unit = _notify(method, params.map(_toJson))

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

  private def handleMessageReceived(json: JObject) = {
    if (json.values.get("method").isDefined) {
      // This is a notification
    } else if (json.values.get("result").isDefined) {
      // This is a method invocation result
    } else {
      throw new JsonParseException("Received JSON value is not in correct format")
    }
  }

  // Event handlers
  // ==============

  class EventHandler(val rpc: WebSocketJsonRpc) extends WebSocketEventHandler {
    def onOpen(client: WebSocketClient) {
      // Call handler
      rpc.onConnect()
    }

    def onMessage(client: WebSocketClient, message: String) {
      // The difficult things happen here...
      try {
	parse(message) match {
	  case json: JObject => rpc.handleMessageReceived(json)
	  case _             => rpc.onError(new JsonParseException("Received JSON value is not an object"))
	}
      } catch {
	case e: Throwable => rpc.onError(e)
      }
    }

    def onMessage(client: WebSocketClient, message: Array[Byte]) {
      // Do nothing
      throw new IllegalStateException("We should not receive nothing in Vinary frame")
    }

    def onError(client: WebSocketClient, e: Throwable) {
      // Call handler
      rpc.onError(e)
    }

    def onClose(client: WebSocketClient) {
      // Call handler
      rpc.onDisconnect()
    }

    def onStop(client: WebSocketClient) {
      // Call handler
      rpc.onStop()
    }
  }
}
