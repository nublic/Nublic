package com.nublic.app.photos.mobile.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Timer;
import com.gwtmobile.ui.client.page.Page;
import com.gwtmobile.ui.client.utils.Utils;
import com.nublic.app.photos.mobile.client.ui.MainUi;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class PhotosApp implements EntryPoint {

	public static MainUi mainUi;

	@Override
	public void onModuleLoad() {
		new Timer() {
			@Override
			public void run() {
				if (mainUi == null) {
					Utils.Console("Loading main ui...");
					mainUi = new MainUi();
					Page.load(mainUi);
				} else {
					this.cancel();
				}
			}
		}.scheduleRepeating(50);
	}

}
