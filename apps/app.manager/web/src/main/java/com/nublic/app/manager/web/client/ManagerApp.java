package com.nublic.app.manager.web.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootLayoutPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ManagerApp implements EntryPoint, ValueChangeHandler<String>, AppUrlChangeHandler {

	ManagerUi theUi;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		initUi();
		
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
		String token = event.getValue();
		int slashPlace = token.indexOf("/");
		if (slashPlace == -1) {
			theUi.select("browser");
		} else {
			String appId = token.substring(0, slashPlace);
			theUi.select(appId, GWT.getHostPageBaseURL() + token);
		}
	}

	@Override
	public void appUrlChanged(AppUrlChangeEvent event) {
		// Do nothing
		String path = event.getUrl().replace(GWT.getHostPageBaseURL(), "");
		History.newItem(path, false);
	}
	
	@Override
	public void appTitleChanged(AppUrlChangeEvent event) {
		Document.get().setTitle(getTitle(event.getTitle()));
	}
	
	private String getTitle(String title) {
		String trimmed = title.trim();
		return trimmed.isEmpty() ? "Nublic" : "Nublic - " + trimmed;
	}
	
}
