package com.nublic.app.market.web.client.ui;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.ui.FlowPanel;
import com.nublic.app.market.web.client.model.AppStatus;

public class AppPanel extends FlowPanel {
	
	Map<String, AppWidget> appMap = new HashMap<String, AppWidget>();
	OpenApp openApp = null;
	
	public void addAppWidget(AppWidget w) {
		appMap.put(w.getId(), w);
		add(w);
	}
	
	public void setOpenApp(OpenApp w) {
		this.clear();
		openApp = w;
		add(w);
	}

	public void changeAppStatus(String id, AppStatus newStatus) {
		AppWidget w = appMap.get(id);
		if (w != null) {
			// For list mode
			w.changeStatus(newStatus);
		}
		if (openApp != null) {
			// For open app mode
			openApp.changeStatus(newStatus);
		}
	}

	@Override
	public void clear() {
		super.clear();
		appMap.clear();
		openApp = null;
	}
}
