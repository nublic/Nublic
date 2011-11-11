package com.nublic.app.manager.web.welcome;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.nublic.app.manager.web.client.AppData;
import com.nublic.app.manager.web.client.ManagerUi;

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
			for(String appId : theUi.getAppOrder()) {
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
