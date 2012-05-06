package com.nublic.app.music.client.ui.song;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.music.client.controller.Controller;
import com.nublic.app.music.client.datamodel.SongInfo;
import com.nublic.app.music.client.datamodel.handlers.AddAtEndButtonHandler;
import com.nublic.app.music.client.datamodel.handlers.DeleteButtonHandler;
import com.nublic.app.music.client.datamodel.handlers.EditButtonHandler;
import com.nublic.app.music.client.datamodel.handlers.PlayButtonHandler;
import com.nublic.app.music.client.ui.ButtonLine;
import com.nublic.app.music.client.ui.dnd.DraggableSong;

public abstract class AlbumSongList extends SongList {
	String albumId;
	String artistId;
	String collectionId;
	
	public AlbumSongList(String albumId, String artistId, String collectionId, int numberOfSongs, Panel scrollPanel) {
		super(numberOfSongs, scrollPanel);
		this.albumId = albumId;
		this.artistId = artistId;
		this.collectionId = collectionId;
	}
	
	@Override
	public void askForsongs(int from, int to) {
		Controller.INSTANCE.getModel().askForSongs(from, to, albumId, artistId, collectionId, songHandler);
	}
	
	// +++ Handlers for buttons +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	protected class MyAddAtEndHandler implements AddAtEndButtonHandler {
		SongInfo song;
		public MyAddAtEndHandler(SongInfo s) {
			this.song = s;
		}
		@Override
		public void onAddAtEnd() {
			Controller.INSTANCE.addAtEndOfPlayingPlaylist(song);
		}
	}
	
	protected class MyPlayHandler implements PlayButtonHandler {
		SongInfo song;
		public MyPlayHandler(SongInfo s) {
			this.song = s;
		}
		@Override
		public void onPlay() {
			Controller.INSTANCE.play(song);
		}
	}
	
	protected class MyEditHandler implements EditButtonHandler {
		@Override
		public void onEdit() {
			// TODO: Edit
			
		}
	}
	
	protected class MyDeleteHandler implements DeleteButtonHandler {
		SongInfo song;
		int row;
		public MyDeleteHandler(SongInfo s, int row) {
			this.song = s;
			this.row = row;
		}
		@Override
		public void onDelete() {
			// Remove from server
			Controller.INSTANCE.getModel().removeFromCollection(collectionId, song.getId(), new DeleteButtonHandler() {
				@Override
				public void onDelete() {
					// Remove from interface
					grid.removeRow(row);
					updateRangesFromDelete(row);
					rearrangeRows(row, grid.getRowCount() -1);
					updateEmptyness();
				}
			});
		}
	}

	public void rearrangeRows(int from, int to) {
		for (int i = from; i <= to ; i++) {
			Widget w = grid.getWidget(i, 0);
			if (w instanceof SongLocalizer) {
				((SongLocalizer)w).setPosition(i);
			} else {
				((DraggableSong)w).setRow(i);
				SongInfo s = ((DraggableSong)w).getSong();
				ButtonLine bl = (ButtonLine)((HorizontalPanel)grid.getWidget(i, 1)).getWidget(1);
				bl.setDeleteButtonHandler(new MyDeleteHandler(s, i));
			}
		}
	}
}
