package com.nublic.app.photos.mobile.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.nublic.app.photos.web.client.controller.PhotoParamsHashMap;
import com.nublic.app.photos.web.client.controller.PhotosController;
import com.nublic.app.photos.web.client.view.MainUi;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class PhotosApp implements EntryPoint, ValueChangeHandler<String> {

	static MainUi theUi;
	static PhotosController controller;
	
	public static MainUi getUi() {
		return theUi;
	}
	
	public static PhotosController getController() {
		return controller;
	}
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		initUi();
		Window.enableScrolling(false);
	}
	
	public void initUi() {
		theUi = new MainUi();
		RootLayoutPanel rp = RootLayoutPanel.get();
	    rp.add(theUi);
	    
	    controller = new PhotosController(theUi);
	    controller.changeTitle("");
	    
	    String startingToken = History.getToken();
	    History.newItem(startingToken);
	    History.addValueChangeHandler(this);
	    History.fireCurrentHistoryState();
	}
	
	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		String args = event.getValue();
		PhotoParamsHashMap hmap = new PhotoParamsHashMap(args);
		controller.changeTo(hmap);
	}
}
