package com.nublic.app.music.client.datamodel.handlers;

import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.nublic.app.music.client.datamodel.SongInfo;

public interface PlaylistHandler extends EventHandler {
	public void onSongsChange(int total, int from, int to, List<SongInfo> answerList);
}
