package com.nublic.app.market.web.client.model;

import java.util.List;

import com.google.gwt.http.client.RequestBuilder;
import com.nublic.app.market.web.client.model.handlers.AppListHandler;
import com.nublic.app.market.web.client.model.messages.AskForAppListMessage;
import com.nublic.util.messages.SequenceHelper;

public class DataModel {

	List<AppInfo> appList = null;
	
	public DataModel() {
		askForAppList();
	}
	
	public void askForAppList() {
		AskForAppListMessage afalm = new AskForAppListMessage(new AppListHandler() {
			@Override
			public void onAppListReceived(List<AppInfo> appList) {
				DataModel.this.appList = appList;
			}
		});
		SequenceHelper.sendJustOne(afalm, RequestBuilder.GET);
	}

}
