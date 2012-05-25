package com.nublic.app.music.client;

import com.bramosystems.oss.player.core.event.client.PlayStateEvent.State;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.nublic.app.music.client.controller.Controller;
import com.nublic.app.music.client.datamodel.DataModel;
import com.nublic.app.music.client.ui.MainUi;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class MusicApp implements EntryPoint, ValueChangeHandler<String>, ClosingHandler {
	DataModel model;
	MainUi ui;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		model = new DataModel();
		ui = new MainUi(model);
		Controller.create(model, ui);
		
		RootLayoutPanel rp = RootLayoutPanel.get();
	    rp.add(ui);

	    Window.addWindowClosingHandler(this);
	    String startingToken = History.getToken();
	    History.newItem(startingToken);
	    History.addValueChangeHandler(this);
	    History.fireCurrentHistoryState();
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		String args = event.getValue();
		ParamsHashMap hmap = new ParamsHashMap(args);
		
		Controller.INSTANCE.changeState(hmap);
	}

	@Override
	public void onWindowClosing(ClosingEvent event) {
		if (Controller.INSTANCE.getPlayer().getState() == State.Started) {
			event.setMessage(Constants.I18N.abandonText());
		}
	}

}
