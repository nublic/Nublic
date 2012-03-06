package com.nublic.app.music.client.datamodel.handlers;

import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.nublic.app.music.client.datamodel.Playlist;

public interface PlaylistsChangeHandler extends EventHandler {
	public void onPlaylistsChange(PlaylistsChangeEvent event);
	
	public class PlaylistsChangeEvent {
		PlaylistsChangeEventType type;
		List<Playlist> involvedSet;
		
		public PlaylistsChangeEvent(PlaylistsChangeEventType type, List<Playlist> involvedSet) {
			this.type = type;
			this.involvedSet = involvedSet;
		}

		public PlaylistsChangeEventType getType() { return type; }
		public List<Playlist> getInvolvedSet() { return involvedSet; }
	}
	
	public enum PlaylistsChangeEventType {
		PLAYLISTS_ADDED,
		PLAYLISTS_REMOVED,
	}
}
