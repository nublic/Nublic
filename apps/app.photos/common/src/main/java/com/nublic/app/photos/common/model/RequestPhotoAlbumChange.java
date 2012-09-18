package com.nublic.app.photos.common.model;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Response;
import com.nublic.util.gwt.LocationUtil;
import com.nublic.util.messages.Message;
import com.nublic.util.messages.SequenceHelper;

class RequestPhotoAlbumChange extends Request {
	Long photoId;
	Long albumId;
	AlbumChangeType type;
	
	public RequestPhotoAlbumChange(PhotosModel model, Long photoId, Long albumId, AlbumChangeType type) {
		super(model);
		this.photoId = photoId;
		this.albumId = albumId;
		this.type = type;
	}

	@Override
	public void execute() {
		Message m = new Message() {
			@Override
			public String getURL() {
				return LocationUtil.encodeURL(GWT.getHostPageBaseURL() + "server/album/" + albumId);
			}
			@Override
			public void onSuccess(Response response) {
				model.removeFirstRequest();
			}
			@Override
			public void onError() {
				model.removeFirstRequest();
			}
		};
		m.addParam("photos", String.valueOf(photoId));
		SequenceHelper.sendJustOne(m, type.getMethod());
	}
}
