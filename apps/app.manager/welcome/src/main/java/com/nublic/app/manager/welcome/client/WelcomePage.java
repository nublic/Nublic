package com.nublic.app.manager.welcome.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.util.gwt.LocationUtil;
import com.nublic.util.messages.Message;
import com.nublic.util.messages.SequenceHelper;

public class WelcomePage extends Composite {

	private static WelcomePageUiBinder uiBinder = GWT.create(WelcomePageUiBinder.class);
	@UiField VerticalPanel appGrid;
	@UiField Label welcomeLabel;

	interface WelcomePageUiBinder extends UiBinder<Widget, WelcomePage> {
	}

	public WelcomePage() {
		initWidget(uiBinder.createAndBindUi(this));
		// Get apps from server
		AppDataMessage msg = new AppDataMessage(this);
		SequenceHelper.sendJustOne(msg, RequestBuilder.GET);
		
		// Get user name to welcome it better
		SequenceHelper.sendJustOne(new Message() {
			@Override
			public String getURL() {
				return LocationUtil.getHostBaseUrl() + "manager/server/user-name";
			}
			@Override
			public void onSuccess(Response response) {
				if (response.getStatusCode() == Response.SC_OK) {
					welcomeLabel.setText(Constants.I18N.greeting(response.getText()));
				}
			}
			@Override
			public void onError() {
				// Do nothing
			}
		}, RequestBuilder.GET);
	}

	public void showApps(final HashMap<String, AppData> apps) {
		if (apps != null) {
			// Add apps
			ArrayList<String> appOrder = new ArrayList<String>();
			for(String appId : apps.keySet()) {
				appOrder.add(appId);
			}
			Collections.sort(appOrder, new Comparator<String>() {
				@Override
				public int compare(String a, String b) {
					AppData aD = apps.get(a);
					AppData bD = apps.get(b);
					return aD.getLocalizedName().compareTo(bD.getLocalizedName());
				}
			});
			for(String appId : appOrder) {
				AppData app = apps.get(appId);
				AppCell cell = new AppCell(appId, 
						LocationUtil.getHostBaseUrl() + "manager/server/app-image/dark/" + appId + "/32",
						app.getLocalizedName(), LocationUtil.getHostBaseUrl() + appId + "/" + app.getPath());
				appGrid.add(cell);
			}
		}
	}
}
