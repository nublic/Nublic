package com.nublic.app.browser.web.client.UI;

import com.bramosystems.oss.player.core.client.AbstractMediaPlayer;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class EmptyUI extends Composite implements ShowsPlayer {

	private static EmptyUIUiBinder uiBinder = GWT.create(EmptyUIUiBinder.class);

	interface EmptyUIUiBinder extends UiBinder<Widget, EmptyUI> {
	}

//	@UiField FlexTable rootPanel;
//	
//	public EmptyUI() {
//		initWidget(uiBinder.createAndBindUi(this));
//		rootPanel.getFlexCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_MIDDLE);
//		rootPanel.getFlexCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);
//	}
//	
//	@Override
//	public void showPlayer(AbstractMediaPlayer player) {
//		rootPanel.setWidget(0, 0, player);
//	}

	@UiField SimpleLayoutPanel rootPanel;
	
	public EmptyUI() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	@Override
	public void showPlayer(AbstractMediaPlayer player) {
		rootPanel.add(player);
	}

	
}
