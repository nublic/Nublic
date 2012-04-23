package com.nublic.app.photos.web.client.model;

import java.util.Set;

public interface CallbackPhotosRemoval {
	void list(AlbumInfo album, Set<Long> deletedPhotos);
	void error();
}
