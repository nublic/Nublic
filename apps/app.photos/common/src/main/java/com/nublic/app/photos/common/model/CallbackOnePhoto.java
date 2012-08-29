package com.nublic.app.photos.common.model;

public interface CallbackOnePhoto {
	void list(AlbumInfo info, PhotoInfo photo);
	void error();
}
