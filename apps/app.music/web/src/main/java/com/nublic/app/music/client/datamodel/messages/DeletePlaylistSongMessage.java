package com.nublic.app.music.client.datamodel.messages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.nublic.app.music.client.controller.Controller;
import com.nublic.app.music.client.datamodel.handlers.DeleteButtonHandler;
import com.nublic.util.error.ErrorPopup;
import com.nublic.util.messages.Message;

//DELETE /playlist/:id
//* Remove songs from a playlist
//* :position -> position to remove
public class DeletePlaylistSongMessage extends Message {
	String id;
	int position;
	DeleteButtonHandler dbh;
	
	public DeletePlaylistSongMessage(String id, int position, DeleteButtonHandler dbh) {
		this.id = id;
		this.position = position;
		this.dbh = dbh;
	}
	
	@Override
	public String getURL() {
		addParam("position", String.valueOf(position));
		return URL.encode(GWT.getHostPageBaseURL() + "server/playlist/" + id);
	}

	@Override
	public void onSuccess(Response response) {
		if (response.getStatusCode() == Response.SC_OK) {
			Controller.INSTANCE.getModel().setDeleting(false);
			dbh.onDelete();
		} else {
			onError();
		}
	}

	@Override
	public void onError() {
		ErrorPopup.showError("Could not delete playlist song");
	}

}
