package com.nublic.app.photos.mobile.client.model;

public interface CallbackThreePhotos {
	void list(AlbumInfo info, PhotoInfo prev, PhotoInfo current, PhotoInfo next);
	void error();
}
