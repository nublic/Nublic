package com.nublic.app.manager.web.client;

import com.google.gwt.event.shared.EventHandler;

public interface AppUrlChangeHandler extends EventHandler {
	void appUrlChanged(AppUrlChangeEvent event);
	void appTitleChanged(AppUrlChangeEvent event);
}
