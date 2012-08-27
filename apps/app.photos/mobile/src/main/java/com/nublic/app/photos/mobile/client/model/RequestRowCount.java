package com.nublic.app.photos.mobile.client.model;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import com.nublic.util.gwt.LocationUtil;
import com.nublic.util.messages.Message;
import com.nublic.util.messages.SequenceHelper;

class RequestRowCount extends Request {
	CallbackRowCount cb;
	
	public RequestRowCount(PhotosModel model, CallbackRowCount cb) {
		super(model);
		this.cb = cb;
	}

	@Override
	public void execute() {
		final AlbumInfo currentAlbum = model.getCurrentAlbum();
		if (currentAlbum.getRowCount() != -1) {
			cb.rowCount(currentAlbum, currentAlbum.getRowCount());
			model.removeFirstRequest();
		} else {
			// We have to get this from the server
			SequenceHelper.sendJustOne(new Message() {
				@Override
				public void onSuccess(Response response) {
					if (response.getStatusCode() == Response.SC_OK) {
						JsonRowCount c = JsonUtils.safeEval(response.getText());
						model.updatePhotos(c, 0);
						cb.rowCount(currentAlbum, currentAlbum.getRowCount());
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
							model.getCurrentAlbum().getOrder().getParameter() + "/0/20/" + rest);
				}
			}, RequestBuilder.GET);
		}
	}
}
