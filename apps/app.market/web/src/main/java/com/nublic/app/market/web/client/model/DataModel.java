package com.nublic.app.market.web.client.model;

import java.util.Map;

import com.google.gwt.http.client.RequestBuilder;
import com.nublic.app.market.web.client.model.handlers.AppListHandler;
import com.nublic.app.market.web.client.model.messages.AskForAppListMessage;
import com.nublic.util.messages.SequenceHelper;

public class DataModel {

	Map<String, AppInfo> appMap = null;
	
	public DataModel() {
	}
	
	public void askForAppList(final AppListHandler alh) {
		if (appMap == null) {
			AskForAppListMessage afalm = new AskForAppListMessage(new AppListHandler() {
				@Override
				public void onAppListReceived(Map<String, AppInfo> appMap) {
					DataModel.this.appMap = appMap;
					alh.onAppListReceived(appMap);
				}
			});
			SequenceHelper.sendJustOne(afalm, RequestBuilder.GET);
		} else {
			alh.onAppListReceived(appMap);
		}
	}

	public void changeAppStatus(String id, AppStatus installing) {
		
	}
}
