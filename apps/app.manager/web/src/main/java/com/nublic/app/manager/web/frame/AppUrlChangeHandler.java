package com.nublic.app.manager.web.frame;

import com.google.gwt.event.shared.EventHandler;

public interface AppUrlChangeHandler extends EventHandler {
	void appUrlChanged(AppUrlChangeEvent event);
	void appTitleChanged(AppUrlChangeEvent event);
}
