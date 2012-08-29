package com.nublic.app.photos.common.model;

public interface CallbackRowCount {
	void rowCount(AlbumInfo info, long rowCount);
	void error();
}
