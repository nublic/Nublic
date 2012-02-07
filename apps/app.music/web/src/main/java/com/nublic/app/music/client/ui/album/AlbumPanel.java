package com.nublic.app.music.client.ui.album;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.music.client.datamodel.Album;

public class AlbumPanel extends Composite {
	private static AlbumPanelUiBinder uiBinder = GWT.create(AlbumPanelUiBinder.class);
	interface AlbumPanelUiBinder extends UiBinder<Widget, AlbumPanel> { }

	@UiField FlowPanel mainPanel;
	
	List<Album> albumList;

	public AlbumPanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setAlbumList(List<Album> albumList) {
		this.albumList = albumList;

		for (Album a : albumList) {
			AlbumWidget aw = new AlbumWidget(a);
			mainPanel.add(aw);
		}
	}

}
