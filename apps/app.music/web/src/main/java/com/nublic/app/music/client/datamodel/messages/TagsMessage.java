package com.nublic.app.music.client.datamodel.messages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.nublic.app.music.client.datamodel.DataModel;
import com.nublic.app.music.client.datamodel.Tag;
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
		JsArray <JSTag> jsTagList = null;
		
		if (response.getStatusCode() == Response.SC_OK) {
			String text = response.getText();
			jsTagList = JsonUtils.safeEval(text);
			
			if (jsTagList == null) {
				onError();
			} else {
				for (int i = 0 ; i < jsTagList.length() ; i++) {
					JSTag jsTag = jsTagList.get(i);
					model.addTag(new Tag(jsTag.getId(), jsTag.getName()));
				}
			}
		} else {
			onError();
		}
		
		model.fireTagsHandlers();
	}

	@Override
	public void onError() {
		ErrorPopup.showError("Could not get collection list");
	}
}
