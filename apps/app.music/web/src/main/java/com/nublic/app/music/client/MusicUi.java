package com.nublic.app.music.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class MusicUi extends Composite {

	private static MusicUiUiBinder uiBinder = GWT.create(MusicUiUiBinder.class);

	interface MusicUiUiBinder extends UiBinder<Widget, MusicUi> {
	}

	public MusicUi() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
