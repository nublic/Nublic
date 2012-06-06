package com.nublic.app.market.web.client.ui;

import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.market.web.client.controller.Controller;
import com.nublic.app.market.web.client.model.AppInfo;
import com.nublic.app.market.web.client.model.AppStatus;
import com.nublic.app.market.web.client.model.DataModel;
import com.nublic.app.market.web.client.model.handlers.AppListHandler;

public class MainUI extends Composite {
	private static MainUIUiBinder uiBinder = GWT.create(MainUIUiBinder.class);
	interface MainUIUiBinder extends UiBinder<Widget, MainUI> { }
	
	@UiField AppPanel appPanel;

	public MainUI(DataModel model) {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void loadAppList() {
		Controller.INSTANCE.getModel().askForAppList(new AppListHandler() {
			@Override
			public void onAppListReceived(Map<String, AppInfo> appMap) {
				appPanel.clear();
				for (AppInfo app : appMap.values()) {
					AppWidget appW = new AppWidget(app);
					appPanel.addAppWidget(appW);
				}
			}
		});
	}
	
	public void loadApp(AppInfo app) {
		OpenApp appW = new OpenApp(app);
		appPanel.setOpenApp(appW);
	}

	public void changeAppStatus(String id, AppStatus newStatus) {
		appPanel.changeAppStatus(id, newStatus);
	}

}
