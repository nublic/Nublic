package com.nublic.app.market.web.client.ui;

import com.google.gwt.user.client.ui.FlowPanel;
import com.nublic.app.market.web.client.model.AppStatus;

public class OpenPanel extends FlowPanel {
	
	OpenApp openApp = null;
	
	public void setOpenApp(OpenApp w) {
		this.clear();
		openApp = w;
		add(w);
	}
	
	public void changeAppStatus(String id, AppStatus newStatus) {
		if (openApp != null) {
			openApp.changeStatus(newStatus);
		}
	}
	
	@Override
	public void clear() {
		super.clear();
		openApp = null;
	}

}
