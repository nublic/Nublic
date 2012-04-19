package com.nublic.app.music.client.datamodel.messages;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.nublic.app.music.client.controller.Controller;
import com.nublic.app.music.client.datamodel.Tag;
import com.nublic.app.music.client.datamodel.handlers.TagsChangeHandler.TagsChangeEvent;
import com.nublic.app.music.client.datamodel.handlers.TagsChangeHandler.TagsChangeEventType;
import com.nublic.app.music.client.datamodel.js.JSTag;
import com.nublic.util.error.ErrorPopup;
import com.nublic.util.messages.Message;

//GET /collections
//* Retrieve all tags in the system
//* Return: [ collection1, collection2, ... ]
//  where:  collection ::= { "id"   : $id
//                         , "name" : $name
//                         }
public class TagsMessage extends Message {	
	public TagsMessage() {
	}

	@Override
	public String getURL() {
		return URL.encode(GWT.getHostPageBaseURL() + "server/collections" );
	}

	@Override
	public void onSuccess(Response response) {
		JsArray <JSTag> jsTagList = null;
		
		if (response.getStatusCode() == Response.SC_OK) {
			String text = response.getText();
			jsTagList = JsonUtils.safeEval(text);
			
			if (jsTagList == null) {
				onError();
			} else {
				List<Tag> receivedTags = new ArrayList<Tag>();
				for (int i = 0 ; i < jsTagList.length() ; i++) {
					JSTag jsTag = jsTagList.get(i);
					Tag t = new Tag(jsTag.getId(), jsTag.getName());
					receivedTags.add(t);
					Controller.INSTANCE.getModel().getTagCache().put(jsTag.getId(), t);
				}				
				Controller.INSTANCE.getModel().fireTagsHandlers(new TagsChangeEvent(TagsChangeEventType.TAGS_ADDED, receivedTags));
			}
		} else {
			onError();
		}
		
	}

	@Override
	public void onError() {
		ErrorPopup.showError("Could not get collection list");
	}
}
