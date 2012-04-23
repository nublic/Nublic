package com.nublic.app.photos.web.client.model;

import java.util.List;

public interface CallbackListOfPhotos {
	void list(AlbumInfo info, long start, long length, List<PhotoInfo> photos);
	void error();
}
