package com.nublic.app.music.client.datamodel.messages;

import com.google.gwt.http.client.Response;
import com.nublic.app.music.client.datamodel.DataModel;
import com.nublic.util.messages.Message;

//GET /collections
//* Retrieve all tags in the system
//* Return: [ collection1, collection2, ... ]
//  where:  collection ::= { "id"   : $id
//                         , "name" : $name
//                         }

public class TagMessage extends Message {
	DataModel model;
	
	public TagMessage(DataModel model) {
		this.model = model;
	}

	@Override
	public String getURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onSuccess(Response response) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onError() {
		// TODO Auto-generated method stub

	}
}
