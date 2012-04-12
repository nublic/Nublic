package com.nublic.ws.json

import com.nublic.ws.WebSocketEventHandler
import java.net.URI
import net.liftweb.json._
import net.liftweb.json.Extraction.decompose
import net.liftweb.json.Serialization.{ read, write }
import net.liftweb.util.ClassHelpers
import com.nublic.ws.WebSocketClient

abstract class WebSocketJsonRpcCallback {
  def execute(method: String, result: JValue): Unit
}

abstract class Callback[R](implicit m: Manifest[R]) extends WebSocketJsonRpcCallback {
  implicit val formats = Serialization.formats(NoTypeHints)
  def response(method: String, result: R): Unit
  def execute(method: String, result: JValue): Unit = response(method, result.extract[R])
}

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

  // Sending
  // =======

  private val _globalLock: Object = new Object()
  private var _nextId: Long = 1
  private var _locks: scala.collection.mutable.Map[Long, Object] = 
    new scala.collection.mutable.HashMap()
  private var _results: scala.collection.mutable.Map[Long, JValue] = 
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

  private def _send(method: String, params: Array[JValue]): JValue = {
    return JNothing
  }

  private def _asyncSend[R](method: String, params: Array[JValue], cb: WebSocketJsonRpcCallback): Unit = {
    
  }

  private def _notify(method: String, params: Array[JValue]): Unit = {

  }

  def send(method: String, params: Array[AnyRef]): JValue = _send(method, params.map(_toJson))

  def sendAndParse[A](method: String, params: Array[AnyRef])(implicit mf: Manifest[A]): A = send(method, params).extract[A]

  def asyncSend(method: String, params: Array[AnyRef], cb: WebSocketJsonRpcCallback): Unit = 
    _asyncSend(method, params.map(_toJson), cb)

  def notify(method: String, params: Array[AnyRef]): Unit = _notify(method, params.map(_toJson))

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

  // Event handlers
  // ==============

  class EventHandler(val rpc: WebSocketJsonRpc) extends WebSocketEventHandler {
    def onOpen(client: WebSocketClient) {
      // Call handler
      rpc.onConnect()
    }

    def onMessage(client: WebSocketClient, message: String) {}
    def onMessage(client: WebSocketClient, message: Array[Byte]) {}

    def onError(client: WebSocketClient, e: Throwable) {}

    def onClose(client: WebSocketClient) {}
    def onStop(client: WebSocketClient) {}
  }
}
