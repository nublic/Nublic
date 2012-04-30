package com.nublic.app.music.client.controller;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.bramosystems.oss.player.core.event.client.PlayStateEvent;
import com.bramosystems.oss.player.core.event.client.PlayStateEvent.State;
import com.bramosystems.oss.player.core.event.client.PlayStateHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.music.client.Constants;
import com.nublic.app.music.client.datamodel.DataModel;
import com.nublic.app.music.client.datamodel.SongInfo;
import com.nublic.app.music.client.datamodel.handlers.MoveSongHandler;
import com.nublic.app.music.client.datamodel.handlers.SavePlaylistSuccessHandler;
import com.nublic.app.music.client.datamodel.handlers.SongHandler;
import com.nublic.app.music.client.ui.MainUi;
import com.nublic.app.music.client.ui.NavigationPanel;
import com.nublic.app.music.client.ui.TagKind;
import com.nublic.app.music.client.ui.album.AlbumImagePanel;
import com.nublic.app.music.client.ui.artist.AlbumInArtist;
import com.nublic.app.music.client.ui.artist.ArtistImage;
import com.nublic.app.music.client.ui.dnd.AlbumDragController;
import com.nublic.app.music.client.ui.dnd.ArtistDragController;
import com.nublic.app.music.client.ui.dnd.DraggableSong;
import com.nublic.app.music.client.ui.dnd.LeftAlbumDropController;
import com.nublic.app.music.client.ui.dnd.LeftArtistDropController;
import com.nublic.app.music.client.ui.dnd.LeftSongDropController;
import com.nublic.app.music.client.ui.dnd.ListDropController;
import com.nublic.app.music.client.ui.dnd.SongDragController;
import com.nublic.app.music.client.ui.player.NublicPlayer;
import com.nublic.util.widgets.MessagePopup;
import com.nublic.util.widgets.PopupButton;
import com.nublic.util.widgets.PopupButtonHandler;
import com.nublic.util.widgets.PopupColor;
import com.nublic.util.widgets.TextPopup;

public class Controller extends URLController {
	public static Controller INSTANCE = null;
	// DataModel model;
	// MainUi ui;
	
	// Depending on what is being played
	String playingPlaylistId = Constants.CURRENT_PLAYLIST_ID;
	
	// Drag and drop support
	SongDragController songDragController = new SongDragController();  // Add only DraggableSong widgets to it
	AlbumDragController albumDragController = new AlbumDragController(); // Add only HasAlbumId widgets to it (AlbumInArtist and ..)
	ArtistDragController artistDragController = new ArtistDragController(); // Add only HasArtistId widgets
	ListDropController centerDropController = null;
	LeftSongDropController leftSongDropController = null;
	LeftAlbumDropController leftAlbumDropController = null;
	LeftArtistDropController leftArtistDropController = null;
	List<Widget> draggableSongWidgets = new ArrayList<Widget>();
	
	public static void create(DataModel model, MainUi ui) {
		if (INSTANCE == null) {
			INSTANCE = new Controller(model, ui);
		}
	}
	
	private Controller(DataModel model, MainUi ui) {
		super(model, ui);
		
		addPlayHandler();
	}
	
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

	// Getters and setters of singletones
	public NublicPlayer getPlayer() { return ui.getPlayer(); }
	public String getPlayingPlaylistId() { return playingPlaylistId; }
	public void setPlayingPlaylistId(String playingPlaylistId) { this.playingPlaylistId = playingPlaylistId; }
	public DataModel getModel() { return model; }
	public void setModel(DataModel model) { this.model = model; }

	// +++++ Drag and drop stuff +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	public void makeDraggable(DraggableSong w) {
		draggableSongWidgets.add(w);
		songDragController.makeDraggable(w);
	}
	
	public void makeDraggable(AlbumInArtist w) {
//		draggableSongWidgets.add(w);
		albumDragController.makeDraggable(w);
	}
	
	public void makeDraggable(AlbumImagePanel w) {
		albumDragController.makeDraggable(w);
	}
	
	public void makeDraggable(ArtistImage w) {
		artistDragController.makeDraggable(w);
	}
	
	public void createCenterDropController(Panel dropTarget, String playlistId) {
		if (centerDropController != null) {
			// Remove old drop controller
			songDragController.unregisterDropController(centerDropController);
		}
		// Create new drop controller
		centerDropController = new ListDropController(dropTarget, playlistId);
		songDragController.registerDropController(centerDropController);
		
		
		// When new drop controller is created for central panel we assume old draggable widgets no longer exists
		// And we remove them to avoid memory leaks. TODO: It fails, that's why it is commented
//		for (Widget w : draggableWidgets) {
//			songDragController.makeNotDraggable(w);
//		}
//		draggableWidgets.clear();
	}
	
	public void removeCenterDropController() {
		if (centerDropController != null) {
			songDragController.unregisterDropController(centerDropController);
			centerDropController = null;
		}
	}
	
	public void createLeftSongDropController(NavigationPanel navigationPanel) {
		if (leftSongDropController != null) {
			// Remove old drop controller
			songDragController.unregisterDropController(leftSongDropController);
		}
		leftSongDropController = new LeftSongDropController(navigationPanel);
		songDragController.registerDropController(leftSongDropController);
	}

	public void createLeftAlbumDropController(NavigationPanel navigationPanel) {
		if (leftAlbumDropController != null) {
			// Remove old drop controller
			albumDragController.unregisterDropController(leftAlbumDropController);
		}
		leftAlbumDropController = new LeftAlbumDropController(navigationPanel);
		albumDragController.registerDropController(leftAlbumDropController);
	}
	
	public void createLeftArtistDropController(NavigationPanel navigationPanel) {
		if (leftArtistDropController != null) {
			// Remove old drop controller
			artistDragController.unregisterDropController(leftArtistDropController);
		}
		leftArtistDropController = new LeftArtistDropController(navigationPanel);
		artistDragController.registerDropController(leftArtistDropController);
	}
	
	// +++++ Utils to music reproduction +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	public void setPlayingList(String playlistId, SongHandler sh) {
		ui.getPlayer().clearNublicPlaylist();
		playingPlaylistId = playlistId;
		model.askForPlaylistSongs(0, 32000, playlistId, sh, false);
	}
	
	public void setPlayingList(String playlistId) {
		if (!playlistId.equals(playingPlaylistId)) {
			setPlayingList(playlistId, new SongHandler() {
				@Override
				public void onSongsChange(int total, int from, int to, List<SongInfo> answerList) {
					// Load the new playlist
					ui.getPlayer().addSongsToPlaylist(answerList);
				}
			});
		}
	}
	
	public void setPlayingListAndPlay(String playlistId, final int row) {
		if (!playlistId.equals(playingPlaylistId)) {
			setPlayingList(playlistId, new SongHandler() {
				@Override
				public void onSongsChange(int total, int from, int to, List<SongInfo> answerList) {
					// Load the new playlist and play
					ui.getPlayer().addSongsToPlaylist(answerList);
					ui.getPlayer().playSong(row);
				}
			});
		} else {
			ui.getPlayer().play(row);
		}
		
	}
	
	// Plays a playlist
	public void play(final String playlistId) {
		if (!playlistId.equals(playingPlaylistId)) {
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
		} else {
			ui.getPlayer().play(0);
		}
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
	
	public void addAtEndOfPlaylist(final String playlistId, String artistId, String albumId, String collectionId) {
		model.askForSongs(0, 32000, albumId, artistId, collectionId, new SongHandler() {
			@Override
			public void onSongsChange(int total, int from, int to, List<SongInfo> answerList) {
				addAtEndOfPlaylist(playlistId, answerList);
			}
		}, false);
	}
	
	public void addAtEndOfPlaylist(String playlistId, SongInfo s) {
		model.addToPlaylist(playlistId, s);
		if (playlistId.equals(playingPlaylistId)) {
			ui.getPlayer().addSongToPlaylist(s);
		}
	}
	
	public void addAtEndOfPlaylist(String playlistId, List<SongInfo> songList) {
		model.addToPlaylist(playlistId, songList);
		if (playlistId.equals(playingPlaylistId)) {
			ui.getPlayer().addSongsToPlaylist(songList);
		}
	}
	
	public void addAtEndOfPlayingPlaylist(List<SongInfo> songList) {
		addAtEndOfPlaylist(playingPlaylistId, songList);
	}

	public void addAtEndOfPlayingPlaylist(SongInfo s) {
		addAtEndOfPlaylist(playingPlaylistId, s);
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
				State s = ui.getPlayer().getState();
				if (s != null) {
					switch (s) {
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
		if (tagKind == TagKind.PLAYLIST && id.equals(Constants.CURRENT_PLAYLIST_ID)) {
			model.clearCurrentPlaylist();
			if (isBeingPlayed(Constants.CURRENT_PLAYLIST_ID)) {
				ui.getPlayer().clearNublicPlaylist();
			}
			model.askForPlaylistSongs(Constants.CURRENT_PLAYLIST_ID, new MyPlaylistHandler(Constants.CURRENT_PLAYLIST_ID), true);
		} else {
			EnumSet<PopupButton> set = EnumSet.of(PopupButton.DELETE, PopupButton.CANCEL);
			final MessagePopup confirmDeletion = new MessagePopup(Constants.I18N.confirmDeletionTitle(), Constants.I18N.confirmDeletionInfo(), set);
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
	}
	
	// Useful auxiliar method for check if a playlist is being played
	public boolean isBeingPlayed(String playlistId) {
		return playlistId.equals(Controller.INSTANCE.getPlayingPlaylistId());
	}

	
	public void moveSongInPlaylist(String playlistId, int draggingRow, int targetRow) {
		// IGNORE OTHERS! They are moving the song before itself or after itself, which leaves it at the same position
		if (!(draggingRow == targetRow || draggingRow +1 == targetRow)) {
			model.moveSongInPlaylist(playlistId, draggingRow, targetRow, new MoveSongHandler() {
				@Override
				public void onSongMoved(String playlistId, int from, int to) {
					if (isBeingPlayed(playlistId)) {
						// if playlist is being played, move it as well inside player
						ui.getPlayer().reorderNublicPlaylist(from, to);
					}
					ui.moveRowsInPlaylist(playlistId, from, to);
					// This has to be done afterwards as it uses getPlaylistIndex() from player, which get updated with player reordering
				}
			});
		}
	}
	
	// +++ Collections handle ++++++++++++++++++++++++++++++++++++++++
	public void addToCollection(String collectionId, SongInfo song) {
		model.addToCollection(collectionId, song);
	}

	public void addToCollection(final String targetCollectionId, String artistId, String albumId, String collectionId) {
		model.askForSongs(0, 32000, albumId, artistId, collectionId, new SongHandler() {
			@Override
			public void onSongsChange(int total, int from, int to, List<SongInfo> answerList) {
				model.addToCollection(targetCollectionId, answerList);
			}
		}, false);
	}
}
