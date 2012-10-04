package com.nublic.app.init.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class CompletedUserWidget extends Composite {
	private static CompletedUserWidgetUiBinder uiBinder = GWT.create(CompletedUserWidgetUiBinder.class);
	interface CompletedUserWidgetUiBinder extends UiBinder<Widget, CompletedUserWidget> {}

	public CompletedUserWidget(String name) {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
