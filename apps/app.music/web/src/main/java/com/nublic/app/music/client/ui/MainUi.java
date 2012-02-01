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
import com.nublic.app.music.client.datamodel.handlers.PutTagHandler;
import com.nublic.app.music.client.datamodel.handlers.StateChangeHandler;
import com.nublic.app.music.client.datamodel.handlers.TagsChangeHandler;
import com.nublic.app.music.client.ui.album.AlbumPanel;
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
	@UiField AddTagWidget addTagWidget;
	@UiField AddTagWidget addPlaylistWidget;
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
		addPutTagHandler();
	}

	// Handler to handle changes in playlists list
	private void addPlaylistsChangeHandler() {
		model.addPlaylistsChangeHandler(new PlaylistsChangeHandler() {
			@Override
			public void onPlaylistsChange() {
				// Add current playlist, which is always there
				if (playlistIndex.get(Constants.CURRENT_PLAYLIST_ID) == null) {
					PlaylistWidget currentPlaylist = new PlaylistWidget(new Playlist(Constants.CURRENT_PLAYLIST_ID, Constants.CURRENT_PLAYLIST_NAME));
					currentPlaylist.setPlaying(true); // TODO: set playing only when proceeds
					playlistIndex.put(Constants.CURRENT_PLAYLIST_ID, currentPlaylist); // Add playlist to index of playlists
					playlistsPanel.add(currentPlaylist);
				}
				// Add rest of playlists
				List<Playlist> playlistList = model.getPlaylistList();
				for (Playlist p : playlistList) {
					if (playlistIndex.get(p.getId()) == null) {
						PlaylistWidget newPlaylist = new PlaylistWidget(p);
						playlistIndex.put(p.getId(), newPlaylist); // Add playlist to index of playlists
						playlistsPanel.add(newPlaylist);
					}
				}
			}
		});
	}

	// Handler to handle changes in collections list
	private void addTagsChangeHandler() {
		model.addTagsChangeHandler(new TagsChangeHandler() {
			@Override
			public void onTagsChange() {
				List<Tag> tagList = model.getTagList();
				for (Tag t : tagList) {
					if (tagIndex.get(t.getId()) == null) {
						PlaylistWidget newTag = new PlaylistWidget(t);
						tagIndex.put(t.getId(), newTag); // Add tag to index of tags
						tagsPanel.add(newTag);
					}
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
	
	// Handler to notify model that the user has added a tag
	private void addPutTagHandler() {
		addTagWidget.addPutTagHandler(new PutTagHandler() {
			@Override
			public void onPutTag() {
				model.putNewTag(addTagWidget.getText());				
			}
		});
		addPlaylistWidget.addPutTagHandler(new PutTagHandler() {
			@Override
			public void onPutTag() {
				model.putNewPlaylist(addPlaylistWidget.getText());				
			}
		});
	}
	
	public void setSelectedWidget(PlaylistWidget newSelectedWidget) {
		showingPlaylistWidget.setSelected(false);
		showingPlaylistWidget = newSelectedWidget;
		newSelectedWidget.setSelected(true);
	}

	public void refillCentralPanel() {
		State s = model.getState();
		
		switch (s) {
		case ARTIST_ALBUMS:
			ArtistPanel artPanel = new ArtistPanel();
			artPanel.setArtistList(model.getArtistList());
			mainPanel.setWidget(artPanel);
			break;
		case ALBUM_SONGS:
			AlbumPanel albPanel = new AlbumPanel();
			albPanel.setAlbumList(model.getAlbumList());
			mainPanel.setWidget(albPanel);
			break;
		case SONGS:
			break;
		}
	}
	
	public void playlistRefillCentralPanel() {
		
	}
	
	public void error(String message) {
		ErrorPopup.showError(message);
	}
}
