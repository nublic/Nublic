package com.nublic.app.photos.web.client.controller;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.photos.web.client.Constants;
import com.nublic.app.photos.web.client.model.AlbumOrder;
import com.nublic.app.photos.web.client.model.CallbackOneAlbum;
import com.nublic.app.photos.web.client.model.PhotoInfo;
import com.nublic.app.photos.web.client.model.PhotosModel;
import com.nublic.app.photos.web.client.view.MainUi;
import com.nublic.app.photos.web.client.view.album.ShowAllAlbumsWidget;
import com.nublic.app.photos.web.client.view.album.ShowAsCellsWidget;
import com.nublic.app.photos.web.client.view.album.ShowAsPresentationWidget;
import com.nublic.app.photos.web.client.view.navigation.PutTagHandler;
import com.nublic.util.widgets.MessagePopup;
import com.nublic.util.widgets.PopupButton;
import com.nublic.util.widgets.PopupButtonHandler;

public class PhotosController implements PutTagHandler {

	private MainUi theUi;
	
	// State information
	boolean initialized;
	long album;
	View view;
	AlbumOrder order;
	
	Set<PhotoInfo> selectedPhotos;
	List<SelectedPhotosChangeHandler> selectionH = new ArrayList<SelectedPhotosChangeHandler>();
	
	public PhotosController(MainUi ui) {	
		this.theUi = ui;
		this.initialized = false;
		this.album = -1;
		this.view = View.AS_ALBUMS;
		this.order = AlbumOrder.DATE_DESC;
		// Initialize drag and drop
		this.selectedPhotos = new HashSet<PhotoInfo>();
		
		this.theUi.getNavigationPanel().addPutTagHandler(this);
		
		PhotosModel.get().addAlbumAddedHandler(new CallbackOneAlbum() {
			@Override
			public void list(long id, String name) {
				theUi.getNavigationPanel().addAlbum(name, id);
			}
			@Override
			public void error() {
				// Do nothing
			}
		});
		
		PhotosModel.get().addAlbumDeletedHandler(new CallbackOneAlbum() {
			@Override
			public void list(long id, String name) {
				theUi.getNavigationPanel().removeAlbum(id);
			}
			@Override
			public void error() {
				// Do nothing
			}
		});
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
			case AS_CELLS:
				w = new ShowAsCellsWidget(this, this.album, this.order);
				break;
			case AS_PRESENTATION:
				w = new ShowAsPresentationWidget(this.album, this.order);
				break;
			default:
				w = new ShowAllAlbumsWidget(this);
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
			if (this.album == -2) {
				theUi.getNavigationPanel().selectAllAlbums();
			} else if (this.album == -1) {
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
				// This is now done as handler
				// theUi.getNavigationPanel().addAlbum(name, id);
			}
			
			@Override
			public void error() {
				MessagePopup popup = new MessagePopup("Error creating album",
						"Check an album with that name doesn't already exist",
						EnumSet.of(PopupButton.OK));
				popup.addButtonHandler(PopupButton.OK, popup.POPUP_CLOSE);
				popup.center();
			}
		});
	}
	
	public Set<PhotoInfo> getSelectedPhotos() {
		return selectedPhotos;
	}
	
	public Set<Long> getSelectedPhotoIds() {
		return Sets.newHashSet(Collections2.transform(selectedPhotos, new Function<PhotoInfo, Long>() {
			@Override
			public Long apply(PhotoInfo p) {
				return p.getId();
			}
		}));
	}
	
	public void addSelectionChangeHandler(SelectedPhotosChangeHandler h) {
		selectionH.add(h);
	}
	
	public void removeSelectionChangeHandler(SelectedPhotosChangeHandler h) {
		selectionH.remove(h);
	}
	
	private void notifySelectionChanged() {
		for (SelectedPhotosChangeHandler h : selectionH) {
			h.selectedPhotosChanged(selectedPhotos);
		}
	}
	
	public void clearSelection() {
		selectedPhotos.clear();
		notifySelectionChanged();
	}
	
	public void select(PhotoInfo photo) {
		selectedPhotos.add(photo);
		notifySelectionChanged();
	}
	
	public void unselect(PhotoInfo photo) {
		selectedPhotos.remove(photo);
		notifySelectionChanged();
	}
	
	public void deleteAlbum(final long id) {
		final MessagePopup popup = new MessagePopup(Constants.I18N.deleteAlbum(),
				Constants.I18N.deleteAlbumText(),
				EnumSet.of(PopupButton.DELETE, PopupButton.CANCEL));
		popup.addButtonHandler(PopupButton.DELETE, new PopupButtonHandler() {
			@Override
			public void onClicked(PopupButton button, ClickEvent event) {
				PhotosModel.get().deleteAlbum(id, new CallbackOneAlbum() {
					
					@Override
					public void list(long id, String name) {
						// Move to initial view
						History.newItem("");
					}
					
					@Override
					public void error() {
						// Do nothing
					}
				});
				popup.hide();
			}
		});
		popup.setInnerHeight(140);
		popup.center();
	}
	
	public void changeTitle(String s) {
		if (s == null || s.equals("")) {
			Window.setTitle(Constants.I18N.windowTitle());
		} else {
			Window.setTitle(Constants.I18N.windowTitlePhoto(s));
		}
	}
}
