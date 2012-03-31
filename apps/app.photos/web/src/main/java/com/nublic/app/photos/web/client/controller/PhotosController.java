package com.nublic.app.photos.web.client.controller;

import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.photos.web.client.model.AlbumOrder;
import com.nublic.app.photos.web.client.view.MainUi;
import com.nublic.app.photos.web.client.view.album.ShowAsCellsWidget;

public class PhotosController {

	private MainUi theUi;
	
	// State information
	boolean initialized;
	long album;
	View view;
	AlbumOrder order;
	
	public PhotosController(MainUi ui) {
		this.theUi = ui;
		this.initialized = false;
		this.album = -1;
		this.view = View.AS_CELLS;
		this.order = AlbumOrder.TITLE_ASC;
	}
	
	public void changeTo(ParamsHashMap params) {
		View newView = params.getView() == null ? this.view : params.getView();
		AlbumOrder newOrder = params.getOrder() == null ? this.order : params.getOrder();
		
		if (!initialized || newView != view || newOrder != order || params.getAlbum() != album) {
			// We have to recreate the inside
			initialized = true; // We have already created a widget
			// Write the new attributes
			this.album = params.getAlbum();
			this.view = newView;
			this.order = newOrder;
			// Create the widget
			Widget w;
			switch(this.view) {
			case AS_PRESENTATION:
				w = new ShowAsCellsWidget(this.album, this.order);
				break;
			default:
				w = new ShowAsCellsWidget(this.album, this.order);
				break;
			}
			// Show the widget
			theUi.setInnerWidget(w);
			// Select the album
			if (this.album == -1) {
				theUi.getNavigationPanel().selectAllPhotos();
			} else {
				theUi.getNavigationPanel().selectCollection(this.album);
			}
		}
	}
}
