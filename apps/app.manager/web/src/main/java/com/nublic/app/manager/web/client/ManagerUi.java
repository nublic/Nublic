package com.nublic.app.manager.web.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Unit;
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
import com.nublic.app.manager.web.frame.Counter;
import com.nublic.app.manager.web.settings.SettingsPage;
import com.nublic.app.manager.web.welcome.WelcomePage;
import com.nublic.util.messages.SequenceHelper;

public class ManagerUi extends Composite implements AppUrlChangeHandler {
	
	interface Styles extends CssResource {
	    String inner();
	}
	@UiField Styles style;

	private static ManagerUiUiBinder uiBinder = GWT.create(ManagerUiUiBinder.class);
	@UiField LayoutPanel layout;
	@UiField TopBar navBar;
	Widget innerWidget = null;
	// Model
	ExtendedHistory history = null;
	HashMap<String, AppData> apps = null;
	ClientState state = null;
	String path = null;

	interface ManagerUiUiBinder extends UiBinder<Widget, ManagerUi> {
	}

	public ManagerUi() {
		initWidget(uiBinder.createAndBindUi(this));
		// Initialize client state
		this.history = new ExtendedHistory(Location.getHref());
		this.state = ClientState.INITIAL;
		this.path = "";
		// Get apps from server
		AppDataMessage msg = new AppDataMessage(this);
		SequenceHelper.sendJustOne(msg, RequestBuilder.GET);
		// Add tab for welcome
		navBar.addToSecondaryTab("settings", "images/settings.png", "Settings", "#settings");
	}
	
	void loadApps(final HashMap<String, AppData> apps) {
		this.apps = apps;
		// Initialize set of favourites
		for (AppData data : apps.values()) {
			if (data.isFavourite()) {
				addAppTab(data);
			}
		}
		// Show apps in welcome page if shown
		if (state == ClientState.WELCOME) {
			((WelcomePage)innerWidget).showApps();
		}
	}
	
	public void addAppTab(AppData data) {
		navBar.addToPrimaryTab(data.getId(),
				GWT.getHostPageBaseURL() + "manager/server/app-image/light/" + data.getId() + "/32",
				data.getDefaultName(), 
				"#" + data.getId() + "/" + data.getPath());
	}

	
	public void setFavourite(String id, boolean favourite) {
		AppData app = this.apps.get(id);
		if (app.isFavourite() != favourite) {
			FavouriteMessage msg = new FavouriteMessage(id);
			if (favourite) {
				addAppTab(app);
				SequenceHelper.sendJustOne(msg, RequestBuilder.PUT);
			} else {
				navBar.removeFromTabs(id);
				SequenceHelper.sendJustOne(msg, RequestBuilder.DELETE);
			}
			app.setFavourite(favourite);
		}
	}
	
	void setContentWidget(Widget w) {
		if (innerWidget != null) {
			layout.remove(innerWidget);
		}
		innerWidget = w;
		innerWidget.setHeight("100%");
		innerWidget.setWidth("100%");
		layout.add(innerWidget);
		layout.setWidgetTopBottom(innerWidget, 40, Unit.PX, 0, Unit.PX);
	}
	
	public void go(String token) {
		// Generate final URL
		String current = Location.getHref();
		ClientState newState = ClientState.fromToken(token);
		String newHref = GWT.getHostPageBaseURL() + token;
		long innerId = innerWidget instanceof AppFrame ? ((AppFrame)innerWidget).getId() : Counter.NOT_ALLOWED;
		if (history.isCurrent(current) && !history.isBareNew()) {
			/* Do nothing */
		} else if (history.isPrevious(current)) {
			if (newState == ClientState.FRAME && state == ClientState.FRAME) {
				AppFrame frame = (AppFrame)innerWidget;
				if (history.isPreviousId(innerId)) {
					frame.back();
				} else {
					changeTo(newState, newHref);
				}
			} else {
				changeTo(newState, newHref);
			}
			history.back();
		} else if (history.isNext(current)) {
			if (newState == ClientState.FRAME && state == ClientState.FRAME) {
				AppFrame frame = (AppFrame)innerWidget;
				if (history.isPreviousId(innerId)) {
					frame.forward();
				} else {
					changeTo(newState, newHref);
				}
			} else {
				changeTo(newState, newHref);
			}
			history.forward();
		} else {
			changeTo(newState, newHref);
			innerId = innerWidget instanceof AppFrame ? ((AppFrame)innerWidget).getId() : Counter.NOT_ALLOWED;
			history.go(current, innerId);
		}
		// Change tabs as needed
		tabChange(token);
	}
	
	public void tabChange(String token) {
		if (state == ClientState.WELCOME) {
			// Remove elements which are not favourite
			if (this.apps != null) {
				ArrayList<String> toBeRemoved = new ArrayList<String>();
				for (String appId : navBar.getElementsInPrimaryBar()) {
					if (!this.apps.get(appId).isFavourite()) {
						toBeRemoved.add(appId);
					}
				}
				for (String appToRemove : toBeRemoved) {
					navBar.removeFromTabs(appToRemove);
				}
			}
		} else if (state == ClientState.FRAME) {
			// Get app id
			int slashPos = token.indexOf('/');
			String appId = token.substring(0, slashPos);
			if (!navBar.getElementsInPrimaryBar().contains(appId)) {
				addAppTab(apps.get(appId));
			}
		}
		
		if (state == ClientState.WELCOME) {
			// Deselect every element
			navBar.deselectAll();
		} else {
			int slashPos = token.indexOf('/');
			String appId = token.substring(0, slashPos);
			navBar.select(appId);
		}
	}
	
	void changeTo(ClientState newState, String path) {
		if (newState != state) {
			setContentWidget(createWidget(newState));
			if (newState == ClientState.FRAME) {
				AppFrame frame = (AppFrame)innerWidget;
				frame.setHref(path);
			}
			this.state = newState;
			this.path = path;
		}
	}
	
	Widget createWidget(ClientState newState) {
		switch(newState) {
		case INITIAL:
			return null;
		case WELCOME:
			return new WelcomePage(this);
		case SETTINGS:
			return new SettingsPage(this);
		case FRAME:
			AppFrame frame = new AppFrame("inner");
			frame.addAppUrlChangedHandler(this);
			frame.setStyleName(style.inner());
			return frame;
		}
		return null;
	}
	
	public Map<String, AppData> getApps() {
		return this.apps;
	}

	@Override
	public void appUrlChanged(AppUrlChangeEvent event) {
		String path = event.getUrl().replace(GWT.getHostPageBaseURL(), "");
		// Generate final URL
		LocationWithHash location = new LocationWithHash(Location.getHref());
		LocationWithHash newLocation = new LocationWithHash(location.getBase(), path);
		String finalNewPath = newLocation.getLocation();
		// Check in extended history
		if (history.isCurrent(finalNewPath)) {
			// Do nothing
		} else if (history.isPrevious(finalNewPath)) {
			History.back();
		} else if (history.isNext(finalNewPath)) {
			History.forward();
		} else {
			History.newItem(path);
		}
	}

	@Override
	public void appTitleChanged(AppUrlChangeEvent event) {
		String trimmed = event.getTitle().trim();
		String title = trimmed.isEmpty() ? "Nublic" : "Nublic - " + trimmed;
		Document.get().setTitle(title);
	}
}
