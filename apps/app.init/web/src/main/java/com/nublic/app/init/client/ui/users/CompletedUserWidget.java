package com.nublic.app.init.client.ui.users;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Label;
import com.nublic.app.init.client.ui.CheckFeedback;

public class CompletedUserWidget extends Composite {
	private static CompletedUserWidgetUiBinder uiBinder = GWT.create(CompletedUserWidgetUiBinder.class);
	interface CompletedUserWidgetUiBinder extends UiBinder<Widget, CompletedUserWidget> {}

	@UiField Label nameLabel;
	@UiField CheckFeedback checkFeedback;

	public CompletedUserWidget(String name) {
		initWidget(uiBinder.createAndBindUi(this));
		
		nameLabel.setText(name);
	}

}
