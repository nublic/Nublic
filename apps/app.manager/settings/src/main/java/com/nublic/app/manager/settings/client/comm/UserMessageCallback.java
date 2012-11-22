package com.nublic.app.manager.settings.client.comm;

import com.google.gwt.event.shared.EventHandler;

public interface UserMessageCallback extends EventHandler{
	public void onUserMessage(User u);
}
