package com.nublic.app.music.client.ui.artist;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.nublic.app.music.client.datamodel.Artist;

public class ArtistPanel extends Composite {
	private static ArtistPanelUiBinder uiBinder = GWT.create(ArtistPanelUiBinder.class);
	interface ArtistPanelUiBinder extends UiBinder<Widget, ArtistPanel> { }
	
	@UiField FlowPanel mainPanel;

	List<Artist> artistList;

	public ArtistPanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setArtistList(List<Artist> artistList) {
		this.artistList = artistList;

		for (Artist a : artistList) {
			ArtistWidget aw = new ArtistWidget(a);
			mainPanel.add(aw);
		}
	}

}
