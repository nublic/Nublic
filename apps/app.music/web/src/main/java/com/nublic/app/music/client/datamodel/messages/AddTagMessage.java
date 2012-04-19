package com.nublic.app.music.client.datamodel.messages;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.nublic.app.music.client.controller.Controller;
import com.nublic.app.music.client.datamodel.Tag;
import com.nublic.app.music.client.datamodel.handlers.TagsChangeHandler.TagsChangeEvent;
import com.nublic.app.music.client.datamodel.handlers.TagsChangeHandler.TagsChangeEventType;
import com.nublic.util.error.ErrorPopup;
import com.nublic.util.messages.Message;

//PUT /collections
//* Add a collection to the system
//* :name -> name that the user gave
//* Return: the new id of the collection
public class AddTagMessage extends Message {
	String name;
	
	public AddTagMessage(String name) {
		this.name = name;
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

			List<Tag> involvedSet = new ArrayList<Tag>();
			Tag t = new Tag(text, name);
			involvedSet.add(new Tag(text, name));
			TagsChangeEvent event = new TagsChangeEvent(TagsChangeEventType.TAGS_ADDED, involvedSet);
			Controller.INSTANCE.getModel().fireTagsHandlers(event);
			Controller.INSTANCE.getModel().getTagCache().put(text, t);
		} else {
			onError();
		}
	}

	@Override
	public void onError() {
		ErrorPopup.showError("Could not add collection");
	}

}
