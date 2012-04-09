package com.nublic.app.music.client.ui;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.music.client.Constants;
import com.nublic.app.music.client.datamodel.AlbumInfo;
import com.nublic.app.music.client.datamodel.ArtistInfo;
import com.nublic.app.music.client.datamodel.Controller;
import com.nublic.app.music.client.datamodel.DataModel;
import com.nublic.app.music.client.datamodel.Playlist;
import com.nublic.app.music.client.datamodel.SongInfo;
import com.nublic.app.music.client.datamodel.Tag;
import com.nublic.app.music.client.datamodel.handlers.PlaylistsChangeHandler;
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
	
	@UiField SimplePanel mainPanel;
	@UiField NavigationPanel navigationPanel;
	@UiField(provided=true) Widget _player;
//	@UiField HorizontalPanel playerPanel;
	NublicPlayer player;
	DataModel model;
	
	boolean currentPlaylistBeingShown = false;

	public MainUi(DataModel model) {
		createPlayer();

		initWidget(uiBinder.createAndBindUi(this));
		
		this.model = model;

		addTagsChangeHandler();
		addPlaylistsChangeHandler();
	}

	private void createPlayer() {
		_player = NublicPlayer.create();
//		if (_player instanceof NublicPlayer) {
//			player = (NublicPlayer) _player;
//			player.addErrorHandler(new ErrorHandler() {
//				@Override
//				public void onError(ErrorEvent event) {
//					Timer t = new Timer() {
//						@Override
//						public void run() {
//							Window.alert("Creating new player...");
//							_player = NublicPlayer.create();
//							player = (NublicPlayer) _player;
//							playerPanel.clear();
//							playerPanel.add(player);
//						}
//					};
//					t.schedule(1000);
//				}
//			});
//		} else {
//			player = null;
//		}
		player = _player instanceof NublicPlayer ? (NublicPlayer) _player : null;
	}

	// Handler to handle changes in playlists list
	private void addPlaylistsChangeHandler() {
		model.addPlaylistsChangeHandler(new PlaylistsChangeHandler() {
			@Override
			public void onPlaylistsChange(PlaylistsChangeEvent event) {
				switch (event.getType()) {
				case PLAYLISTS_ADDED:
					for (Playlist p : event.getInvolvedSet()) {
						navigationPanel.addPlaylist(p.getName(), p.getId());
					}
					break;
				case PLAYLISTS_REMOVED:
					for (Playlist p : event.getInvolvedSet()) {
						navigationPanel.removePlaylist(p.getId());
					}
					break;
				}
			}
		});
	}

	// Handler to handle changes in collections list
	private void addTagsChangeHandler() {
		model.addTagsChangeHandler(new TagsChangeHandler() {
			@Override
			public void onTagsChange(TagsChangeEvent event) {
				switch (event.getType()) {
				case TAGS_ADDED:
					for (Tag t : event.getInvolvedSet()) {
						navigationPanel.addCollection(t.getName(), t.getId());
					}
					break;
				case TAGS_REMOVED:
					for (Tag t : event.getInvolvedSet()) {
						navigationPanel.removeCollection(t.getId());
					}
					break;
				}
			}
		});
	}

	public void setSelectedCollection(String collectionId) {
		if (collectionId == null) {
			navigationPanel.selectAllMusic();
		} else {
			navigationPanel.selectCollection(collectionId);
		}
		currentPlaylistBeingShown = false;
	}
	private void setSelectedPlaylist(String playlistId) {
		if (playlistId == null) {
			navigationPanel.selectAllMusic();
		} else {
			navigationPanel.selectPlaylist(playlistId);
			
			// Only to know when are we shoing current playlist
			if (playlistId.equals(Constants.CURRENT_PLAYLIST_ID)) {
				currentPlaylistBeingShown = true;
			} else {
				currentPlaylistBeingShown = false;
			}
		}
	}
	
	public void showAlbumList(List<AlbumInfo> albumList, String artistId, String collectionId) {
		AlbumPanel albPanel = new AlbumPanel(artistId, collectionId);
		albPanel.setAlbumList(albumList);
		setMainWidget(albPanel);
	}
	
	public void showArtistList(List<ArtistInfo> answerList, String collectionId) {
		setSelectedCollection(collectionId);
		
		ArtistPanel artPanel = new ArtistPanel(collectionId);
		artPanel.setArtistList(answerList);
		setMainWidget(artPanel);
	}
	
	public void showSongList(int total, int from, int to, List<SongInfo> answerList, String albumId, String collectionId) {
		SongPanel songPanel = new SongPanel(albumId, collectionId);
		songPanel.setSongList(total, from, to, answerList, albumId, collectionId);
		setMainWidget(songPanel);
	}
	
	
	public void showPlaylist(int total, int from, int to, List<SongInfo> answerList, String playlistId) {
		setSelectedPlaylist(playlistId);
		
		PlaylistPanel plPanel = new PlaylistPanel(playlistId);
		plPanel.setSongList(total, from, to, answerList);
		mainPanel.setWidget(plPanel); // Playlists do have a center drop controller
	}

	public void setMainWidget(Widget w) {
		// If panel doesn't have a center drop controller use this method
		Controller.INSTANCE.removeCentreDropController();
		mainPanel.setWidget(w);
	}

	public NublicPlayer getPlayer() {
		if (player == null) {
			error("No player plugin available");
		}
		return player;		
	}

	public void setPlaying(String playlistId) {
		if (playlistId == null) {
			navigationPanel.stop();
		} else {
			navigationPanel.playPlaylist(playlistId);
		}
	}

	public void setPaused(String playlistId) {
		if (playlistId == null) {
			navigationPanel.stop();
		} else {
			navigationPanel.pausePlaylist(playlistId);
		}
	}
	
	public boolean isCurrentPlaylistBeingShown() {
		return currentPlaylistBeingShown;
	}
	
	public void moveRowsInPlaylist(String playlistId, int from, int to) {
		Widget centralWidget = mainPanel.getWidget();
		
		if (centralWidget instanceof PlaylistPanel) {
			PlaylistPanel playlistPanel = (PlaylistPanel) centralWidget;
			playlistPanel.moveRowsInPlaylist(playlistId, from, to);
		}
	}
	
	public void error(String message) {
		ErrorPopup.showError(message);
	}

}
