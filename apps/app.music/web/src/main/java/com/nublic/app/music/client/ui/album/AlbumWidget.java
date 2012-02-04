package com.nublic.app.music.client.ui.album;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.music.client.datamodel.Album;

public class AlbumWidget extends Composite {
	private static AlbumWidgetUiBinder uiBinder = GWT.create(AlbumWidgetUiBinder.class);
	interface AlbumWidgetUiBinder extends UiBinder<Widget, AlbumWidget> { }

	Album album;
	
	public AlbumWidget(Album a) {
		initWidget(uiBinder.createAndBindUi(this));
		
		album = a;
	}

}
