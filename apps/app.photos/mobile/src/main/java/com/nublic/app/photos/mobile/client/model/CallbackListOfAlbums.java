package com.nublic.app.photos.mobile.client.model;

import java.util.Map;

public interface CallbackListOfAlbums {
	void list(Map<Long, String> albums);
	void error();
}
