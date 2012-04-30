package com.nublic.app.music.client.datamodel.messages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.nublic.app.music.client.Constants;
import com.nublic.app.music.client.controller.Controller;
import com.nublic.app.music.client.datamodel.handlers.MoveSongHandler;
import com.nublic.util.error.ErrorPopup;
import com.nublic.util.messages.Message;

//POST /playlist/order/:id
//* Change the order of a list
//* :from -> the place where the element is now
//* :to -> the place where the element will be later
public class MovePlaylistSongMessage extends Message {
	String id;
	int from;
	int to;
	MoveSongHandler msh;
	
	public MovePlaylistSongMessage(String id, int from, int to, MoveSongHandler msh) {
		this.id = id;
		this.from = from;
		this.to = to;
		this.msh = msh;
	}
	
	@Override
	public String getURL() {
		addParam("from", String.valueOf(from));
		addParam("to", String.valueOf(to));
		return URL.encode(GWT.getHostPageBaseURL() + "server/playlist/order/" + id);
	}

	@Override
	public void onSuccess(Response response) {
		if (response.getStatusCode() == Response.SC_OK) {
			Controller.INSTANCE.getModel().updateMoveInCache(id, from, to);
			msh.onSongMoved(id, from, to);
		} else {
			onError();
		}
	}

	@Override
	public void onError() {
		ErrorPopup.showError(Constants.I18N.moveSongError());
	}

}
