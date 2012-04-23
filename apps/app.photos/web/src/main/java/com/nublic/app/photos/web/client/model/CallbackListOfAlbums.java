package com.nublic.app.photos.web.client.model;

import java.util.Map;

public interface CallbackListOfAlbums {
	void list(Map<Long, String> albums);
	void error();
}
