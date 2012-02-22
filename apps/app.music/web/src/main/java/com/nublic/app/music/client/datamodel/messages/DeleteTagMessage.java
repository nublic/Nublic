package com.nublic.app.music.client.datamodel.messages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.nublic.app.music.client.datamodel.DataModel;
import com.nublic.util.error.ErrorPopup;
import com.nublic.util.messages.Message;

//DELETE /collections
//* Delete a collection from the system
//* :id -> id of the collection to remove
public class DeleteTagMessage extends Message {
	String id;
	DataModel model;
	
	public DeleteTagMessage(String id, DataModel model) {
		this.id = id;
		this.model = model;
	}
	
	@Override
	public String getURL() {
		addParam("id", id);
		return URL.encode(GWT.getHostPageBaseURL() + "server/collections" );
	}

	@Override
	public void onSuccess(Response response) {
		if (response.getStatusCode() == Response.SC_OK) {
			model.removeTag(id);
			model.fireTagsHandlers();
			// TODO: reload main ui
//			model.askForArtists();
		} else {
			onError();
		}
	}

	@Override
	public void onError() {
		ErrorPopup.showError("Could not delete collection");
	}

}
