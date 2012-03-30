package com.nublic.app.photos.web.client.view.navigation;

import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class NavigationPanel extends Composite {
	private static NavigationPanelUiBinder uiBinder = GWT.create(NavigationPanelUiBinder.class);
	interface NavigationPanelUiBinder extends UiBinder<Widget, NavigationPanel> { }

	@UiField HTMLPanel libraryPanel;
	@UiField HTMLPanel albumPanel;
	@UiField AddWidget addAlbum;
	HashMap<Long, TagWidget> albums = new HashMap<Long, TagWidget>();
	TagWidget allPhotos;
	TagWidget activeTag;
	
	public NavigationPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.addAttachHandler(new Handler() {
			@Override
			public void onAttachOrDetach(AttachEvent event) {
				if (event.isAttached()) {
					addAllPhotos();
					activeTag = allPhotos;
					selectAllPhotos();
					addAddTagsHandlers();
				}
			}
		});
	}
	
	// Adding methods
	public void addAllPhotos() {
		allPhotos = new TagWidget("All photos", -1);
		libraryPanel.add(allPhotos);
	}

	public void addAlbum(String name, long id) {
		TagWidget col = new TagWidget(name, id);
		albumPanel.add(col);
		albums.put(id, col);
	}
	
	// Removing methods
	public void removeAlbum(String id) {
		TagWidget tagToRemove = albums.get(id);
		if (activeTag == tagToRemove) {
			activeTag = allPhotos;
		}
		tagToRemove.removeFromParent();
	}
	
	// Selecting methods
	public void selectAllPhotos() {
		select(allPhotos);
	}

	public void selectCollection(long id) {
		select(albums.get(id));
	}

	public void select(TagWidget e) {
		activeTag.select(false);
		activeTag = e;
		e.select(true);
	}
	
	// Handler to notify model that the user has added a tag
	private void addAddTagsHandlers() {
		addAlbum.addPutTagHandler(new PutTagHandler() {
			@Override
			public void onPutTag(String newTagName) {
				// Do nothing by now
			}
		});
	}

}
