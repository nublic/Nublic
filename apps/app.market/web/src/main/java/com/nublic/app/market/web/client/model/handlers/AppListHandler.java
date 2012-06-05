package com.nublic.app.market.web.client.model.handlers;

import java.util.Map;

import com.nublic.app.market.web.client.model.AppInfo;

public interface AppListHandler {
	public void onAppListReceived(Map<String, AppInfo> appMap);
}
