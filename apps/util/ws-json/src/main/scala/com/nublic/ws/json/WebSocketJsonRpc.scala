package com.nublic.ws.json

import com.nublic.ws.WebSocketEventHandler
import net.liftweb.json._
import net.liftweb.json.Extraction.decompose
import net.liftweb.json.Serialization.{read, write}
import net.liftweb.util.ClassHelpers
import com.nublic.ws.WebSocketClient
import java.net.URI

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
  
  private val id: Long = 1
  
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
  
  private def _asyncSend(method: String, params: Array[JValue]): Unit = {
    
  }
  
  def send(method: String, params: Array[AnyRef]): JValue = _send(method, params.map(_toJson))
  
  def sendAndParse[A](method: String, params: Array[AnyRef])(implicit mf: Manifest[A]): A = send(method, params).extract[A]
  
  def asyncSend(method: String, params: Array[AnyRef]): Unit = _asyncSend(method, params.map(_toJson))

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