package com.nublic.app.market.web.client.controller;

import com.nublic.app.market.web.client.model.DataModel;
import com.nublic.app.market.web.client.ui.MainUI;

public class Controller extends URLController {
	public static Controller INSTANCE;

	MainUI ui;
	DataModel model;

	public Controller(MainUI ui, DataModel model) {
		this.ui = ui;
		this.model = model;
	}

	public static void create(MainUI ui, DataModel model) {
		INSTANCE = new Controller(ui, model);
	}

	public DataModel getModel() {
		return model;
	}

	public MainUI getUi() {
		return ui;
	}

	public void uninstallApp(String id) {
		
	}

	public void installApp(String id) {
				
	}
	
	
}
