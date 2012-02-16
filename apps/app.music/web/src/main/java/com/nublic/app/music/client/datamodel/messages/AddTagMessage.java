package com.nublic.app.music.client.datamodel.messages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.nublic.app.music.client.datamodel.DataModel;
import com.nublic.app.music.client.datamodel.Tag;
import com.nublic.util.error.ErrorPopup;
import com.nublic.util.messages.Message;

//PUT /collections
//* Add a collection to the system
//* :name -> name that the user gave
//* Return: the new id of the collection
public class AddTagMessage extends Message {
	String name;
	DataModel model;
	
	public AddTagMessage(String name, DataModel model) {
		this.name = name;
		this.model = model;
	}
	
	@Override
	public String getURL() {
		addParam("name", name);
		return URL.encode(GWT.getHostPageBaseURL() + "server/collections" );
	}

	@Override
	public void onSuccess(Response response) {
		if (response.getStatusCode() == Response.SC_OK) {
			String text = response.getText();

			model.addTag(new Tag(text, name));
			model.fireTagsHandlers();
		} else {
			onError();
		}
	}

	@Override
	public void onError() {
		ErrorPopup.showError("Could not add collection");
	}

}
