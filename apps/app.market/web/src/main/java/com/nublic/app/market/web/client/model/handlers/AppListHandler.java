package com.nublic.app.market.web.client.model.handlers;

import java.util.List;

import com.nublic.app.market.web.client.model.AppInfo;

public interface AppListHandler {
	public void onAppListReceived(List<AppInfo> appList);
}
