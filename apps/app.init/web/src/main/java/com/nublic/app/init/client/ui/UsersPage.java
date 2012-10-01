package com.nublic.app.init.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class UsersPage extends Composite {
	private static UsersPageUiBinder uiBinder = GWT.create(UsersPageUiBinder.class);
	interface UsersPageUiBinder extends UiBinder<Widget, UsersPage> {}

	public UsersPage() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
