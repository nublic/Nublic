package com.nublic.app.music.client.datamodel;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.bramosystems.oss.player.core.event.client.PlayStateEvent;
import com.bramosystems.oss.player.core.event.client.PlayStateHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.music.client.Constants;
import com.nublic.app.music.client.ParamsHashMap;
import com.nublic.app.music.client.datamodel.handlers.AlbumHandler;
import com.nublic.app.music.client.datamodel.handlers.ArtistHandler;
import com.nublic.app.music.client.datamodel.handlers.MoveSongHandler;
import com.nublic.app.music.client.datamodel.handlers.SavePlaylistSuccessHandler;
import com.nublic.app.music.client.datamodel.handlers.SongHandler;
import com.nublic.app.music.client.ui.MainUi;
import com.nublic.app.music.client.ui.NavigationPanel;
import com.nublic.app.music.client.ui.TagKind;
import com.nublic.app.music.client.ui.dnd.LeftDropController;
import com.nublic.app.music.client.ui.dnd.SongDragController;
import com.nublic.app.music.client.ui.dnd.SongDropController;
import com.nublic.app.music.client.ui.player.NublicPlayer;
import com.nublic.util.widgets.MessagePopup;
import com.nublic.util.widgets.PopupButton;
import com.nublic.util.widgets.PopupButtonHandler;
import com.nublic.util.widgets.PopupColor;
import com.nublic.util.widgets.TextPopup;

public class Controller {
	public static Controller INSTANCE = null;
	DataModel model;
	MainUi ui;
	
	// Depending on what is being played
	String playingPlaylistId = Constants.CURRENT_PLAYLIST_ID;
	
	// Drag and drop support
	SongDragController songDragController = new SongDragController();
	SongDropController centerDropController = null;
	LeftDropController leftDropController = null;
	List<Widget> draggableWidgets = new ArrayList<Widget>();
	
	public static void create(DataModel model, MainUi ui) {
		if (INSTANCE == null) {
			INSTANCE = new Controller(model, ui);
		}
	}
	
	private Controller(DataModel model, MainUi ui) {
		this.ui = ui;
		this.model = model;
		
		addPlayHandler();
	}

	// Getters and setters of singletones
	public NublicPlayer getPlayer() { return ui.getPlayer(); }
	public String getPlayingPlaylistId() { return playingPlaylistId; }
	public void setPlayingPlaylistId(String playingPlaylistId) { this.playingPlaylistId = playingPlaylistId; }
	public DataModel getModel() { return model; }
	public void setModel(DataModel model) { this.model = model; }

	// +++++ Drag and drop stuff +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	public void makeDraggable(Widget w) {
		draggableWidgets.add(w);
		songDragController.makeDraggable(w);
	}
	
	public void createCenterDropController(Panel dropTarget, String playlistId) {
		if (centerDropController != null) {
			// Remove old drop controller
			songDragController.unregisterDropController(centerDropController);
		}
		// Create new drop controller
		centerDropController = new SongDropController(dropTarget, playlistId);
		songDragController.registerDropController(centerDropController);
		
		
		// When new drop controller is created for central panel we assume old draggable widgets no longer exists
		// And we remove them to avoid memory leaks. It fails
//		for (Widget w : draggableWidgets) {
//			songDragController.makeNotDraggable(w);
//		}
//		draggableWidgets.clear();
	}
	
	public void createLeftDropController(NavigationPanel navigationPanel) {
		if (leftDropController != null) {
			// Remove old drop controller
			songDragController.unregisterDropController(leftDropController);
		}
		leftDropController = new LeftDropController(navigationPanel);
		songDragController.registerDropController(leftDropController);
	}
	
	// +++++ Utils to music reproduction +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	public void setPlayingList(String playlistId, SongHandler sh) {
		if (!playlistId.equals(playingPlaylistId)) {
			ui.getPlayer().clearNublicPlaylist();
			playingPlaylistId = playlistId;
			model.askForPlaylistSongs(0, 32000, playlistId, sh, false);
		}
	}
	
	public void setPlayingList(String playlistId) {
		setPlayingList(playlistId, new SongHandler() {
			@Override
			public void onSongsChange(int total, int from, int to, List<SongInfo> answerList) {
				// Load the new playlist
				ui.getPlayer().addSongsToPlaylist(answerList);
			}
		});
	}
	
	public void setPlayingListAndPlay(String playlistId, final int row) {
		setPlayingList(playlistId, new SongHandler() {
			@Override
			public void onSongsChange(int total, int from, int to, List<SongInfo> answerList) {
				// Load the new playlist and play
				ui.getPlayer().addSongsToPlaylist(answerList);
				ui.getPlayer().playSong(row);
			}
		});
		
	}
	
	// Plays a playlist
	public void play(final String playlistId) {
		setPlayingList(playlistId, new SongHandler() {
			@Override
			public void onSongsChange(int total, int from, int to, List<SongInfo> answerList) {
				// Load the new playlist and play
				ui.getPlayer().addSongsToPlaylist(answerList);
				int playlistSize = ui.getPlayer().getPlaylistSize();
				if (playlistSize > 0) {
					if (ui.getPlayer().isShuffleEnabled()) {
						play(Random.nextInt() % playlistSize, playlistId);				
					} else {
						play(0, playlistId);
					}
				}
			}
		});
	}

	public void play(String artistId, String albumId, String collectionId) {
		model.askForSongs(0, 32000, albumId, artistId, collectionId, new SongHandler() {
			@Override
			public void onSongsChange(int total, int from, int to, List<SongInfo> answerList) {
//				setPlayingList(Constants.CURRENT_PLAYLIST_ID);
				playingPlaylistId = Constants.CURRENT_PLAYLIST_ID;
				model.clearCurrentPlaylist();
				model.addToPlaylist(Constants.CURRENT_PLAYLIST_ID, answerList);
				ui.getPlayer().clearNublicPlaylist();
				ui.getPlayer().addSongsToPlaylist(answerList);
				ui.getPlayer().nublicPlay();
			}
		}, false);
	}
	
	public void addAtEnd(String artistId, String albumId, String collectionId) {
		model.askForSongs(0, 32000, albumId, artistId, collectionId, new SongHandler() {
			@Override
			public void onSongsChange(int total, int from, int to, List<SongInfo> answerList) {
				addAtEndOfPlayingPlaylist(answerList);
			}
		}, false);
	}
	
	public void addAtEndOfPlayingPlaylist(List<SongInfo> songList) {
		model.addToPlaylist(playingPlaylistId, songList);
		ui.getPlayer().addSongsToPlaylist(songList);
	}

	public void addAtEndOfPlayingPlaylist(SongInfo s) {
		model.addToPlaylist(playingPlaylistId, s);
		ui.getPlayer().addSongToPlaylist(s);
	}
	
	// Plays a song from a collection
	public void play(SongInfo s) {
		setPlayingList(Constants.CURRENT_PLAYLIST_ID);
		addAtEndOfPlayingPlaylist(s);
		ui.getPlayer().playSong(ui.getPlayer().getNublicPlaylistSize() -1);
	}
	
	// Plays a song from a playlist
	public void play(int row, String playlistId) {
		if (playingPlaylistId != playlistId) {
			setPlayingListAndPlay(playlistId, row);
		} else {
			ui.getPlayer().playSong(row);
		}
	}

	public void saveCurrentPlaylist() {
		// TODO: check that current playlist is not empty
		EnumSet<PopupButton> set = EnumSet.of(PopupButton.CUSTOM, PopupButton.CANCEL);
		final TextPopup tp = new TextPopup("Enter new playlist name", set, "Save", PopupButton.CUSTOM);
		tp.setCustomButtonColor(PopupColor.PRIMARY);
		tp.addButtonHandler(PopupButton.CUSTOM, new PopupButtonHandler() {
			@Override
			public void onClicked(PopupButton button, ClickEvent event) {
				model.saveCurrentPlaylist(tp.getText(), new MySavePlaylistSuccessHandler());
				tp.hide();
			}
		});
		tp.setText("New playlist");
		tp.center();
		tp.selectAndFocus();
	}
	
	private class MySavePlaylistSuccessHandler implements SavePlaylistSuccessHandler {
		@Override
		public void onSaveSuccess(String newPlaylistId) {
			// If we are playing current playlist
			if (isBeingPlayed(Constants.CURRENT_PLAYLIST_ID)) {
				playingPlaylistId = newPlaylistId;
				PlayStateEvent e = ui.getPlayer().getLastEvent();
				if (e != null) {
					switch (e.getPlayState()) {
					case Started:
						ui.setPlaying(newPlaylistId);
						break;
					case Stopped:
						ui.setPaused(newPlaylistId);
						break;
					}
				}
			}
			// If we are showing current playlist
			if (ui.isCurrentPlaylistBeingShown()) {
				History.newItem(Constants.PARAM_PLAYLIST + "=" + newPlaylistId);
			}
			model.clearCurrentPlaylist();
		}
	}
	
	// Deletion method
	public void deleteTag(final String id, final TagKind tagKind) {
		EnumSet<PopupButton> set = EnumSet.of(PopupButton.DELETE, PopupButton.CANCEL);
		final MessagePopup confirmDeletion = new MessagePopup(Constants.CONFIRM_DELETION_TITLE, Constants.CONFIRM_DELETION_INFO, set);
		confirmDeletion.addButtonHandler(PopupButton.DELETE, new PopupButtonHandler() {
			@Override
			public void onClicked(PopupButton button, ClickEvent event) {
				switch (tagKind) {
				case COLLECTION:
					model.deleteTag(id);
					break;
				case PLAYLIST:
					model.deletePlaylist(id);
					break;
				}
				confirmDeletion.hide();
			}
		});
		confirmDeletion.setHeight("175px");
		confirmDeletion.center();
	}
	
	// Useful auxiliar method for check if a playlist is being played
	public boolean isBeingPlayed(String playlistId) {
		return playlistId.equals(Controller.INSTANCE.getPlayingPlaylistId());
	}

	
	public void moveSongInPlaylist(String playlistId, int draggingRow, int targetRow) {
		if (draggingRow == targetRow || draggingRow +1 == targetRow) {
			// IGNORE! They are moving the song before itself or after itself, which leaves it at the same position
		} else {
			// TODO if playlist is being played, move it as well inside player
			model.moveSongInPlaylist(playlistId, draggingRow, targetRow, new MoveSongHandler() {
				@Override
				public void onSongMoved(String playlistId, int from, int to) {
					ui.moveRowsInPlaylist(playlistId, from, to);
				}
			});
		}
//		Window.alert("Moving from " + draggingRow + " to " + targetRow);
	}
	
	// +++++ Handle history state change ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	private void addPlayHandler() {
		if (ui.getPlayer() != null) {
			ui.getPlayer().addPlayStateHandler(new PlayStateHandler() {
				@Override
				public void onPlayStateChanged(PlayStateEvent event) {
					switch (event.getPlayState()) {
					case Paused:
						ui.setPaused(getPlayingPlaylistId());
	            		break;
	            	case Started:
						ui.setPlaying(getPlayingPlaylistId());
	            		break;
	            	case Stopped:
						ui.setPlaying(null);
	            		break;
	            	case Finished:
						ui.setPlaying(null);
	            		break;
					}
				}
			});
		}
	}
	
	// When URL changes this method is called
	public void changeState(ParamsHashMap hmap) {
		String collection = hmap.get(Constants.PARAM_COLLECTION);
		String playlist = hmap.get(Constants.PARAM_PLAYLIST);
		String artist = hmap.get(Constants.PARAM_ARTIST);
		String album = hmap.get(Constants.PARAM_ALBUM);

		if (playlist != null) {
			model.askForPlaylistSongs(playlist, new MyPlaylistHandler(playlist), true);
		} else {
			if (album != null) {
				model.askForSongs(album, artist, collection, new MySongHandler(album, collection), true);
			} else if (artist != null) {
				model.askForAlbums(artist, collection, new MyAlbumHandler(artist, collection), true);
			} else {
				model.askForArtists(collection, new MyArtistHandler(collection), true);
			}
		}
	}
	
	// Song
	class MySongHandler implements SongHandler {
		String albumId;
		String collectionId;	
		public MySongHandler(String albumId, String collection) {
			this.albumId = albumId;
			this.collectionId = collection;
		}
		@Override
		public void onSongsChange(int total, int from, int to, List<SongInfo> answerList) {
			ui.showSongList(total, from, to, answerList, albumId, collectionId);
		}
	}
	
	// Album
	class MyAlbumHandler implements AlbumHandler {
		String artistId;
		String collectionId;	
		public MyAlbumHandler(String artist, String collection) {
			artistId = artist;
			collectionId = collection;
		}
		@Override
		public void onAlbumChange(List<AlbumInfo> answerList) {
			ui.showAlbumList(answerList, artistId, collectionId);
		}
	}
	
	// Artist
	class MyArtistHandler implements ArtistHandler {
		String collectionId;
		public MyArtistHandler(String collection) {
			collectionId = collection;
		}
		@Override
		public void onArtistChange(List<ArtistInfo> answerList) {
			ui.showArtistList(answerList, collectionId);
		}
	}

	// Playlist
	class MyPlaylistHandler implements SongHandler {
		String playlistId;
		public MyPlaylistHandler(String playlistId) {
			this.playlistId = playlistId;
		}
		@Override
		public void onSongsChange(int total, int from, int to, List<SongInfo> answerList) {
			ui.showPlaylist(total, from, to, answerList, playlistId);
		}
	}

}
