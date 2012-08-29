package com.nublic.app.photos.web.client.controller;

import java.util.Set;

import com.nublic.app.photos.common.model.PhotoInfo;


public interface SelectedPhotosChangeHandler {
	void selectedPhotosChanged(Set<PhotoInfo> selectedIds);
}
