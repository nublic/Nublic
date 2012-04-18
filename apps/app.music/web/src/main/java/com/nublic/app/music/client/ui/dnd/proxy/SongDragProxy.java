package com.nublic.app.music.client.ui.dnd.proxy;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.music.client.datamodel.ArtistInfo;
import com.nublic.app.music.client.datamodel.Controller;
import com.nublic.app.music.client.datamodel.SongInfo;
import com.nublic.util.cache.CacheHandler;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Label;

public class SongDragProxy extends Composite implements DragProxy {
	private static SongDragProxyUiBinder uiBinder = GWT.create(SongDragProxyUiBinder.class);
	interface SongDragProxyUiBinder extends UiBinder<Widget, SongDragProxy> {}
	
	@UiField Label text;
	@UiField SimplePanel plusPanel;
	@UiField SimplePanel upPanel;
	@UiField SimplePanel downPanel;
	
	public SongDragProxy(final SongInfo draggingSong) {
		initWidget(uiBinder.createAndBindUi(this));
		
		Controller.INSTANCE.getModel().getArtistCache().addHandler(draggingSong.getArtistId(), new CacheHandler<String, ArtistInfo>() {
			@Override
			public void onCacheUpdated(String k, ArtistInfo v) {
				text.setText(v.getName() + " - " + draggingSong.getTitle());
			}
		});
		Controller.INSTANCE.getModel().getArtistCache().obtain(draggingSong.getArtistId());
		

		setState(ProxyState.NONE);
	}
	
	public void setText(String text) {
		this.text.setText(text);
	}

	@Override
	public void setState(ProxyState state) {
		plusPanel.setVisible(state == ProxyState.PLUS);
		upPanel.setVisible(state == ProxyState.UP);
		downPanel.setVisible(state == ProxyState.DOWN);
	}

}
