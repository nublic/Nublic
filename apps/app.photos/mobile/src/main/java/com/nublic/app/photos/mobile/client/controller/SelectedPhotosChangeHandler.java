package com.nublic.app.photos.mobile.client.controller;

import java.util.Set;

import com.nublic.app.photos.mobile.client.model.PhotoInfo;


public interface SelectedPhotosChangeHandler {
	void selectedPhotosChanged(Set<PhotoInfo> selectedIds);
}
