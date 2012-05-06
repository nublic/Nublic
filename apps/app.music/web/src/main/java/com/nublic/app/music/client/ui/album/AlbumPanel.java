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
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.music.client.Constants;
import com.nublic.app.music.client.Utils;
import com.nublic.app.music.client.controller.Controller;
import com.nublic.app.music.client.controller.ViewKind;
import com.nublic.app.music.client.datamodel.AlbumInfo;
import com.nublic.app.music.client.datamodel.ArtistInfo;
import com.nublic.app.music.client.datamodel.handlers.AddAtEndButtonHandler;
import com.nublic.app.music.client.datamodel.handlers.DeleteButtonHandler;
import com.nublic.app.music.client.datamodel.handlers.PlayButtonHandler;
import com.nublic.app.music.client.ui.ButtonLine;
import com.nublic.app.music.client.ui.ButtonLineParam;
import com.nublic.app.music.client.ui.EmptyWidget;
import com.nublic.app.music.client.ui.TagKind;
import com.nublic.app.music.client.ui.ViewTabs;
import com.nublic.util.cache.Cache;
import com.nublic.util.cache.CacheHandler;

public class AlbumPanel extends Composite {
	private static AlbumPanelUiBinder uiBinder = GWT.create(AlbumPanelUiBinder.class);
	interface AlbumPanelUiBinder extends UiBinder<Widget, AlbumPanel> { }

	@UiField FlowPanel mainPanel;
	@UiField Label titleLabel;
	@UiField HorizontalPanel titlePanel;
	@UiField ViewTabs viewTabs;
	@UiField PushButton backButton;
	
	List<AlbumInfo> albumList;
	String artistId;
	String collectionId;

	public AlbumPanel(String artistId, String collectionId) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.collectionId = collectionId;
		this.artistId = artistId;
	
		// Get artist info (null means all artists)
		if (artistId == null) {
			if (collectionId != null) {
				titleLabel.setText(Controller.INSTANCE.getModel().getTagCache().get(collectionId).getName());
			} else {
				titleLabel.setText(Constants.I18N.allMusic());
			}
			setViewLinks(true);
			backButton.setVisible(false);
		} else {
			Cache<String, ArtistInfo> artistCache = Controller.INSTANCE.getModel().getArtistCache();
			artistCache.addHandler(artistId, new CacheHandler<String, ArtistInfo>() {
				@Override
				public void onCacheUpdated(String k, ArtistInfo v) {
					titleLabel.setText(v.getName());
				}
			});
			artistCache.obtain(artistId);
			setViewLinks(false);
			Utils.setBackButton(backButton, collectionId);
		}
		
		// Create button line
		createButtonLine();
	}
	
	private void createButtonLine() {
		EnumSet<ButtonLineParam> buttonSet = EnumSet.of(ButtonLineParam.ADD_AT_END, ButtonLineParam.PLAY);
		if (collectionId != null && artistId == null) { // We're in an album view of a collection
			buttonSet.add(ButtonLineParam.DELETE);
		}
		ButtonLine b = new ButtonLine(buttonSet);
		setDeleteButtonHandler(b);
		setAddAtEndButtonHandler(b);
		setPlayButtonHandler(b);
		titlePanel.insert(b, 2);
	}

	public void setAlbumList(List<AlbumInfo> albumList) {
		this.albumList = albumList;

		if (albumList.isEmpty()) {
			mainPanel.add(new EmptyWidget());
		} else {
			for (AlbumInfo a : albumList) {
				AlbumWidget aw = new AlbumWidget(a, artistId, collectionId, mainPanel);
				mainPanel.add(aw);
			}
		}
	}

	private void setViewLinks(boolean shouldShowArtist) {
		if (shouldShowArtist) {
			String artistTarget = Utils.getTargetHistoryToken(null, null, collectionId, ViewKind.ARTISTS.toString());
			viewTabs.setTarget(ViewKind.ARTISTS, artistTarget);
		} else {
			viewTabs.hideTab(ViewKind.ARTISTS);
		}
		String songTarget = Utils.getTargetHistoryToken(artistId, null, collectionId, ViewKind.SONGS.toString());
		viewTabs.setTarget(ViewKind.SONGS, songTarget);
		
		viewTabs.setSelected(ViewKind.ALBUMS);
	}
	
	// Handlers for button line
	private void setDeleteButtonHandler(ButtonLine b) {
		b.setDeleteButtonHandler(new DeleteButtonHandler() {
			@Override
			public void onDelete() {
				Controller.INSTANCE.deleteTag(collectionId, TagKind.COLLECTION);
			}
		});
	}
	
	private void setAddAtEndButtonHandler(ButtonLine b) {
		b.setAddAtEndButtonHandler(new AddAtEndButtonHandler() {
			@Override
			public void onAddAtEnd() {
				Controller.INSTANCE.addAtEnd(artistId, null, collectionId);
			}
		});
	}

	private void setPlayButtonHandler(ButtonLine b) {
		b.setPlayButtonHandler(new PlayButtonHandler() {
			@Override
			public void onPlay() {
				Controller.INSTANCE.play(artistId, null, collectionId);
			}
		});
	}

}
