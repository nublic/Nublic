package com.nublic.app.browser.web.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
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
		// Create the model
		model = new BrowserModel();
		
		theUi = new BrowserUi(model);
		RootLayoutPanel rp = RootLayoutPanel.get();
	    rp.add(theUi);
	    
	    String startingToken = History.getToken();
	    History.addValueChangeHandler(this);
	    History.newItem(startingToken, true);
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

		// TODO: request folder if it's not loaded
		if (token.isEmpty()) {
			// show the initial screen (empties the file list of the model)
			model.updateFiles(new ParamsHashMap());
		} else if (token.equals(Constants.BROWSER_VIEW)) {
			// show the desired browser page
			model.updateFiles(new ParamsHashMap(args));
//		} else if (...) {
//			// ...more checks for other token values...
//		} else {
//			Window.alert("Unrecognized token=" + token);
		}
	}

	
}
