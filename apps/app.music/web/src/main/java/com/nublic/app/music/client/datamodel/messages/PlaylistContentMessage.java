package com.nublic.app.music.client.datamodel.messages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.nublic.app.music.client.datamodel.DataModel;
import com.nublic.app.music.client.datamodel.State;
import com.nublic.util.messages.Message;

//GET /playlist/:id/:order/:asc-desc/:start/:length
//* Gets all the songs from the playlist
//* Return: as in /songs

public class PlaylistContentMessage extends Message {
	DataModel model;
	String id;

	public PlaylistContentMessage(DataModel model, String id) {
		this.model = model;
		this.id = id;
	}
	
	@Override
	public String getURL() {
		StringBuilder url = new StringBuilder();
		url.append(GWT.getHostPageBaseURL());
		url.append("server/playlist/");
		url.append(id);
		url.append("/desc/0/32000");
		return URL.encode(url.toString());
	}

	@Override
	public void onSuccess(Response response) {
		model.setShowing(id, false);
		model.setState(State.SONGS);
		model.fireStateHandlers();
	}

	@Override
	public void onError() {
		onSuccess(null);
	}

}
