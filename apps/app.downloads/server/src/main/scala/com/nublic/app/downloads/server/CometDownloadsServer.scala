package com.nublic.app.downloads.server

import com.nublic.filesAndUsers.java.User
import net.zschech.gwt.comet.server.CometServlet
import net.zschech.gwt.comet.server.CometServletResponse

class CometDownloadsServer extends CometServlet {
  override def doComet(response: CometServletResponse) = {
    val user = new User(response.getRequest().getRemoteUser())
    response.write(user.getUsername())
  }

  override def cometTerminated(response: CometServletResponse, serverInitiated: Boolean) = {

  }
}
