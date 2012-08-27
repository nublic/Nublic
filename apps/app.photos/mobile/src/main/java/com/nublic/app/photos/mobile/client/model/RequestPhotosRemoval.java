package com.nublic.app.photos.mobile.client.model;

import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import com.nublic.util.gwt.LocationUtil;
import com.nublic.util.gwt.NublicLists;
import com.nublic.util.messages.Message;
import com.nublic.util.messages.SequenceHelper;

class RequestPhotosRemoval extends Request {
	CallbackPhotosRemoval cb;
	Set<Long> photoIds;
	
	public RequestPhotosRemoval(PhotosModel model, CallbackPhotosRemoval cb, Set<Long> photoIds) {
		super(model);
		this.cb = cb;
		this.photoIds = photoIds;
	}

	@Override
	public void execute() {
		final AlbumInfo album = model.getCurrentAlbum();
		final String photoList = NublicLists.joinList(photoIds, ",");
		Message m = new Message() {
			@Override
			public String getURL() {
				return LocationUtil.encodeURL(GWT.getHostPageBaseURL() + "server/album/" + album.getId());
			}
			@Override
			public void onSuccess(Response response) {
				if (response.getStatusCode() == Response.SC_OK) {
					cb.list(album, photoIds);
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
		};
		m.addParam("photos", photoList);
		SequenceHelper.sendJustOne(m, RequestBuilder.DELETE);
	}
}
