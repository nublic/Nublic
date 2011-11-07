package com.nublic.app.manager.web.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.RootLayoutPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ManagerApp implements EntryPoint, ValueChangeHandler<String>, AppUrlChangeHandler {

	ManagerUi theUi;
	ExtendedHistory extendedHistory;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		initUi();
		// Initialize the extended history for controlling inside
		extendedHistory = new ExtendedHistory(Location.getHref());
		// Initialize tokens
		String startingToken = History.getToken();
	    History.newItem(startingToken);
	    History.addValueChangeHandler(this);
	    History.fireCurrentHistoryState();
	}
	
	public void initUi() {
		theUi = new ManagerUi();
		RootLayoutPanel rp = RootLayoutPanel.get();
	    rp.add(theUi);
	    
	    theUi.addAppUrlChangedHandler(this);
	    
	    theUi.addTab("browser", "Browser", "images/browser.png",
	    		"http://localhost:8081/browser/BrowserApp.html");
	    theUi.addTab("music", "Music", "images/music.png",
	    		"http://localhost:8081/music/MusicApp.html");
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		// Handle token
		String token = event.getValue();
		int slashPlace = token.indexOf("/");
		String appId = slashPlace == -1 ? null : token.substring(0, slashPlace);
		// Handle extended history
		String current = Location.getHref();
		if (extendedHistory.isCurrent(current) && !extendedHistory.isBareNew()) {
			// Do nothing
		} else if (extendedHistory.isPrevious(current)) {
			extendedHistory.back();
			theUi.frameBack();
			if (appId != null) {
				theUi.selectTabOnly(appId);
			}
		} else if (extendedHistory.isNext(current)) {
			extendedHistory.forward();
			theUi.frameForward();
			if (appId != null) {
				theUi.selectTabOnly(appId);
			}
		} else {
			extendedHistory.go(current);
			// Handle token
			if (appId == null) {
				// TODO: Show notifications or things like that
				theUi.select("browser");
			} else {
				theUi.select(appId, GWT.getHostPageBaseURL() + token);
			}
		}
	}

	@Override
	public void appUrlChanged(AppUrlChangeEvent event) {
		String path = event.getUrl().replace(GWT.getHostPageBaseURL(), "");
		// Generate final URL
		LocationWithHash location = new LocationWithHash(Location.getHref());
		LocationWithHash newLocation = new LocationWithHash(location.getBase(), path);
		String finalNewPath = newLocation.getLocation();
		// Check in extended history
		if (extendedHistory.isCurrent(finalNewPath)) {
			// Do nothing
		} else if (extendedHistory.isPrevious(finalNewPath)) {
			History.back();
		} else if (extendedHistory.isNext(finalNewPath)) {
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
