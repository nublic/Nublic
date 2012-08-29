package com.nublic.app.photos.common.model;

import java.util.Map;

public interface CallbackListOfAlbums {
	void list(Map<Long, String> albums);
	void error();
}
