package com.nublic.app.music.client.ui.song;

import java.util.EnumSet;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.music.client.Constants;
import com.nublic.app.music.client.Utils;
import com.nublic.app.music.client.controller.Controller;
import com.nublic.app.music.client.controller.ViewKind;
import com.nublic.app.music.client.datamodel.AlbumInfo;
import com.nublic.app.music.client.datamodel.ArtistInfo;
import com.nublic.app.music.client.datamodel.SongInfo;
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

public class SongPanel extends Composite {
	private static SongPanelUiBinder uiBinder = GWT.create(SongPanelUiBinder.class);
	interface SongPanelUiBinder extends UiBinder<Widget, SongPanel> { }

	// CSS Styles defined in the .xml file
	interface SongPanelStyle extends CssResource {
		String scroll();
		String rightmargin();
		String space();
	}
	@UiField SongPanelStyle style;
	
	@UiField FlowPanel mainPanel;
	@UiField Label titleLabel;
	@UiField Label byLabel;
	@UiField HorizontalPanel titlePanel;
	@UiField HorizontalPanel subtitlePanel;
	@UiField ViewTabs viewTabs;
	@UiField PushButton backButton;
	
	String inCollection;
	String albumId;
	String artistId;

	public SongPanel(String albumId, String artistId, String collectionId) {
		initWidget(uiBinder.createAndBindUi(this));

		this.albumId = albumId;
		this.artistId = artistId;
		this.inCollection = collectionId;

		// Get album info (null means all songs)
		if (albumId == null && artistId == null) {
			if (inCollection != null) {
				titleLabel.setText(Controller.INSTANCE.getModel().getTagCache().get(inCollection).getName());
			} else {
				titleLabel.setText(Constants.I18N.allMusic());
			}
			byLabel.setVisible(false);
			subtitlePanel.setVisible(false);
			setViewLinks(true, true);
			backButton.setVisible(false);
		} else if (artistId != null) {
			Cache<String, ArtistInfo> artistCache = Controller.INSTANCE.getModel().getArtistCache();
			artistCache.addHandler(artistId, new CacheHandler<String, ArtistInfo>() {
				@Override
				public void onCacheUpdated(String k, ArtistInfo v) {
					titleLabel.setText(v.getName());
				}
			});
			artistCache.obtain(artistId);
			byLabel.setVisible(false);
			subtitlePanel.setVisible(false);
			setViewLinks(false, true);
			Utils.setBackButton(backButton, collectionId);
		} else {
			Cache<String, AlbumInfo> albumCache = Controller.INSTANCE.getModel().getAlbumCache();
			albumCache.addHandler(albumId, new CacheHandler<String, AlbumInfo>() {
				@Override
				public void onCacheUpdated(String k, AlbumInfo v) {
					titleLabel.setText(v.getName());
					byLabel.setVisible(true);
					setSubtitles(v.getArtistList());
				}
			});
			albumCache.obtain(albumId);
			setViewLinks(false, false);
			Utils.setBackButton(backButton, collectionId);
		}

		// Create button line
		createButtonLine();		
	}
	
	private void createButtonLine() {
		EnumSet<ButtonLineParam> buttonSet = EnumSet.of(ButtonLineParam.ADD_AT_END, ButtonLineParam.PLAY);
		if (inCollection != null && artistId == null && albumId == null) { // We're in an album view of a collection
			buttonSet.add(ButtonLineParam.DELETE);
		}
		ButtonLine b = new ButtonLine(buttonSet);
		setDeleteButtonHandler(b);
		setAddAtEndButtonHandler(b);
		setPlayButtonHandler(b);
		titlePanel.insert(b, 2);
		
	}

	public void setSongList(int total, int from, int to, List<SongInfo> answerList, String albumId, String artistId, String collectionId) {
		if (total != 0) {
			SongList sl = new AlbumSongList(albumId, artistId, collectionId, total, mainPanel);
			sl.addSongs(total, from, to, answerList);
			mainPanel.add(sl);
		} else {
			mainPanel.add(new EmptyWidget());
		}
	}

	private void setViewLinks(boolean showArtist, boolean showAlbum) {
		if (showArtist) {
			String artistTarget = Utils.getTargetHistoryToken(artistId, albumId, inCollection, ViewKind.ARTISTS.toString());
			viewTabs.setTarget(ViewKind.ARTISTS, artistTarget);
			String albumTarget = Utils.getTargetHistoryToken(artistId, albumId, inCollection, ViewKind.ALBUMS.toString());
			viewTabs.setTarget(ViewKind.ALBUMS, albumTarget);
		} else if (showAlbum) {
			String albumTarget = Utils.getTargetHistoryToken(artistId, albumId, inCollection, ViewKind.ALBUMS.toString());
			viewTabs.setTarget(ViewKind.ALBUMS, albumTarget);
			viewTabs.hideTab(ViewKind.ARTISTS);
		} else {
			viewTabs.setVisible(false);
		}
		viewTabs.setSelected(ViewKind.SONGS);
	}

	public void setSubtitles(List<String> artistList) {
		subtitlePanel.clear();
		for (String artistId : artistList) {
			final Hyperlink artistLink = new Hyperlink();
			Label commaLabel = new Label(",");
			commaLabel.getElement().addClassName(style.space());
			Cache<String, ArtistInfo> artistCache = Controller.INSTANCE.getModel().getArtistCache();
			artistCache.addHandler(artistId, new CacheHandler<String, ArtistInfo>() {
				@Override
				public void onCacheUpdated(String k, ArtistInfo v) {
					artistLink.setText(v.getName());
					artistLink.setTargetHistoryToken(v.getTargetHistoryToken());
				}
			});
			artistCache.obtain(artistId);
			subtitlePanel.add(artistLink);
			subtitlePanel.add(commaLabel);
		}
		subtitlePanel.remove(subtitlePanel.getWidgetCount() - 1);
		subtitlePanel.setVisible(true);
	}
	
	// Handlers for button line
	private void setDeleteButtonHandler(ButtonLine b) {
		b.setDeleteButtonHandler(new DeleteButtonHandler() {
			@Override
			public void onDelete() {
				Controller.INSTANCE.deleteTag(inCollection, TagKind.COLLECTION);
			}
		});
	}

	private void setAddAtEndButtonHandler(ButtonLine b) {
		b.setAddAtEndButtonHandler(new AddAtEndButtonHandler() {
			@Override
			public void onAddAtEnd() {
				Controller.INSTANCE.addAtEnd(null, albumId, inCollection);
			}
		});
	}

	private void setPlayButtonHandler(ButtonLine b) {
		b.setPlayButtonHandler(new PlayButtonHandler() {
			@Override
			public void onPlay() {
				Controller.INSTANCE.play(null, albumId, inCollection);
			}
		});
	}

}
