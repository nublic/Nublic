package com.nublic.app.music.client.datamodel.messages;

import java.util.Collection;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.nublic.app.music.client.Constants;
import com.nublic.app.music.client.datamodel.handlers.DeleteButtonHandler;
import com.nublic.util.error.ErrorPopup;
import com.nublic.util.gwt.NublicLists;
import com.nublic.util.messages.Message;

//DELETE /collection/:id
//* Remove songs from a collection
//* :songs -> comma-separated list of ids of songs to remove
public class DeleteTagSongMessage extends Message {
	String id;
	Collection<String> songIDs;
	DeleteButtonHandler dbh;

	public DeleteTagSongMessage(String id, String songId, DeleteButtonHandler dbh) {
		this(id, Lists.newArrayList(songId), dbh);
	}

	public DeleteTagSongMessage(String id, Collection<String> songIDs, DeleteButtonHandler dbh) {
		this.id = id;
		this.songIDs = songIDs;
		this.dbh = dbh;
	}

	@Override
	public String getURL() {
		addParam("songs", NublicLists.joinList(songIDs, ","));
		return URL.encode(GWT.getHostPageBaseURL() + "server/collection/" + id);
	}

	@Override
	public void onSuccess(Response response) {
		if (response.getStatusCode() == Response.SC_OK) {
			dbh.onDelete();
		} else {
			onError();
		}
	}

	@Override
	public void onError() {
		ErrorPopup.showError(Constants.I18N.deleteCollectionSongError());
	}

}
