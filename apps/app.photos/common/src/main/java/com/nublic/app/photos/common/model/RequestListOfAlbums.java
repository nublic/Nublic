package com.nublic.app.photos.common.model;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import com.nublic.util.gwt.LocationUtil;
import com.nublic.util.messages.Message;
import com.nublic.util.messages.SequenceHelper;

public class RequestListOfAlbums extends Request {
	CallbackListOfAlbums cb;
	long photoId;

	public RequestListOfAlbums(PhotosModel model, long photoId, CallbackListOfAlbums cb) {
		super(model);
		this.cb = cb;
		this.photoId = photoId;
	}

	@Override
	public void execute() {
		SequenceHelper.sendJustOne(new Message() {
			@Override
			public String getURL() {
				return LocationUtil.encodeURL(GWT.getHostPageBaseURL() + "server/albums/" + photoId);
			}

			@Override
			public void onSuccess(Response response) {
				if (response.getStatusCode() == Response.SC_OK) {
					JsArray<JsonAlbum> albums = JsonUtils.safeEval(response.getText());
					Map<Long, String> albumCache = new HashMap<Long, String>();
					for (int i = 0; i < albums.length(); i++) {
						JsonAlbum json_album = albums.get(i);
						albumCache.put((long)json_album.getId(), json_album.getName());
					}
					cb.list(albumCache);
					model.removeFirstRequest();
				} else {
					onError();
				}
			}

			@Override
			public void onError() {
				// Nothing
				model.removeFirstRequest();
			}
		}, RequestBuilder.GET);
	}
}