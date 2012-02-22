package com.nublic.app.music.client.datamodel.handlers;

import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.nublic.app.music.client.datamodel.ArtistInfo;

public interface ArtistHandler extends EventHandler {
	public void onArtistChange(List<ArtistInfo> answerList);
}
