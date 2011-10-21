package com.nublic.app.browser.web.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootLayoutPanel;

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

		if (token.isEmpty()) {
			// Necessary to distinct whether the user wants the browser below or is accessing a raw content
			if (model == null) {
				initBrowser();
			}
			// show the initial screen (empties the file list of the model) TODO: welcome screen?
			//model.updateFiles(new ParamsHashMap());
		} else if (token.equals(Constants.BROWSER_VIEW)) {
			// Necessary to distinct whether the user wants the browser below or is accessing a raw content
			if (model == null) {
				initBrowser();
			}
			// show the desired browser page
			model.updateFiles(new ParamsHashMap(args));
		} else if (token.equals(Constants.IMAGE_VIEW)) {
			ParamsHashMap hmap = new ParamsHashMap(args);
			if (model == null) {
				String path = hmap.get(Constants.PATH_PARAMETER);
				if (path != null) {
					// Redirect navigation to raw resource in server
					Window.open(GWT.getHostPageBaseURL() + "server/view/" + path + "." + Constants.IMAGE_TYPE, "_self", "");
				} else {
					// TODO: error, must specify a path 
				}
			} else {
				// show the image lightbox
				theUi.showImage(hmap);
			}
		} else if (token.equals(Constants.DOCUMENT_VIEW)) {
			ParamsHashMap hmap = new ParamsHashMap(args);
			if (model == null) {
				String path = hmap.get(Constants.PATH_PARAMETER);
				if (path != null) {
					// Redirect navigation to raw resource in server
					Window.open(GWT.getHostPageBaseURL() + "server/view/" + path + "." + Constants.DOCUMENT_TYPE, "_self", "");
				} else {
					// TODO: error, must specify a path 
				}
			} else {
				// show the image lightbox
				theUi.showPDF(hmap);
			}
			
			
//		} else {
//			Window.alert("Unrecognized token=" + token);
		}
	}
	
}
