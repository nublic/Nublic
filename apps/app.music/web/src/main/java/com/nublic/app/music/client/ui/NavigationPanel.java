package com.nublic.app.music.client.ui;

import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.music.client.Constants;
import com.nublic.app.music.client.Resources;
import com.nublic.app.music.client.controller.Controller;
import com.nublic.app.music.client.datamodel.Playlist;
import com.nublic.app.music.client.datamodel.handlers.PutTagHandler;

public class NavigationPanel extends Composite {
	private static NavigationPanelUiBinder uiBinder = GWT.create(NavigationPanelUiBinder.class);
	interface NavigationPanelUiBinder extends UiBinder<Widget, NavigationPanel> { }

	@UiField HTMLPanel libraryPanel;
	@UiField HTMLPanel collectionPanel;
	@UiField HTMLPanel playlistPanel;
	@UiField AddWidget addCollection;
	@UiField AddWidget addPlaylist;
	HashMap<String, TagWidget> collections = new HashMap<String, TagWidget>();
	HashMap<String, TagWidget> playlists = new HashMap<String, TagWidget>();
	TagWidget allMusic;
	TagWidget activeTag;
	TagWidget playingTag;
	
	public NavigationPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.addAttachHandler(new Handler() {
			@Override
			public void onAttachOrDetach(AttachEvent event) {
				if (event.isAttached()) {
					addAllMusic();
					createCurrentPlaylist();
					activeTag = allMusic;
					selectAllMusic();
					addAddTagsHandlers();
					Controller.INSTANCE.createLeftSongDropController(NavigationPanel.this);
					Controller.INSTANCE.createLeftAlbumDropController(NavigationPanel.this);
					Controller.INSTANCE.createLeftArtistDropController(NavigationPanel.this);
				}
			}
		});
	}	

	public void createCurrentPlaylist() {
		TagWidget pw = new TagWidget(TagKind.PLAYLIST, Constants.CURRENT_PLAYLIST_NAME, Constants.CURRENT_PLAYLIST_ID, new Image(Resources.INSTANCE.save()));
		pw.addIconAction(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Controller.INSTANCE.saveCurrentPlaylist();
			}
		});
		playlistPanel.add(pw);
		playlists.put(Constants.CURRENT_PLAYLIST_ID, pw);

		// Add it to the model
		Playlist current = new Playlist(Constants.CURRENT_PLAYLIST_ID, Constants.CURRENT_PLAYLIST_NAME);
		Controller.INSTANCE.getModel().getPlaylistCache().put(current.getId(), current);
	}
	
	// Adding methods
	public void addAllMusic() {
		allMusic = new TagWidget(null, "All music", "");
		libraryPanel.add(allMusic);
	}

	public void addCollection(String name, String id) {
		TagWidget col = new TagWidget(TagKind.COLLECTION, name, id);
		collectionPanel.add(col);
		collections.put(id, col);
	}
	
	public void addPlaylist(String name, String id) {
		TagWidget play = new TagWidget(TagKind.PLAYLIST, name, id);
		playlistPanel.add(play);
		playlists.put(id, play);
	}
	
	// Removing methods
	public void removeCollection(String id) {
		TagWidget tagToRemove = collections.get(id);
		if (activeTag == tagToRemove) {
			activeTag = allMusic;
		}
		tagToRemove.removeFromParent();
	}
	
	public void removePlaylist(String id) {
		TagWidget tagToRemove = playlists.get(id);
		if (activeTag == tagToRemove) {
			activeTag = allMusic;
		}
		tagToRemove.removeFromParent();
	}
	
	// Getters
	public TagWidget getSelectedTag() {
		return activeTag;
	}
	
	public TagWidget getIntersectionTag(int mouseX, int mouseY) {
		// is all music
		if (intersects(mouseX, mouseY, allMusic)) {
			return allMusic;
		}
		// is a collection
		for (TagWidget col : collections.values()) {
			if (intersects(mouseX, mouseY, col)) {
				return col;
			}
		}
		// is a playlist
		for (TagWidget pl : playlists.values()) {
			if (intersects(mouseX, mouseY, pl)) {
				return pl;
			}
		}
		return null;
	}
	
	public boolean intersects(int mouseX, int mouseY, TagWidget tw) {
		// Only checking Y coordinate..
		return (tw.getAbsoluteTop() < mouseY && mouseY < tw.getAbsoluteTop() + tw.getOffsetHeight());
	}
	
	// Selecting methods	
	public void selectAllMusic() {
		select(allMusic);
	}

	public void selectCollection(String id) {
		select(collections.get(id));
	}

	public void selectPlaylist(String id) {
		select(playlists.get(id));
	}

	public void select(TagWidget e) {
		if (activeTag != null) {
			activeTag.select(false);
		}
		activeTag = e;
		if (e != null) {
			e.select(true);
		}
	}

	// Playing methods
	public void playPlaylist(String id) {
		stop();
		playingTag = playlists.get(id);
		playingTag.play();
	}
	public void pausePlaylist(String id) {
		stop();
		playingTag = playlists.get(id);
		playingTag.pause();
	}
	
	public void stop() {
		if (playingTag != null) {
			playingTag.stop();
		}
	}
	
	public void stopPlaylist(String id) {
		playlists.get(id).stop();
	}
	
	// Handler to notify model that the user has added a tag
	private void addAddTagsHandlers() {
		addCollection.addPutTagHandler(new PutTagHandler() {
			@Override
			public void onPutTag(String newTagName) {
				Controller.INSTANCE.getModel().putNewTag(newTagName);
			}
		});
		addPlaylist.addPutTagHandler(new PutTagHandler() {
			@Override
			public void onPutTag(String newTagName) {
				Controller.INSTANCE.getModel().putNewPlaylist(newTagName);
			}
		});
	}

}
