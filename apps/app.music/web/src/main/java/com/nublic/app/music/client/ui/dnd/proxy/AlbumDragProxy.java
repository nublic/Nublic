package com.nublic.app.music.client.ui.dnd.proxy;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class AlbumDragProxy extends Composite implements DragProxy {
	private static AlbumDragProxyUiBinder uiBinder = GWT.create(AlbumDragProxyUiBinder.class);
	interface AlbumDragProxyUiBinder extends UiBinder<Widget, AlbumDragProxy> { }
	
	@UiField Label text;
	@UiField SimplePanel plusPanel;
	
//	public SongDragProxy(final SongInfo draggingSong) {
//		initWidget(uiBinder.createAndBindUi(this));
//		
//		Controller.INSTANCE.getModel().getArtistCache().addHandler(draggingSong.getArtistId(), new CacheHandler<String, ArtistInfo>() {
//			@Override
//			public void onCacheUpdated(String k, ArtistInfo v) {
//				text.setText(v.getName() + " - " + draggingSong.getTitle());
//			}
//		});
//		Controller.INSTANCE.getModel().getArtistCache().obtain(draggingSong.getArtistId());
//		
//
//		setState(ProxyState.NONE);
//	}
	public AlbumDragProxy(String draggingAlbumId) {
		initWidget(uiBinder.createAndBindUi(this));
		
		setText(draggingAlbumId);
		setState(ProxyState.NONE);
	}

	
	public void setText(String text) {
		this.text.setText(text);
	}

	@Override
	public void setState(ProxyState state) {
		plusPanel.setVisible(state == ProxyState.PLUS);
	}

}
