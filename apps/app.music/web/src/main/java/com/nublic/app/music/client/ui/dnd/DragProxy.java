package com.nublic.app.music.client.ui.dnd;

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

public class DragProxy extends Composite {
	private static DragProxyUiBinder uiBinder = GWT.create(DragProxyUiBinder.class);
	interface DragProxyUiBinder extends UiBinder<Widget, DragProxy> {}
	
	@UiField Label text;
	@UiField SimplePanel plusPanel;
	
	public DragProxy(final SongInfo draggingSong) {
		initWidget(uiBinder.createAndBindUi(this));
		
		Controller.INSTANCE.getModel().getArtistCache().addHandler(draggingSong.getArtistId(), new CacheHandler<String, ArtistInfo>() {
			@Override
			public void onCacheUpdated(String k, ArtistInfo v) {
				text.setText(v.getName() + " - " + draggingSong.getTitle());
			}
		});
		Controller.INSTANCE.getModel().getArtistCache().obtain(draggingSong.getArtistId());
		

		showPlus(false);
	}
	
	public void setText(String text) {
		this.text.setText(text);
	}
	
	public void showPlus(boolean show) {
		plusPanel.setVisible(show);
	}

}
