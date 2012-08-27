package com.nublic.app.photos.mobile.client.model;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import com.nublic.util.gwt.LocationUtil;
import com.nublic.util.messages.Message;
import com.nublic.util.messages.SequenceHelper;

class RequestPhotosAround extends Request {
	CallbackThreePhotos cb;
	long position;
	
	public RequestPhotosAround(PhotosModel model, CallbackThreePhotos cb, long position) {
		super(model);
		this.cb = cb;
		this.position = position;
	}

	@Override
	public void execute() {
		final AlbumInfo currentAlbum = model.getCurrentAlbum();
		
		final long initialPosition = position - (position == 0 ? 0 : 1);
		final long numberToFetch = 1 + (position == 0 ? 0 : 1) + (position >= currentAlbum.getRowCount() - 1 ? 0 : 1);
		
		if (currentAlbum.has(initialPosition, numberToFetch)) {
			cb.list(currentAlbum,
					position == 0 ? null : currentAlbum.get(position - 1),
					currentAlbum.get(position),
					position >= currentAlbum.getRowCount() - 1 ? null : currentAlbum.get(position + 1));
			model.removeFirstRequest();
		} else {
			// We have to get this from the server
			SequenceHelper.sendJustOne(new Message() {
				@Override
				public void onSuccess(Response response) {
					if (response.getStatusCode() == Response.SC_OK) {
						JsonRowCount c = JsonUtils.safeEval(response.getText());
						model.updatePhotos(c, initialPosition);
						cb.list(currentAlbum,
								position == 0 ? null : currentAlbum.get(position - 1),
								currentAlbum.get(position),
								position >= currentAlbum.getRowCount() - 1 ? null : currentAlbum.get(position + 1));
					} else {
						cb.error();
					}
					model.removeFirstRequest();
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
							String.valueOf(initialPosition) + "/" + String.valueOf(numberToFetch) + "/" + rest);
				}
			}, RequestBuilder.GET);
		}
	}
}
