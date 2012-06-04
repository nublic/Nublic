package com.nublic.app.market.web.client.model;

import java.util.List;

import com.google.gwt.http.client.RequestBuilder;
import com.nublic.app.market.web.client.model.handlers.AppListHandler;
import com.nublic.app.market.web.client.model.messages.AskForAppListMessage;
import com.nublic.util.messages.SequenceHelper;

public class DataModel {

	List<AppInfo> appList = null;
	
	public DataModel() {
	}
	
	public void askForAppList(final AppListHandler alh) {
		if (appList == null) {
			AskForAppListMessage afalm = new AskForAppListMessage(new AppListHandler() {
				@Override
				public void onAppListReceived(List<AppInfo> appList) {
					DataModel.this.appList = appList;
					alh.onAppListReceived(appList);
				}
			});
			SequenceHelper.sendJustOne(afalm, RequestBuilder.GET);
		} else {
			alh.onAppListReceived(appList);
		}
	}
	
	

}
