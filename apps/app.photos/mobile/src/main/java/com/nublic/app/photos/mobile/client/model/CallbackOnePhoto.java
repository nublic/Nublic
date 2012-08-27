package com.nublic.app.photos.mobile.client.model;

public interface CallbackOnePhoto {
	void list(AlbumInfo info, PhotoInfo photo);
	void error();
}
