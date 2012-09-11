package com.nublic.app.photos.mobile.client;

import java.util.Map;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Timer;
import com.gwtmobile.ui.client.page.Page;
import com.gwtmobile.ui.client.utils.Utils;
import com.nublic.app.photos.common.model.CallbackListOfAlbums;
import com.nublic.app.photos.common.model.PhotosModel;
import com.nublic.app.photos.mobile.client.ui.MainUi;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class PhotosApp implements EntryPoint {

	public MainUi mainUi;
	public PhotosModel model;

	@Override
	public void onModuleLoad() {
		new Timer() {
			@Override
			public void run() {
				if (mainUi == null) {
					Utils.Console("Loading main ui...");
					load();
				} else {
					this.cancel();
				}
			}
		}.scheduleRepeating(50);
	}

	private void load() {
		// ui
		mainUi = MainUi.create();
		Page.load(mainUi);
		
		// model
		model = PhotosModel.get();
		askForAlbums();
	}

	private void askForAlbums() {
		model.albums(new CallbackListOfAlbums() {
			@Override
			public void list(Map<Long, String> albums) {
				mainUi.setAlbumList(albums);
			}
			@Override
			public void error() {
				// nothing
			}
		});
	}

}
