package com.nublic.app.photos.web.client.view.navigation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.photos.web.client.controller.AlbumDropController;
import com.nublic.app.photos.web.client.model.CallbackListOfAlbums;
import com.nublic.app.photos.web.client.model.PhotosModel;
import com.nublic.app.photos.web.client.view.MainUi;

public class NavigationPanel extends Composite {
	private static NavigationPanelUiBinder uiBinder = GWT.create(NavigationPanelUiBinder.class);
	interface NavigationPanelUiBinder extends UiBinder<Widget, NavigationPanel> { }
	
	public static final int ALL_ALBUMS = -2;
	public static final int ALL_PHOTOS = -1;

	@UiField HTMLPanel libraryPanel;
	@UiField HTMLPanel albumPanel;
	@UiField AddWidget addAlbum;
	HashMap<Long, TagWidget> albums = new HashMap<Long, TagWidget>();
	TagWidget allAlbums;
	TagWidget allPhotos;
	boolean loaded = false;
	long activeId = -1;
	
	ArrayList<PutTagHandler> putTagHandlers = new ArrayList<PutTagHandler>();
	
	MainUi ui;
	
	public NavigationPanel(MainUi ui) {
		initWidget(uiBinder.createAndBindUi(this));
		this.ui = ui;
		
		this.addAttachHandler(new Handler() {
			@Override
			public void onAttachOrDetach(AttachEvent event) {
				if (event.isAttached()) {
					addAllAlbums();
					addAllPhotos();
					addAddTagsHandlers();
					
					Scheduler.get().scheduleDeferred(new ScheduledCommand() {
						@Override
						public void execute() {
							addExistingAlbums();
						}
					});
				}
			}
		});
	}
	
	// Adding methods
	public void addAllAlbums() {
		allAlbums = new TagWidget("All albums", -2);
		allAlbums.setTargetToken("view=albums");
		libraryPanel.add(allAlbums);
		albums.put(-2L, allAlbums);
	}
	
	public void addAllPhotos() {
		allPhotos = new TagWidget("All photos", -1);
		libraryPanel.add(allPhotos);
		albums.put(-1L, allPhotos);
	}

	public synchronized void addAlbum(String name, long id) {
		TagWidget col = new TagWidget(name, id);
		// Find place to insert the album
		Element nodeToInsertAt = null;
		for (int i = 0; i < albumPanel.getWidgetCount(); i++) {
			if (albumPanel.getWidget(i) instanceof TagWidget) {
				TagWidget w = (TagWidget)albumPanel.getWidget(i);
				if (name.compareToIgnoreCase(w.getText()) < 0) {
					nodeToInsertAt = w.getElement();
					break;
				}
			}
		}
		albumPanel.add(col);
		albumPanel.getElement().insertBefore(col.getElement(), nodeToInsertAt);
		// Add to list
		albums.put(id, col);
		// Add drop controller
		AlbumDropController controller = new AlbumDropController(col);
		ui.getDragController().registerDropController(controller);
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
	public void selectAllAlbums() {
		selectCollection(ALL_ALBUMS);
	}
	
	public void selectAllPhotos() {
		selectCollection(ALL_PHOTOS);
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
