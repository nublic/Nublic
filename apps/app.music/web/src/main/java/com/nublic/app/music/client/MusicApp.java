package com.nublic.app.music.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.nublic.app.music.client.ArtistCell.Images;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class MusicApp implements EntryPoint, ValueChangeHandler<String> {

	MusicUi theUi;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		
		theUi = new MusicUi();
		RootLayoutPanel rp = RootLayoutPanel.get();
	    rp.add(theUi);
	    
	    //analizar 
	    String startingToken = History.getToken();
	    History.newItem(startingToken);
	    History.addValueChangeHandler(this);
	    History.fireCurrentHistoryState();
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		// TODO Analizar
		String url = event.getValue();
		Window.alert(url);
	}
}
