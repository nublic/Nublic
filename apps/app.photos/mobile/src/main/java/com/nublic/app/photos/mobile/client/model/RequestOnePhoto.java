package com.nublic.app.photos.mobile.client.model;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import com.nublic.util.gwt.LocationUtil;
import com.nublic.util.messages.Message;
import com.nublic.util.messages.SequenceHelper;

class RequestOnePhoto extends Request {
	CallbackOnePhoto cb;
	long position;
	
	public RequestOnePhoto(PhotosModel model, CallbackOnePhoto cb, long position) {
		super(model);
		this.cb = cb;
		this.position = position;
	}

	@Override
	public void execute() {
		final AlbumInfo currentAlbum = model.getCurrentAlbum();
		if (currentAlbum.has(position, 1)) {
			cb.list(currentAlbum, currentAlbum.get(position));
			model.removeFirstRequest();
		} else {
			// We have to get this from the server
			SequenceHelper.sendJustOne(new Message() {
				@Override
				public void onSuccess(Response response) {
					if (response.getStatusCode() == Response.SC_OK) {
						JsonRowCount c = JsonUtils.safeEval(response.getText());
						model.updatePhotos(c, position);
						cb.list(currentAlbum, currentAlbum.get(position));
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
							String.valueOf(position) + "/30/" + rest);
				}
			}, RequestBuilder.GET);
		}
	}
}
