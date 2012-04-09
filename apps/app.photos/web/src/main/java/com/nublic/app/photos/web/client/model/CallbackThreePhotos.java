package com.nublic.app.photos.web.client.model;

public interface CallbackThreePhotos {
	void list(AlbumInfo info, PhotoInfo prev, PhotoInfo current, PhotoInfo next);
	void error();
}
