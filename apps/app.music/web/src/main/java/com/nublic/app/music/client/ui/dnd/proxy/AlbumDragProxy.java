package com.nublic.app.music.client.ui.dnd.proxy;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.music.client.Resources;
import com.nublic.app.music.client.controller.Controller;
import com.nublic.app.music.client.datamodel.AlbumInfo;
import com.nublic.app.music.client.datamodel.ArtistInfo;
import com.nublic.util.cache.CacheHandler;
import com.nublic.util.widgets.ImageHelper;

public class AlbumDragProxy extends Composite implements DragProxy {
	private static AlbumDragProxyUiBinder uiBinder = GWT.create(AlbumDragProxyUiBinder.class);
	interface AlbumDragProxyUiBinder extends UiBinder<Widget, AlbumDragProxy> { }
	
	@UiField Label title;
	@UiField Label numberOfSongs;
	@UiField Label artists;
	@UiField SimplePanel plusPanel;
	@UiField Image albumArt;

	public AlbumDragProxy(String draggingAlbumId, final String draggingArtistId, final int songs) {
		initWidget(uiBinder.createAndBindUi(this));
		
		setImage(AlbumInfo.getAlbumImageUrl(draggingAlbumId));
		
		Controller.INSTANCE.getModel().getAlbumCache().addHandler(draggingAlbumId, new CacheHandler<String, AlbumInfo>() {
			@Override
			public void onCacheUpdated(String k, AlbumInfo v) {
				numberOfSongs.setText(songs + " songs");
				title.setText(v.getName());
				setArtist(draggingArtistId);
			}
		});
		Controller.INSTANCE.getModel().getAlbumCache().obtain(draggingAlbumId);

		setState(ProxyState.NONE);
	}
	
	private void setImage(String albumUrl) {
		ImageHelper.setImage(albumArt, albumUrl, Resources.INSTANCE.album());
	}
	
	private void setArtist(String draggingArtistId) {
		Controller.INSTANCE.getModel().getArtistCache().addHandler(draggingArtistId, new CacheHandler<String, ArtistInfo>() {
			@Override
			public void onCacheUpdated(String k, ArtistInfo v) {
				artists.setText(v.getName());
			}
		});
		Controller.INSTANCE.getModel().getArtistCache().obtain(draggingArtistId);
	}
	
	public void setText(String text) {
		this.title.setText(text);
	}

	@Override
	public void setState(ProxyState state) {
		plusPanel.setVisible(state == ProxyState.PLUS);
	}

}
