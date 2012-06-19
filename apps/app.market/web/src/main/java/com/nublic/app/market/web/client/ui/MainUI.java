package com.nublic.app.market.web.client.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.market.web.client.controller.Controller;
import com.nublic.app.market.web.client.model.AppInfo;
import com.nublic.app.market.web.client.model.AppStatus;
import com.nublic.app.market.web.client.model.DataModel;
import com.nublic.app.market.web.client.model.handlers.AppListHandler;

public class MainUI extends Composite {
	private static MainUIUiBinder uiBinder = GWT.create(MainUIUiBinder.class);
	interface MainUIUiBinder extends UiBinder<Widget, MainUI> { }
	
	private final int LIST_PANEL = 0;
	private final int OPEN_PANEL = 1;
	
	@UiField DeckPanel deckPanel;
	@UiField AppPanel appPanel;
	@UiField OpenPanel openPanel;

	public MainUI(DataModel model) {
		initWidget(uiBinder.createAndBindUi(this));
		deckPanel.showWidget(LIST_PANEL);
	}

	public void loadAppList() {
		Controller.INSTANCE.getModel().askForAppList(new AppListHandler() {
			@Override
			public void onAppListReceived(Map<String, AppInfo> appMap) {
				// Clear panel
				appPanel.clear();

				// Order the list of apps by their name
				ArrayList<AppInfo> appList = Lists.newArrayList(appMap.values());
				Collections.sort(appList, new Comparator<AppInfo>() {
					@Override
					public int compare(AppInfo app1, AppInfo app2) {
						return app1.getName().toLowerCase().compareTo(app2.getName().toLowerCase());
					}
				});

				// Create and add widgets
				for (AppInfo app : appList) {
					AppWidget appW = new AppWidget(app);
					appPanel.addAppWidget(appW);
				}
				deckPanel.showWidget(LIST_PANEL);
			}
		});
	}

	public void loadApp(AppInfo app) {
		OpenApp appW = new OpenApp(app);
		openPanel.setOpenApp(appW);
		deckPanel.showWidget(OPEN_PANEL);
	}

	public void changeAppStatus(String id, AppStatus newStatus) {
		appPanel.changeAppStatus(id, newStatus);
		openPanel.changeAppStatus(id, newStatus);
	}

}
