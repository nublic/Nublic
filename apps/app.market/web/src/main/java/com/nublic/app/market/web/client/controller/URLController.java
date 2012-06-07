package com.nublic.app.market.web.client.controller;

import com.nublic.app.market.web.client.Constants;
import com.nublic.app.market.web.client.model.AppInfo;
import com.nublic.app.market.web.client.model.DataModel;
import com.nublic.app.market.web.client.model.handlers.AppReceivedHandler;
import com.nublic.app.market.web.client.ui.MainUI;
import com.nublic.util.messages.ParamsHashMap;

public class URLController {
	MainUI ui;
	DataModel model;

	public URLController(MainUI ui, DataModel model) {
		this.ui = ui;
		this.model = model;
	}
	
	public void changeState(ParamsHashMap hmap) {
		String appId = hmap.get(Constants.PARAM_APP);
		
		if (appId == null || appId.equals("")) {
			ui.loadAppList();
		} else {
			model.getAppFromId(appId, new AppReceivedHandler() {
				@Override
				public void onAppReceived(AppInfo app) {
					ui.loadApp(app);					
				}
			});
		}
	}
}
