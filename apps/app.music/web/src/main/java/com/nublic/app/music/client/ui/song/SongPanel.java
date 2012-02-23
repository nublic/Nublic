package com.nublic.app.music.client.ui.song;

import java.util.EnumSet;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.music.client.Constants;
import com.nublic.app.music.client.datamodel.AlbumInfo;
import com.nublic.app.music.client.datamodel.DataModel;
import com.nublic.app.music.client.datamodel.SongInfo;
import com.nublic.app.music.client.datamodel.handlers.AddAtEndButtonHandler;
import com.nublic.app.music.client.datamodel.handlers.PlayButtonHandler;
import com.nublic.app.music.client.ui.ButtonLine;
import com.nublic.app.music.client.ui.ButtonLineParam;
import com.nublic.util.cache.CacheHandler;

public class SongPanel extends Composite {
	private static SongPanelUiBinder uiBinder = GWT.create(SongPanelUiBinder.class);
	interface SongPanelUiBinder extends UiBinder<Widget, SongPanel> { }

	@UiField FlowPanel mainPanel;
	@UiField Label titleLabel;
	@UiField Label byLabel;
	@UiField HorizontalPanel titlePanel;
	@UiField Hyperlink subtitleLabel;
	
	DataModel model;
	String inCollection;
	String albumId;

	public SongPanel(DataModel model, String albumId, String collectionId) {
		initWidget(uiBinder.createAndBindUi(this));

		this.model = model;
		this.albumId = albumId;
		this.inCollection = collectionId;

		// Get artist info (null means all albums)
		if (albumId == null) {
			titleLabel.setText("All albums");
			byLabel.setVisible(false);
			subtitleLabel.setVisible(false);
		} else {
			model.getAlbumCache().addHandler(albumId, new CacheHandler<String, AlbumInfo>() {
				@Override
				public void onCacheUpdated(String k, AlbumInfo v) {
					titleLabel.setText(v.getName());
					byLabel.setVisible(true);
					subtitleLabel.setVisible(true);
					subtitleLabel.setText("some artist");
					setSubtitleTarget();
				}
			});
			model.getAlbumCache().obtain(albumId);
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
	
	public void setSongList(int total, int from, int to, List<SongInfo> answerList, String albumId, String collectionId) {
		SongList sl = new SongList(model, albumId, null, collectionId, total, mainPanel);
		sl.addSongs(total, from, to, answerList);

		mainPanel.add(sl);
	}
	
	public void setSubtitleTarget() {
		StringBuilder target = new StringBuilder();		
		if (inCollection != null) {
			target.append(Constants.PARAM_COLLECTION);
			target.append("=");
			target.append(inCollection);
			target.append("&");
		}
		target.append(Constants.PARAM_ARTIST);
		target.append("=");
		target.append("SomeArtistId");
		subtitleLabel.setTargetHistoryToken(target.toString());
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
