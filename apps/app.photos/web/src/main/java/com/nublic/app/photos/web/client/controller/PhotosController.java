package com.nublic.app.photos.web.client.controller;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.photos.web.client.model.AlbumOrder;
import com.nublic.app.photos.web.client.model.CallbackOneAlbum;
import com.nublic.app.photos.web.client.model.PhotosModel;
import com.nublic.app.photos.web.client.view.MainUi;
import com.nublic.app.photos.web.client.view.album.ShowAsCellsWidget;
import com.nublic.app.photos.web.client.view.album.ShowAsPresentationWidget;
import com.nublic.app.photos.web.client.view.navigation.PutTagHandler;
import com.nublic.util.widgets.MessagePopup;
import com.nublic.util.widgets.PopupButton;

public class PhotosController implements PutTagHandler {

	private MainUi theUi;
	
	// State information
	boolean initialized;
	long album;
	View view;
	AlbumOrder order;
	
	Set<Long> selectedPhotos;
	
	public PhotosController(MainUi ui) {	
		this.theUi = ui;
		this.initialized = false;
		this.album = -1;
		this.view = View.AS_CELLS;
		this.order = AlbumOrder.TITLE_ASC;
		// Initialize drag and drop
		this.selectedPhotos = new HashSet<Long>();
		
		this.theUi.getNavigationPanel().addPutTagHandler(this);
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
				w = new ShowAsPresentationWidget(this.album, this.order);
				break;
			default:
				w = new ShowAsCellsWidget(this, this.album, this.order);
				break;
			}
			// Unselect everything
			clearSelection();
			// Show the widget
			theUi.setInnerWidget(w);
			if (view == View.AS_PRESENTATION) {
				((ShowAsPresentationWidget)theUi.getInnerWidget()).setPosition(params.getPhotoPosition());
			}
			// Select the album
			if (this.album == -1) {
				theUi.getNavigationPanel().selectAllPhotos();
			} else {
				theUi.getNavigationPanel().selectCollection(this.album);
			}
		} else {
			if (view == View.AS_PRESENTATION) {
				((ShowAsPresentationWidget)theUi.getInnerWidget()).setPosition(params.getPhotoPosition());
			}
		}
	}
	
	public long getCurrentAlbumId() {
		return this.album;
	}

	@Override
	public void onPutTag(String newTagName) {
		PhotosModel.get().newAlbum(newTagName, new CallbackOneAlbum() {
			
			@Override
			public void list(long id, String name) {
				theUi.getNavigationPanel().addAlbum(name, id);
			}
			
			@Override
			public void error() {
				MessagePopup popup = new MessagePopup("Error creating album",
						"Check an album with that name doesn't already exist",
						EnumSet.of(PopupButton.OK));
				popup.center();
			}
		});
	}
	
	public Set<Long> getSelectedPhotos() {
		return selectedPhotos;
	}
	
	public void clearSelection() {
		selectedPhotos.clear();
	}
	
	public void select(long id) {
		selectedPhotos.add(id);
	}
	
	public void unselect(long id) {
		selectedPhotos.remove(id);
	}
}
