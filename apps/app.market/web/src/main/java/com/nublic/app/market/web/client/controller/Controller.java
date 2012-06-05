package com.nublic.app.market.web.client.controller;

import com.nublic.app.market.web.client.model.AppStatus;
import com.nublic.app.market.web.client.model.DataModel;
import com.nublic.app.market.web.client.model.handlers.InstallActionHandler;
import com.nublic.app.market.web.client.model.messages.GenericInstallMessage;
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

	public void installApp(final String id) {
		GenericInstallMessage.sendInstallMessage(id, new MyInstallActionHandler(id));
	}

	public void uninstallApp(final String id) {
		GenericInstallMessage.sendUninstallMessage(id, new MyInstallActionHandler(id));
	}
	
	private class MyInstallActionHandler implements InstallActionHandler {
		String id;
		
		public MyInstallActionHandler(String id) {
			this.id = id;
		}
		
		@Override
		public void actionSuccessful(AppStatus newStatus) {
			model.changeAppStatus(id, newStatus);
			ui.changeAppStatus(id, newStatus);
		}
	}

}
