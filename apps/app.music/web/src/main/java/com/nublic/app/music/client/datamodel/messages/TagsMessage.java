package com.nublic.app.music.client.datamodel.messages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.nublic.app.music.client.datamodel.DataModel;
import com.nublic.app.music.client.datamodel.Tag;
import com.nublic.util.messages.Message;

//GET /collections
//* Retrieve all tags in the system
//* Return: [ collection1, collection2, ... ]
//  where:  collection ::= { "id"   : $id
//                         , "name" : $name
//                         }

public class TagsMessage extends Message {
	DataModel model;
	
	public TagsMessage(DataModel model) {
		this.model = model;
	}

	@Override
	public String getURL() {
		return URL.encode(GWT.getHostPageBaseURL() + "server/collections" );
	}

	@Override
	public void onSuccess(Response response) {
		// Fake thing to test without server
		model.addTag(new Tag("1", "Pablo"));
		model.addTag(new Tag("2", "Jazz"));
		model.addTag(new Tag("3", "Pop"));
		model.addTag(new Tag("4", "Rock&Roll"));
		model.fireTagsHandlers();
	}

	@Override
	public void onError() {
		// TODO: do something real
		onSuccess(null);
	}
}
