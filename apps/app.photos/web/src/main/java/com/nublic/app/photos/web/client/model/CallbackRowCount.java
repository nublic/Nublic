package com.nublic.app.photos.web.client.model;

public interface CallbackRowCount {
	void rowCount(AlbumInfo info, long rowCount);
	void error();
}
