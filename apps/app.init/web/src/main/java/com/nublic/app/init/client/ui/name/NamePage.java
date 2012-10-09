package com.nublic.app.init.client.ui.name;

import java.util.EnumSet;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.init.client.Constants;
import com.nublic.app.init.client.model.Step;
import com.nublic.app.init.client.ui.CentralPanel;

public class NamePage extends CentralPanel {
	private static NamePageUiBinder uiBinder = GWT.create(NamePageUiBinder.class);
	interface NamePageUiBinder extends UiBinder<Widget, NamePage> { }

	@UiField InlineHyperlink previousLink;
	@UiField InlineHyperlink nextLink;
	
	public NamePage() {
		initWidget(uiBinder.createAndBindUi(this));

		previousLink.setTargetHistoryToken(Constants.PARAM_PAGE + "=" + Constants.VALUE_NET_CONFIG);
		nextLink.setTargetHistoryToken(Constants.PARAM_PAGE + "=" + Constants.VALUE_FINISHED);
	}

	@Override
	public boolean canChangeTo(Step s) {
		return EnumSet.of(Step.WELCOME, Step.USERS, Step.MASTER_USER, Step.NET_CONFIG, Step.NAME).contains(s) ||
				(s == Step.FINISHED && false);
	}

}
