package com.nublic.app.photos.web.client.dnd;

import java.util.Set;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.photos.web.client.PhotosApp;
import com.nublic.app.photos.web.client.model.PhotoInfo;

public class PhotoDragController extends PickupDragController implements HasProxy {

	String draggingAlbumId;
	String draggingArtistId;
	String draggingCollectionId;
	int numberOfSongs;
	PhotoProxy proxy = null;

	public PhotoDragController() {
		super(RootPanel.get(), false);
	    setBehaviorDragProxy(true);
	    setBehaviorMultipleSelection(false);
	    setBehaviorDragStartSensitivity(5);
	}

	@Override
	protected Widget newDragProxy(DragContext context) {
		Set<PhotoInfo> selectedPhotos = PhotosApp.getController().getSelectedPhotos();

		if (selectedPhotos.size() == 1) {
			for (PhotoInfo info : selectedPhotos) {
				proxy = new PhotoProxy(info);
			}
		} else {
			proxy = new PhotoProxy(selectedPhotos.size());
		}
		
		return proxy;
	}

	@Override
	public DragProxy getProxy() {
		return proxy;
	}
}
