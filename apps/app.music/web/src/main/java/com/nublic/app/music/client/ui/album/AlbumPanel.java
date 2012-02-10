package com.nublic.app.music.client.ui.album;

import java.util.EnumSet;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.music.client.datamodel.Album;
import com.nublic.app.music.client.datamodel.ArtistInfo;
import com.nublic.app.music.client.datamodel.DataModel;
import com.nublic.app.music.client.datamodel.handlers.AddAtEndButtonHandler;
import com.nublic.app.music.client.datamodel.handlers.PlayButtonHandler;
import com.nublic.app.music.client.ui.ButtonLine;
import com.nublic.app.music.client.ui.ButtonLineParam;
import com.nublic.util.cache.Cache;
import com.nublic.util.cache.CacheHandler;

public class AlbumPanel extends Composite {
	private static AlbumPanelUiBinder uiBinder = GWT.create(AlbumPanelUiBinder.class);
	interface AlbumPanelUiBinder extends UiBinder<Widget, AlbumPanel> { }

	@UiField FlowPanel mainPanel;
	@UiField Label titleLabel;
	@UiField HorizontalPanel titlePanel;
	
	List<Album> albumList;
	DataModel model;
	ArtistInfo info;

	public AlbumPanel(DataModel model) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.model = model;
		// Get artist info (null means all artists)
		String artistId = model.getShowingArtistId();
		if (artistId == null) {
			this.info = null;
			titleLabel.setText("All artists");
		} else {
			Cache<String, ArtistInfo> artistCache = model.getArtistCache();
			artistCache.addHandler(artistId, new CacheHandler<String, ArtistInfo>() {
				@Override
				public void onCacheUpdated(String k, ArtistInfo v) {
					info = v;
					titleLabel.setText(info.getName());
				}
			});
			artistCache.obtain(artistId);
		}

		// Create button line
		EnumSet<ButtonLineParam> buttonSet = EnumSet.of(ButtonLineParam.ADD_AT_END,
														ButtonLineParam.PLAY);
//		buttonSet.add(ButtonLineParam.DELETE);
		ButtonLine b = new ButtonLine(buttonSet);
//		setDeleteButtonHandler(b);
		setAddAtEndButtonHandler(b);
		setPlayButtonHandler(b);
		titlePanel.add(b);
	}

	public void setAlbumList(List<Album> albumList) {
		this.albumList = albumList;

		for (Album a : albumList) {
			AlbumWidget aw = new AlbumWidget(model, a, mainPanel);
			mainPanel.add(aw);
		}
	}
	
	// Handlers for button line
	private void setAddAtEndButtonHandler(ButtonLine b) {
		b.setAddAtEndButtonHandler(new AddAtEndButtonHandler() {
			@Override
			public void onAddAtEnd() {
				// TODO: addAtEnd
			}
		});
	}

	private void setPlayButtonHandler(ButtonLine b) {
		b.setPlayButtonHandler(new PlayButtonHandler() {
			@Override
			public void onPlay() {
				// TODO: play
			}
		});
	}

}
