package com.nublic.app.browser.web.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.nublic.app.browser.web.client.UI.BrowserUi;
import com.nublic.app.browser.web.client.UI.EmptyUI;
import com.nublic.app.browser.web.client.UI.UIUtils;
import com.nublic.app.browser.web.client.error.ErrorPopup;
import com.nublic.app.browser.web.client.model.BrowserModel;
import com.nublic.app.browser.web.client.model.ParamsHashMap;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class BrowserApp implements EntryPoint, ValueChangeHandler<String> {
	
	BrowserUi theUi;
	BrowserModel model;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		model = null;
		theUi = null;
	
	    String startingToken = History.getToken();
	    History.newItem(startingToken);
	    History.addValueChangeHandler(this);
	    History.fireCurrentHistoryState();
	}
	
	public void initBrowser() {
		// Create the model and the UI
		model = new BrowserModel();
		theUi = new BrowserUi(model);

		RootLayoutPanel rp = RootLayoutPanel.get();
	    rp.add(theUi);
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		String token = event.getValue();
		
		String args = "";
		int question = token.indexOf("?");
		if (question != -1) {
			args = token.substring(question + 1);
			token = token.substring(0, question);
		}
		
		// Initial or browser page
		if (token.isEmpty() || token.equals(Constants.BROWSER_VIEW)) {
			// Necessary to distinct whether the user wants the browser below or is accessing a raw content
			if (model == null) {
				initBrowser();
			}
			// show the initial screen (empties the file list of the model)
			model.updateFiles(new ParamsHashMap(args));
			
		// An image visualization
		} else if (token.equals(Constants.IMAGE_VIEW)) {
			ParamsHashMap hmap = new ParamsHashMap(args);
			if (model == null) {
				String path = hmap.get(Constants.PATH_PARAMETER);
				if (path != null) {
					// Redirect navigation to raw resource in server
					Window.open(GWT.getHostPageBaseURL() + "server/view/" + path + "." + Constants.IMAGE_TYPE, "_self", "");
				} else {
					ErrorPopup.showError("No path to the resource found");
				}
			} else {
				// show the image lightbox
				theUi.showImage(hmap);
			}
		// A document visualization
		} else if (token.equals(Constants.DOCUMENT_VIEW)) {
			ParamsHashMap hmap = new ParamsHashMap(args);
			if (model == null) {
				String path = hmap.get(Constants.PATH_PARAMETER);
				if (path != null) {
					// Redirect navigation to raw resource in server
					Window.open(GWT.getHostPageBaseURL() + "server/view/" + path + "." + Constants.DOCUMENT_TYPE, "_self", "");
				} else {
					ErrorPopup.showError("No path to the resource found");
				}
			} else {
				// show the PDF lightbox
				theUi.showPDF(hmap);
			}
			
		// A music visualization	
		} else if (token.equals(Constants.MUSIC_VIEW) || token.equals(Constants.VIDEO_VIEW)) {
			ParamsHashMap hmap = new ParamsHashMap(args);
			if (model == null) {
				String path = hmap.get(Constants.PATH_PARAMETER);
				if (path != null) {
					// Create a new "empty" windows with the player
					EmptyUI empty = new EmptyUI();
					RootLayoutPanel rp = RootLayoutPanel.get();
				    rp.add(empty);
				    UIUtils.showPlayer(empty, hmap, false, token);
				} else {
					ErrorPopup.showError("No path to the resource found");
				}
			} else {
				// show the music player (false will try to look first for the flash player)
				UIUtils.showPlayer(theUi, hmap, false, token);
			}
		} else {
			ErrorPopup.showError("Unrecognized token");
		}
	}
	
}
