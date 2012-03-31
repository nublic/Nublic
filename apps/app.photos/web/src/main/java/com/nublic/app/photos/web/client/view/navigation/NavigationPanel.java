package com.nublic.app.photos.web.client.view.navigation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.photos.web.client.model.CallbackListOfAlbums;
import com.nublic.app.photos.web.client.model.PhotosModel;

public class NavigationPanel extends Composite {
	private static NavigationPanelUiBinder uiBinder = GWT.create(NavigationPanelUiBinder.class);
	interface NavigationPanelUiBinder extends UiBinder<Widget, NavigationPanel> { }

	@UiField HTMLPanel libraryPanel;
	@UiField HTMLPanel albumPanel;
	@UiField AddWidget addAlbum;
	HashMap<Long, TagWidget> albums = new HashMap<Long, TagWidget>();
	TagWidget allPhotos;
	boolean loaded = false;
	long activeId = -1;
	
	ArrayList<PutTagHandler> putTagHandlers = new ArrayList<PutTagHandler>();
	
	public NavigationPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.addAttachHandler(new Handler() {
			@Override
			public void onAttachOrDetach(AttachEvent event) {
				if (event.isAttached()) {
					addAllPhotos();
					addExistingAlbums();
					addAddTagsHandlers();
				}
			}
		});
	}
	
	// Adding methods
	public void addAllPhotos() {
		allPhotos = new TagWidget("All photos", -1);
		libraryPanel.add(allPhotos);
		albums.put(-1L, allPhotos);
	}

	public void addAlbum(String name, long id) {
		TagWidget col = new TagWidget(name, id);
		albumPanel.add(col);
		albums.put(id, col);
	}
	
	// Removing methods
	public void removeAlbum(long id) {
		TagWidget tagToRemove = albums.get(id);
		if (activeId == id) {
			selectAllPhotos();
		}
		tagToRemove.removeFromParent();
	}
	
	// Selecting methods
	public void selectAllPhotos() {
		selectCollection(-1);
	}

	public synchronized void selectCollection(long id) {
		if (albums.get(activeId) != null) {
			albums.get(activeId).select(false);
		}
		activeId = id;
		if (albums.get(activeId) != null) {
			albums.get(activeId).select(true);
		}
	}
	
	public void addPutTagHandler(PutTagHandler h) {
		putTagHandlers.add(h);
	}
	
	// Handler to notify model that the user has added a tag
	private void addAddTagsHandlers() {
		addAlbum.addPutTagHandler(new PutTagHandler() {
			@Override
			public void onPutTag(String newTagName) {
				for (PutTagHandler h : putTagHandlers) {
					h.onPutTag(newTagName);
				}
			}
		});
	}
	
	public void addExistingAlbums() {
		PhotosModel.get().albums(new CallbackListOfAlbums() {
			
			@Override
			public void list(Map<Long, String> albums) {
				for (Map.Entry<Long, String> album : albums.entrySet()) {
					addAlbum(album.getValue(), album.getKey());
				}
				selectCollection(activeId);
			}
			
			@Override
			public void error() {
				// Do nothing
			}
		});
	}

}
