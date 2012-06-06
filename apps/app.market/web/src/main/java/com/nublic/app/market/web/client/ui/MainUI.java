package com.nublic.app.market.web.client.ui;

import java.util.Map;

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
				appPanel.clear();
				for (AppInfo app : appMap.values()) {
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
