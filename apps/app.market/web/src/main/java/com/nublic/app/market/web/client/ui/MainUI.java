package com.nublic.app.market.web.client.ui;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.market.web.client.model.AppInfo;
import com.nublic.app.market.web.client.model.DataModel;
import com.nublic.app.market.web.client.model.handlers.AppListHandler;

public class MainUI extends Composite {
	private static MainUIUiBinder uiBinder = GWT.create(MainUIUiBinder.class);
	interface MainUIUiBinder extends UiBinder<Widget, MainUI> { }
	
	@UiField FlowPanel appPanel;

	public MainUI(DataModel model) {
		initWidget(uiBinder.createAndBindUi(this));
		
		model.askForAppList(new AppListHandler() {
			@Override
			public void onAppListReceived(List<AppInfo> appList) {
				for (AppInfo app : appList) {
					AppWidget appW = new AppWidget(app);
					appPanel.add(appW);
				}
			}
		});
	}

}
