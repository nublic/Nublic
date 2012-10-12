package com.nublic.app.init.client.ui.welcome;

import java.util.EnumSet;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.init.client.Constants;
import com.nublic.app.init.client.model.Step;
import com.nublic.app.init.client.ui.CentralPanel;
import com.nublic.app.init.client.ui.FooterPagination;

public class WelcomePage extends CentralPanel {
	private static WelcomePageUiBinder uiBinder = GWT.create(WelcomePageUiBinder.class);
	interface WelcomePageUiBinder extends UiBinder<Widget, WelcomePage> {}

	@UiField FooterPagination footer;
	
	public WelcomePage() {
		initWidget(uiBinder.createAndBindUi(this));

		footer.setLinks("", Constants.PARAM_PAGE + "=" + Constants.VALUE_USERS);
		footer.highlightNext();
	}

	@Override
	public boolean canChangeTo(Step s) {
		return EnumSet.of(Step.WELCOME, Step.USERS).contains(s);
	}

}
