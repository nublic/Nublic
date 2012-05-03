package com.nublic.app.music.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class EmptyWidget extends Composite {
	private static EmptyWidgetUiBinder uiBinder = GWT.create(EmptyWidgetUiBinder.class);
	interface EmptyWidgetUiBinder extends UiBinder<Widget, EmptyWidget> { }

	public EmptyWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
