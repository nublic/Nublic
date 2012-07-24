package com.nublic.app.downloads.server

import com.nublic.filesAndUsers.java.User
import net.zschech.gwt.comet.server.CometServlet
import net.zschech.gwt.comet.server.CometServletResponse
import net.zschech.gwt.comet.server.CometSession

class CometDownloadsServer extends CometServlet {
  override def doComet(response: CometServletResponse) = {
    var session = response.getSession(false)
    if (session == null) {
      val user = new User(response.getRequest().getRemoteUser())
      session = response.getSession()
      AriaDbUser.get.addUserConnection(user, session)
    }
  }

  override def cometTerminated(response: CometServletResponse, serverInitiated: Boolean) = {
    val session = response.getSession(false)
    if (session != null) {
      val user = new User(response.getRequest().getRemoteUser())
      AriaDbUser.get.removeUserConnection(user, session)
    }
  }
}
