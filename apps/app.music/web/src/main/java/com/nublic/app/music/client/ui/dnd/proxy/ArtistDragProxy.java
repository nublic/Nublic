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
import com.nublic.app.music.client.datamodel.ArtistInfo;
import com.nublic.util.cache.CacheHandler;
import com.nublic.util.widgets.ImageHelper;

public class ArtistDragProxy extends Composite implements DragProxy {
	private static ArtistDragProxyUiBinder uiBinder = GWT.create(ArtistDragProxyUiBinder.class);
	interface ArtistDragProxyUiBinder extends UiBinder<Widget, ArtistDragProxy> { }

	@UiField Label title;
	@UiField Label numberOfSongs;
	@UiField SimplePanel plusPanel;
	@UiField Image albumArt;

	public ArtistDragProxy(final String draggingArtistId, final int songs) {
		initWidget(uiBinder.createAndBindUi(this));
		
		setImage(ArtistInfo.getImageUrl(draggingArtistId));

		Controller.INSTANCE.getModel().getArtistCache().addHandler(draggingArtistId, new CacheHandler<String, ArtistInfo>() {
			@Override
			public void onCacheUpdated(String k, ArtistInfo v) {
				numberOfSongs.setText(songs + " songs");
				title.setText(v.getName());
			}
		});
		Controller.INSTANCE.getModel().getArtistCache().obtain(draggingArtistId);

		setState(ProxyState.NONE);
	}

	private void setImage(String artistUrl) {
		ImageHelper.setImage(albumArt, artistUrl, Resources.INSTANCE.artist());
	}

	public void setText(String text) {
		this.title.setText(text);
	}

	@Override
	public void setState(ProxyState state) {
		plusPanel.setVisible(state == ProxyState.PLUS);
	}

}
