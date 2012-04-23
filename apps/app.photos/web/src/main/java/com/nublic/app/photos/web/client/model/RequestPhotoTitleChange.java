package com.nublic.app.photos.web.client.model;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import com.nublic.util.gwt.LocationUtil;
import com.nublic.util.messages.Message;
import com.nublic.util.messages.SequenceHelper;

class RequestPhotoTitleChange extends Request {
	long id;
	String title;
	
	public RequestPhotoTitleChange(PhotosModel model, long id, String title) {
		super(model);
		this.id = id;
		this.title = title;
	}

	@Override
	public void execute() {
		Message m = new Message() {
			@Override
			public String getURL() {
				return LocationUtil.encodeURL(GWT.getHostPageBaseURL() + "server/photo-title/" + id);
			}
			@Override
			public void onSuccess(Response response) {
				if (response.getStatusCode() == Response.SC_OK) {
					model.updatePhotoTitle(id, title);
				}
				model.removeFirstRequest();
			}
			@Override
			public void onError() {
				model.removeFirstRequest();
			}
		};
		m.addParam("title", title);
		SequenceHelper.sendJustOne(m, RequestBuilder.POST);
	}
}
