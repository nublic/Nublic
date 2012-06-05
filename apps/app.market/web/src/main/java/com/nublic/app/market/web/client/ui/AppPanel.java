package com.nublic.app.market.web.client.ui;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.ui.FlowPanel;
import com.nublic.app.market.web.client.model.AppStatus;

public class AppPanel extends FlowPanel {
	Map<String, AppWidget> appMap = new HashMap<String, AppWidget>();
	
	public void addAppWidget(AppWidget w) {
		appMap.put(w.getId(), w);
		add(w);
	}

	public void changeAppStatus(String id, AppStatus newStatus) {
		AppWidget w = appMap.get(id);
		if (w != null) {
			w.changeStatus(newStatus);
		}
	}
}
