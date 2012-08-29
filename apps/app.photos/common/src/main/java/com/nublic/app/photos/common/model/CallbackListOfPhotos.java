package com.nublic.app.photos.common.model;

import java.util.List;

public interface CallbackListOfPhotos {
	void list(AlbumInfo info, long start, long length, List<PhotoInfo> photos);
	void error();
}
