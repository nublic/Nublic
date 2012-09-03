/*
 * Copyright (c) 2010 Zhihua (Dennis) Jiang
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.nublic.app.photos.mobile.client.ui;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtmobile.ui.client.page.Page;
import com.nublic.app.photos.common.model.AlbumInfo;
import com.nublic.app.photos.common.model.CallbackListOfPhotos;
import com.nublic.app.photos.common.model.PhotoInfo;
import com.nublic.app.photos.common.model.PhotosModel;

public class AlbumGrid extends Page {
	private static AlbumGridUiBinder uiBinder = GWT.create(AlbumGridUiBinder.class);
	interface AlbumGridUiBinder extends UiBinder<Widget, AlbumGrid> { }

	@UiField FlowPanel grid;
	
	public AlbumGrid(Long albumId) {
		initWidget(uiBinder.createAndBindUi(this));
		
		createInterface(albumId);
	}

	private void createInterface(Long albumId) {
		// TODO: Not using album!!
		PhotosModel.get().photoList(0, 35000, new CallbackListOfPhotos() {
			@Override
			public void list(AlbumInfo info, long start, long length, List<PhotoInfo> photos) {
				for (PhotoInfo pi : photos) {
					addPhotoToGrid(pi);
				}
			}

			@Override
			public void error() {
				// nothing
			}
		});
	}
	
	private void addPhotoToGrid(PhotoInfo pi) {
		grid.add(new PhotoThumbnail(pi, this));
	}


}
