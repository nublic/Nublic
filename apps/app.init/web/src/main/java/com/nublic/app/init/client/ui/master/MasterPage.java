package com.nublic.app.init.client.ui.master;

import java.util.EnumSet;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.init.client.Constants;
import com.nublic.app.init.client.model.Step;
import com.nublic.app.init.client.ui.CentralPanel;

public class MasterPage extends CentralPanel {
	private static MasterPageUiBinder uiBinder = GWT.create(MasterPageUiBinder.class);
	interface MasterPageUiBinder extends UiBinder<Widget, MasterPage> { }

	@UiField InlineHyperlink previousLink;
	@UiField InlineHyperlink nextLink;

	public MasterPage() {
		initWidget(uiBinder.createAndBindUi(this));
		
		previousLink.setTargetHistoryToken(Constants.PARAM_PAGE + "=" + Constants.VALUE_USERS);
		nextLink.setTargetHistoryToken(Constants.PARAM_PAGE + "=" + Constants.VALUE_NET_CONFIG);
	}

	@Override
	public boolean canChangeTo(Step s) {
		return EnumSet.of(Step.WELCOME, Step.USERS, Step.MASTER_USER).contains(s) ||
				(s == Step.NET_CONFIG && false);
	}

}
