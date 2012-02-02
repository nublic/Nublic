package com.nublic.app.music.client.ui.popup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Label;

public class InfoFill extends Composite {
	private static InfoFillUiBinder uiBinder = GWT.create(InfoFillUiBinder.class);
	interface InfoFillUiBinder extends UiBinder<Widget, InfoFill> { }
	@UiField Label text;

	public InfoFill(String info) {
		initWidget(uiBinder.createAndBindUi(this));
		
		text.setText(info);
	}

}
