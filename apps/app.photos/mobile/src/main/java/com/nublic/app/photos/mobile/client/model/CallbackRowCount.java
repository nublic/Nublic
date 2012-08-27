package com.nublic.app.photos.mobile.client.model;

public interface CallbackRowCount {
	void rowCount(AlbumInfo info, long rowCount);
	void error();
}
