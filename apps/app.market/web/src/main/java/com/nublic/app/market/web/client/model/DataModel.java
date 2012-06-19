package com.nublic.app.market.web.client.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.user.client.Timer;
import com.nublic.app.market.web.client.Constants;
import com.nublic.app.market.web.client.controller.Controller;
import com.nublic.app.market.web.client.model.handlers.AppListHandler;
import com.nublic.app.market.web.client.model.handlers.AppReceivedHandler;
import com.nublic.app.market.web.client.model.handlers.InstallActionHandler;
import com.nublic.app.market.web.client.model.messages.AskForAppListMessage;
import com.nublic.app.market.web.client.model.messages.PackageMessage;
import com.nublic.app.market.web.client.model.messages.StatusMessage;
import com.nublic.util.messages.SequenceHelper;

public class DataModel {
	Map<String, AppInfo> appMap = null;
	Set<AppInfo> pendingAppsSet = new HashSet<AppInfo>();
	Timer updateTimer = new StatusTimer();
	
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

	public void changeAppStatus(String id, AppStatus status) {
		AppInfo appToChange = appMap.get(id);
		if (status == AppStatus.INSTALLING || status == AppStatus.REMOVING) {
			pendingAppsSet.add(appToChange);
		} else {
			pendingAppsSet.remove(appToChange);
		}
		appToChange.setStatus(status);
		setTimerAccordingToPendingSet();
	}
	
	public void getAppFromId(String appId, AppReceivedHandler arh) {
		if (appMap != null) {
			AppInfo app = appMap.get(appId);
			if (app != null) {
				arh.onAppReceived(app);
			}
		} else {
			PackageMessage.sendPackageMessage(appId, arh);
		}
	}

	private class StatusTimer extends Timer {
		@Override
		public void run() {
			List<AppInfo> tempPendingList = new ArrayList<AppInfo>(pendingAppsSet);
			// pendingAppsList can be modified inside the loop
			for (AppInfo app : tempPendingList) {
				StatusMessage.sendStatusMessage(app.getId(), new StatusChangeActionHandler(app));
			}
		}
	}
	
	private class StatusChangeActionHandler implements InstallActionHandler {
		AppInfo app;
		
		public StatusChangeActionHandler(AppInfo app) {
			this.app = app;
		}

		@Override
		public void actionSuccessful(AppStatus newStatus) {
			if (app.getStatus() != newStatus) {
				Controller.INSTANCE.changeAppStatus(app.getId(), newStatus);
			}
		}
	}
	
	private void setTimerAccordingToPendingSet() {
		if (pendingAppsSet.isEmpty()) {
			updateTimer.cancel();
		} else {
			updateTimer.scheduleRepeating(Constants.POLLING_TIME);
		}
	}
}
