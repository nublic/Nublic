package com.nublic.app.music.client.datamodel.handlers;

import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.nublic.app.music.client.datamodel.AlbumInfo;

public interface AlbumHandler extends EventHandler {
	public void onAlbumChange(List<AlbumInfo> answerList);
}
