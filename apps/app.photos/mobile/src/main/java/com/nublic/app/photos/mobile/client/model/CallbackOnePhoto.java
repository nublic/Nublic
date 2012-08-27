package com.nublic.app.photos.web.client.model;

public interface CallbackOnePhoto {
	void list(AlbumInfo info, PhotoInfo photo);
	void error();
}
