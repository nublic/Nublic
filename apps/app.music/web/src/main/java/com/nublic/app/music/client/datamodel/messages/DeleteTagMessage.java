package com.nublic.app.music.client.datamodel.messages;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.History;
import com.nublic.app.music.client.datamodel.Controller;
import com.nublic.app.music.client.datamodel.Tag;
import com.nublic.app.music.client.datamodel.handlers.TagsChangeHandler.TagsChangeEvent;
import com.nublic.app.music.client.datamodel.handlers.TagsChangeHandler.TagsChangeEventType;
import com.nublic.util.error.ErrorPopup;
import com.nublic.util.messages.Message;

//DELETE /collections
//* Delete a collection from the system
//* :id -> id of the collection to remove
public class DeleteTagMessage extends Message {
	String id;
	
	public DeleteTagMessage(String id) {
		this.id = id;
	}
	
	@Override
	public String getURL() {
		addParam("id", id);
		return URL.encode(GWT.getHostPageBaseURL() + "server/collections" );
	}

	@Override
	public void onSuccess(Response response) {
		if (response.getStatusCode() == Response.SC_OK) {
//			model.removeTag(id);
			List<Tag> involvedSet = new ArrayList<Tag>();
			involvedSet.add(new Tag(id, id));
			TagsChangeEvent event = new TagsChangeEvent(TagsChangeEventType.TAGS_REMOVED, involvedSet);
			Controller.getModel().fireTagsHandlers(event);
			Controller.getModel().getTagCache().remove(id);
			// reload main ui
			History.newItem("");
		} else {
			onError();
		}
	}

	@Override
	public void onError() {
		ErrorPopup.showError("Could not delete collection");
	}

}
