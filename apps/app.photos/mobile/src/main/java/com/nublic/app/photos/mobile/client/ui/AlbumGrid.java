package com.nublic.app.photos.mobile.client.ui;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.gwtmobile.ui.client.page.Page;
import com.nublic.app.photos.common.model.AlbumInfo;
import com.nublic.app.photos.common.model.AlbumOrder;
import com.nublic.app.photos.common.model.CallbackListOfPhotos;
import com.nublic.app.photos.common.model.PhotoInfo;
import com.nublic.app.photos.common.model.PhotosModel;

public class AlbumGrid extends Page {
	private static AlbumGridUiBinder uiBinder = GWT.create(AlbumGridUiBinder.class);
	interface AlbumGridUiBinder extends UiBinder<Widget, AlbumGrid> { }

	@UiField FlowPanel grid;
	@UiField Label titleLabel;
	
	public AlbumGrid(Long albumId, String title) {
		initWidget(uiBinder.createAndBindUi(this));
		
		createInterface(albumId, title);
	}

	private void createInterface(Long albumId, String title) {
		titleLabel.setText(title);
		PhotosModel.get().startNewAlbum(albumId, AlbumOrder.DATE_ASC);
		PhotosModel.get().photoList(0, 35000, new CallbackListOfPhotos() {
			@Override
			public void list(AlbumInfo info, long start, long length, List<PhotoInfo> photos) {
				int index = 0;
				for (PhotoInfo pi : photos) {
					addPhotoToGrid(pi, index++);
				}
			}

			@Override
			public void error() {
				// nothing
			}
		});
	}
	
	private void addPhotoToGrid(PhotoInfo pi, int photoIndex) {
		grid.add(new PhotoThumbnail(pi, this, photoIndex));
	}

}
