package com.nublic.app.photos.common.model;

import java.util.Set;

public interface CallbackPhotosRemoval {
	void list(AlbumInfo album, Set<Long> deletedPhotos);
	void error();
}
