package com.nublic.app.manager.web.welcome;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.nublic.app.manager.web.client.AppData;
import com.nublic.app.manager.web.client.ManagerUi;
import com.nublic.util.messages.Message;
import com.nublic.util.messages.SequenceHelper;

public class WelcomePage extends Composite {

	private static WelcomePageUiBinder uiBinder = GWT.create(WelcomePageUiBinder.class);
	@UiField Grid appGrid;
	@UiField Label welcomeLabel;
	ManagerUi theUi;

	interface WelcomePageUiBinder extends UiBinder<Widget, WelcomePage> {
	}

	public WelcomePage(ManagerUi ui) {
		initWidget(uiBinder.createAndBindUi(this));
		this.theUi = ui;
		this.showApps();
		Document.get().setTitle("Nublic - Welcome");
		// Get user name to welcome it better
		SequenceHelper.sendJustOne(new Message() {
			@Override
			public String getURL() {
				return GWT.getHostPageBaseURL() + "manager/server/user-name";
			}
			@Override
			public void onSuccess(Response response) {
				welcomeLabel.setText("Welcome, " + response.getText() + "!");
			}
			@Override
			public void onError() {
				// Do nothing
			}
		}, RequestBuilder.GET);
	}

	public void showApps() {
		if (theUi.getApps() != null) {
			// Set number of necessary rows
			int n = theUi.getApps().size();
			int n_rows = (n / 2) + (n % 2);
			appGrid.resize(n_rows, 2);
			// Initialize placement
			int col = 0;
			int row = 0;
			// Add apps
			ArrayList<String> appOrder = new ArrayList<String>();
			for(String appId : theUi.getApps().keySet()) {
				appOrder.add(appId);
			}
			Collections.sort(appOrder, new Comparator<String>() {
				@Override
				public int compare(String a, String b) {
					AppData aD = theUi.getApps().get(a);
					AppData bD = theUi.getApps().get(b);
					return aD.getDefaultName().compareTo(bD.getDefaultName());
				}
			});
			for(String appId : appOrder) {
				AppData app = theUi.getApps().get(appId);
				AppCell cell = new AppCell(theUi, appId, 
						GWT.getHostPageBaseURL() + "manager/server/app-image/" + appId + "/32",
						app.getDefaultName(), appId + "/" + app.getPath(), app.isFavourite());
				appGrid.setWidget(row, col, cell);
				// Set new rows
				row += col;
				col = (col + 1) % 2;
			}
		}
	}
}
