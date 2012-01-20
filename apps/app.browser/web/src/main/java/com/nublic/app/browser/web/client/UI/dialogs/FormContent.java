package com.nublic.app.browser.web.client.UI.dialogs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class FormContent extends Composite {
	private static FormContentUiBinder uiBinder = GWT.create(FormContentUiBinder.class);
	interface FormContentUiBinder extends UiBinder<Widget, FormContent> { }

	
	// TODO: fill this
	public FormContent() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
