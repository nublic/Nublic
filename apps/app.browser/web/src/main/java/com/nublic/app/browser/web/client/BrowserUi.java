package com.nublic.app.browser.web.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class BrowserUi extends Composite {

	private static BrowserUiUiBinder uiBinder = GWT.create(BrowserUiUiBinder.class);

	interface BrowserUiUiBinder extends UiBinder<Widget, BrowserUi> {
	}

	public BrowserUi() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
