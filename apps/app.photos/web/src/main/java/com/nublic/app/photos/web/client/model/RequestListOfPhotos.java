package com.nublic.app.photos.web.client.model;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import com.nublic.util.gwt.LocationUtil;
import com.nublic.util.messages.Message;
import com.nublic.util.messages.SequenceHelper;

class RequestListOfPhotos extends Request {
	CallbackListOfPhotos cb;
	long start;
	long length;
	
	public RequestListOfPhotos(PhotosModel model, CallbackListOfPhotos cb, long start, long length) {
		super(model);
		this.cb = cb;
		this.start = start;
		this.length = length;
	}

	@Override
	public void execute() {
		final AlbumInfo currentAlbum = model.getCurrentAlbum();
		if (currentAlbum.has(start, length)) {
			cb.list(currentAlbum, start, length, currentAlbum.get(start, length));
			model.removeFirstRequest();
		} else {
			// We have to get this from the server
			SequenceHelper.sendJustOne(new Message() {
				@Override
				public void onSuccess(Response response) {
					if (response.getStatusCode() == Response.SC_OK) {
						JsonRowCount c = JsonUtils.safeEval(response.getText());
						model.updatePhotos(c, start);
						cb.list(currentAlbum, start, length, currentAlbum.get(start, length));
						model.removeFirstRequest();
					} else {
						cb.error();
						model.removeFirstRequest();
					}
				}
				
				@Override
				public void onError() {
					cb.error();
					model.removeFirstRequest();
				}
				
				@Override
				public String getURL() {
					String rest = currentAlbum.getId() == -1 ? "" : String.valueOf(currentAlbum.getId());
					return LocationUtil.encodeURL(GWT.getHostPageBaseURL() + "server/photos/" + 
							model.getCurrentAlbum().getOrder().getParameter() + "/" + 
							String.valueOf(start) + "/" + String.valueOf(length) + "/" + rest);
				}
			}, RequestBuilder.GET);
		}
	}
}
