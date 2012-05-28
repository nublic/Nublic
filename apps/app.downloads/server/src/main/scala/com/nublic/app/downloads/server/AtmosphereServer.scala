package com.nublic.app.downloads.server
import com.nublic.filesAndUsers.java.User

import org.atmosphere.cpr.AtmosphereHandler
import org.atmosphere.cpr.AtmosphereResource
import org.atmosphere.cpr.AtmosphereResource.TRANSPORT._
import org.atmosphere.cpr.AtmosphereResourceEvent
import org.atmosphere.cpr.BroadcasterFactory

class AtmosphereServer extends AtmosphereHandler {

  def onRequest(r: AtmosphereResource): Unit = {
    val req = r.getRequest
    val user = new User(req.getRemoteUser)

    if (req.getMethod.equalsIgnoreCase("GET")) {
      // Subscribe to the broadcaster for your user
      val b = BroadcasterFactory.getDefault.get(user.getUsername)
      r.setBroadcaster(b)
      r.suspend()
    } else if (req.getMethod.equalsIgnoreCase("POST")) {
      r.getBroadcaster.broadcast(req.getReader.readLine)
    }
  }

  def onStateChange(event: AtmosphereResourceEvent): Unit = {
    val r = event.getResource
    val response = r.getResponse

    if (r.isSuspended) {
      response.getWriter.write(event.getMessage.toString)
      r.transport match {
        case JSONP => r.resume()
        case LONG_POLLING => r.resume()
        case WEBSOCKET => response.getWriter.flush()
        case STREAMING => response.getWriter.flush()
      }
    } else if (!event.isResuming) {
      // Someone left
    }
  }
  
  def destroy(): Unit = {

  }
}
