package com.nublic.app.music.client.ui;

import java.util.HashMap;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.music.client.Constants;
import com.nublic.app.music.client.datamodel.DataModel;
import com.nublic.app.music.client.datamodel.Playlist;
import com.nublic.app.music.client.datamodel.State;
import com.nublic.app.music.client.datamodel.Tag;
import com.nublic.app.music.client.datamodel.handlers.PlaylistsChangeHandler;
import com.nublic.app.music.client.datamodel.handlers.StateChangeHandler;
import com.nublic.app.music.client.datamodel.handlers.TagsChangeHandler;
import com.nublic.app.music.client.ui.artist.ArtistPanel;
import com.nublic.util.error.ErrorPopup;
import com.google.gwt.user.client.ui.SimplePanel;

public class MainUi extends Composite {
	private static MainUiUiBinder uiBinder = GWT.create(MainUiUiBinder.class);
	interface MainUiUiBinder extends UiBinder<Widget, MainUi> {}
	
	@UiField VerticalPanel tagsPanel;
	@UiField VerticalPanel playlistsPanel;
	@UiField SimplePanel mainPanel;
	@UiField PlaylistWidget allMusic;
	HashMap<String, PlaylistWidget> tagIndex = new HashMap<String, PlaylistWidget>();
	HashMap<String, PlaylistWidget> playlistIndex = new HashMap<String, PlaylistWidget>();
	PlaylistWidget showingPlaylistWidget;
	DataModel model;

	public MainUi(DataModel model) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.model = model;
		
		allMusic.setSelected(true);
		showingPlaylistWidget = allMusic;
		addTagsChangeHandler();
		addPlaylistsChangeHandler();
		addStateChangeHandler();
	}

	// Handler to handle changes in playlists list
	private void addPlaylistsChangeHandler() {
		model.addPlaylistsChangeHandler(new PlaylistsChangeHandler() {
			@Override
			public void onPlaylistsChange() {
				// Clear panel
				playlistsPanel.clear();
				playlistIndex.clear();
				// Add current playlist, which is always there
				PlaylistWidget currentPlaylist = new PlaylistWidget(new Playlist(Constants.CURRENT_PLAYLIST_ID, Constants.CURRENT_PLAYLIST_NAME));
				currentPlaylist.setPlaying(true);
				playlistIndex.put(Constants.CURRENT_PLAYLIST_ID, currentPlaylist); // Add playlist to index of playlists
				playlistsPanel.add(currentPlaylist);
				// Add rest of playlists
				List<Playlist> playlistList = model.getPlaylistList();
				for (Playlist p : playlistList) {
					PlaylistWidget newPlaylist = new PlaylistWidget(p);
					playlistIndex.put(p.getId(), newPlaylist); // Add playlist to index of playlists
					playlistsPanel.add(newPlaylist);
				}
			}
		});
	}

	// Handler to handle changes in collections list
	private void addTagsChangeHandler() {
		model.addTagsChangeHandler(new TagsChangeHandler() {
			@Override
			public void onTagsChange() {
				tagsPanel.clear();
				tagIndex.clear();
				List<Tag> tagList = model.getTagList();
				for (Tag t : tagList) {
					PlaylistWidget newTag = new PlaylistWidget(t);
					tagIndex.put(t.getId(), newTag); // Add tag to index of tags
					tagsPanel.add(newTag);
				}
			}
		});
	}

	// Handler to handle changes in model states, which derive in showing different screens
	private void addStateChangeHandler() {
		model.addStateChangeHandler(new StateChangeHandler() {
			@Override
			public void onStateChange() {
				Tag showingTag = model.getShowingTag();
				Playlist showingPlaylist = model.getShowingPlaylist();
				if (showingTag != null) {
					// A tag is selected in the model, lets find which one
					PlaylistWidget selectedTag = tagIndex.get(showingTag.getId());
					if (selectedTag != null) {
						if (showingPlaylistWidget != selectedTag) {
							setSelectedWidget(selectedTag);
						}
						refillCentralPanel();
					} else {
						error("Couldn't find collection");
					}
				} else if (showingPlaylist != null) {
					// A playlist is selected in the model, lets find which one
					PlaylistWidget selectedPlaylist = playlistIndex.get(showingPlaylist.getId());
					if (selectedPlaylist != null) {
						if (showingPlaylistWidget != selectedPlaylist) {
							setSelectedWidget(selectedPlaylist);
						}
						playlistRefillCentralPanel();
					} else {
						error("Couldn't find playlist");
					}
				} else {
					// "All music" is selected
					if (showingPlaylistWidget != allMusic) {
						setSelectedWidget(allMusic);
					}
					refillCentralPanel();
				}
			}
		});
	}
	
	public void setSelectedWidget(PlaylistWidget newSelectedWidget) {
		showingPlaylistWidget.setSelected(false);
		showingPlaylistWidget = newSelectedWidget;
		newSelectedWidget.setSelected(true);
	}

	public void error(String message) {
		ErrorPopup.showError(message);
	}

	public void refillCentralPanel() {
		State s = model.getState();
		
		switch (s) {
		case ARTIST_ALBUMS:
			ArtistPanel ap = new ArtistPanel();
			ap.setArtistList(model.getArtistList());
			mainPanel.setWidget(ap);
			break;
		case ALBUM_SONGS:
			break;
		case SONGS:
			break;
		}
	}
	
	public void playlistRefillCentralPanel() {
		
	}
}
