package com.nublic.app.music.client.datamodel.messages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.nublic.app.music.client.datamodel.DataModel;
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
		model.removeTag(id);
		model.fireTagsHandlers();
		model.askForArtists();
	}

	@Override
	public void onError() {
		// TODO doError
		onSuccess(null);
	}

}
