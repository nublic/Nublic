package com.nublic.app.manager.web.client;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.manager.web.frame.AppFrame;
import com.nublic.app.manager.web.frame.AppUrlChangeEvent;
import com.nublic.app.manager.web.frame.AppUrlChangeHandler;
import com.nublic.util.gwt.LocationUtil;
import com.nublic.util.messages.SequenceHelper;

public class ManagerUi extends Composite implements AppUrlChangeHandler {
	
	interface Styles extends CssResource {
	    String inner();
	}
	@UiField Styles style;

	private static ManagerUiUiBinder uiBinder = GWT.create(ManagerUiUiBinder.class);
	@UiField LayoutPanel layout;
	@UiField TopBar navBar;
	@UiField AppFrame innerFrame;
	// Model
	HashMap<String, AppData> apps = null;

	interface ManagerUiUiBinder extends UiBinder<Widget, ManagerUi> {
	}

	public ManagerUi() {
		initWidget(uiBinder.createAndBindUi(this));
		// Bind to URL changes
		innerFrame.addAppUrlChangedHandler(this);
		// Get apps from server
		AppDataMessage msg = new AppDataMessage(this);
		SequenceHelper.sendJustOne(msg, RequestBuilder.GET);
		// Add tab for welcome
		navBar.addToSecondaryTab(Constants.marketAppName,
				"images/market.png", 
				Constants.I18N.market(), 
				"#" + Constants.marketAppName);
		navBar.addToSecondaryTab(Constants.settingsAppName,
				"images/settings.png", 
				Constants.I18N.settings(), 
				"#" + Constants.settingsAppName);
	}
	
	void loadApps(final HashMap<String, AppData> apps) {
		this.apps = apps;
		for (AppData data : apps.values()) {
			addAppTab(data);
		}
	}
	
	public void addAppTab(AppData data) {
		navBar.addToPrimaryTab(data.getId(),
				LocationUtil.getHostBaseUrl() + "manager/server/app-image/light/" + data.getId() + "/16",
				data.getLocalizedName(), 
				"#" + data.getId());
	}
	
	public void go(String token) {
		String newHref = LocationUtil.getHostBaseUrl() + token + "/";
		if (apps != null && apps.containsKey(token)) {
		    if (apps.get(token).getWebPath() != null) {
		        newHref += apps.get(token).getWebPath();
		    }
		}
		// Add query parameters passed to manager
		newHref += Location.getQueryString();
		if (apps != null && apps.containsKey(token)) {
		    if (apps.get(token).getWebExtra() != null) {
		        newHref += apps.get(token).getWebExtra();
		    }
		}
		innerFrame.setHref(newHref);
	}
	
	public void tabChange(String token) {
		int slashPos = token.indexOf('/');
		String appId = null;
		if (slashPos == -1) {
			appId = token;
		} else {
			appId = token.substring(0, slashPos);
		}
		
		if (appId.equals(Constants.welcomeAppName)) {
			// Deselect every element
			navBar.deselectAll();
		} else {
			navBar.select(appId);
		}
	}
	
	public Map<String, AppData> getApps() {
		return this.apps;
	}

	@Override
	public void appUrlChanged(AppUrlChangeEvent event) {		
		String path = event.getUrl().replace(LocationUtil.getHostBaseUrl(), "");
		int slashPos = path.indexOf('/');
		String appId = null;
		if (slashPos == -1) {
			appId = path;
		} else {
			appId = path.substring(0, slashPos);
		}
		
		if (!History.getToken().equals(appId) && !History.getToken().startsWith(appId + "/")) {
			Location.replace(LocationUtil.getHostBaseUrl() + "manager/#" + appId);
		}
		
		tabChange(path);
	}

	@Override
	public void appTitleChanged(AppUrlChangeEvent event) {
		String title = Constants.getTitle(event.getTitle());
		Document.get().setTitle(title);
	}
}
