package com.nublic.app.music.client.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.music.client.Constants;
import com.nublic.app.music.client.datamodel.AlbumInfo;
import com.nublic.app.music.client.datamodel.ArtistInfo;
import com.nublic.app.music.client.datamodel.DataModel;
import com.nublic.app.music.client.datamodel.Playlist;
import com.nublic.app.music.client.datamodel.SongInfo;
import com.nublic.app.music.client.datamodel.Tag;
import com.nublic.app.music.client.datamodel.handlers.PlaylistsChangeHandler;
import com.nublic.app.music.client.datamodel.handlers.PutTagHandler;
import com.nublic.app.music.client.datamodel.handlers.TagsChangeHandler;
import com.nublic.app.music.client.ui.album.AlbumPanel;
import com.nublic.app.music.client.ui.artist.ArtistPanel;
import com.nublic.app.music.client.ui.player.NublicPlayer;
import com.nublic.app.music.client.ui.playlist.PlaylistPanel;
import com.nublic.app.music.client.ui.song.SongPanel;
import com.nublic.util.error.ErrorPopup;

public class MainUi extends Composite {
	private static MainUiUiBinder uiBinder = GWT.create(MainUiUiBinder.class);
	interface MainUiUiBinder extends UiBinder<Widget, MainUi> {}
	
	@UiField VerticalPanel tagsPanel;
	@UiField VerticalPanel playlistsPanel;
	@UiField SimplePanel mainPanel;
	@UiField PlaylistWidget allMusic;
	@UiField AddTagWidget addTagWidget;
	@UiField AddTagWidget addPlaylistWidget;
	@UiField(provided=true) Widget _player = NublicPlayer.create();
	NublicPlayer player;
	HashMap<String, PlaylistWidget> tagIndex = new HashMap<String, PlaylistWidget>();
	HashMap<String, PlaylistWidget> playlistIndex = new HashMap<String, PlaylistWidget>();
	PlaylistWidget showingPlaylistWidget;
	DataModel model;

	public MainUi(DataModel model) {
		_player = NublicPlayer.create();
		player = _player instanceof NublicPlayer ? null : (NublicPlayer)_player;

		initWidget(uiBinder.createAndBindUi(this));
		
		this.model = model;
		
		allMusic.setSelected(true);
		showingPlaylistWidget = allMusic;
		addTagsChangeHandler();
		addPlaylistsChangeHandler();
		addPutTagHandler();
	}

	// Handler to handle changes in playlists list
	private void addPlaylistsChangeHandler() {
		model.addPlaylistsChangeHandler(new PlaylistsChangeHandler() {
			@Override
			public void onPlaylistsChange() {
				// Add current playlist, which is always there
				PlaylistWidget currentPlaylist = playlistIndex.get(Constants.CURRENT_PLAYLIST_ID);
				if (currentPlaylist == null) {
					currentPlaylist = new PlaylistWidget(new Playlist(Constants.CURRENT_PLAYLIST_ID, Constants.CURRENT_PLAYLIST_NAME));
					currentPlaylist.setPlaying(true); // TODO: set playing only when proceeds
					playlistIndex.put(Constants.CURRENT_PLAYLIST_ID, currentPlaylist); // Add playlist to index of playlists
					playlistsPanel.add(currentPlaylist);
				}
				// Add rest of playlists
				List<Playlist> playlistList = model.getPlaylistList();
				Set<Widget> widgetsToRemove = Sets.newHashSet(playlistsPanel);
				widgetsToRemove.remove(currentPlaylist); // We don't want to remove current playlist from the panel
				for (Playlist p : playlistList) {
					PlaylistWidget pw = playlistIndex.get(p.getId());
					if (pw == null) {
						PlaylistWidget newPlaylist = new PlaylistWidget(p);
						playlistIndex.put(p.getId(), newPlaylist); // Add playlist to index of playlists
						playlistsPanel.add(newPlaylist);
					} else {
						widgetsToRemove.remove(pw);
					}
				}
				// Remove deletions
				for (Widget del : widgetsToRemove) {
					playlistsPanel.remove(del);
					playlistIndex.remove(((PlaylistWidget)del).getId());
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
				Set<Widget> widgetsToRemove = Sets.newHashSet(tagsPanel);
				for (Tag t : tagList) {
					PlaylistWidget w = tagIndex.get(t.getId());
					if (w == null) {
						PlaylistWidget newTag = new PlaylistWidget(t);
						tagIndex.put(t.getId(), newTag); // Add tag to index of tags
						tagsPanel.add(newTag);
					} else {
						widgetsToRemove.remove(w);
					}
				}
				// Remove deletions
				for (Widget del : widgetsToRemove) {
					tagsPanel.remove(del);
					tagIndex.remove(((PlaylistWidget)del).getId());
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
	public void setSelectedCollection(String collectionId) {
		PlaylistWidget newSelected = tagIndex.get(collectionId);
		if (newSelected == null) {
			setSelectedWidget(allMusic);
		} else {
			setSelectedWidget(newSelected);
		}
	}
	private void setSelectedPlaylist(String playlistId) {
		PlaylistWidget newSelected = playlistIndex.get(playlistId);
		if (newSelected == null) {
			setSelectedWidget(allMusic);
		} else {
			setSelectedWidget(newSelected);
		}
	}
	
	public void showAlbumList(List<AlbumInfo> albumList, String artistId, String collectionId) {
		AlbumPanel albPanel = new AlbumPanel(model, artistId, collectionId);
		albPanel.setAlbumList(albumList);
		mainPanel.setWidget(albPanel);
	}
	
	public void showArtistList(List<ArtistInfo> answerList, String collectionId) {
		setSelectedCollection(collectionId);
		
		ArtistPanel artPanel = new ArtistPanel(model, collectionId, showingPlaylistWidget.getText());
		artPanel.setArtistList(answerList);
		mainPanel.setWidget(artPanel);
	}
	
	public void showSongList(int total, int from, int to, List<SongInfo> answerList, String albumId, String collectionId) {
		SongPanel songPanel = new SongPanel(model, albumId, collectionId);
		songPanel.setSongList(total, from, to, answerList, albumId, collectionId);
		mainPanel.setWidget(songPanel);
	}
	
	public void showPlaylist(int total, int from, int to, List<SongInfo> answerList, String playlistId) {
		setSelectedPlaylist(playlistId);
		
		PlaylistPanel plPanel = new PlaylistPanel(model, playlistId, showingPlaylistWidget.getText());
		plPanel.setSongList(total, from, to, answerList, playlistId);
		mainPanel.setWidget(plPanel);
	}

	public void error(String message) {
		ErrorPopup.showError(message);
	}



}
