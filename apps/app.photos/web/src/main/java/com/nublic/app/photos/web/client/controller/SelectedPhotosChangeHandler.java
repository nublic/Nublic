package com.nublic.app.photos.web.client.controller;

import java.util.Set;

public interface SelectedPhotosChangeHandler {
	void selectedPhotosChanged(Set<Long> selectedIds);
}
