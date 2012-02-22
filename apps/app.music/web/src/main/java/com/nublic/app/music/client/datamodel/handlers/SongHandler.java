package com.nublic.app.music.client.datamodel.handlers;

import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.nublic.app.music.client.datamodel.SongInfo;

public interface SongHandler extends EventHandler {
	public void onSongsChange(int from, int to, List<SongInfo> answerList);
}
