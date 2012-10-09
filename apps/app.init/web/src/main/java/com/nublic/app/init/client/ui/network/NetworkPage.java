package com.nublic.app.init.client.ui.network;

import java.util.EnumSet;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.init.client.Constants;
import com.nublic.app.init.client.model.Step;
import com.nublic.app.init.client.ui.CentralPanel;

public class NetworkPage extends CentralPanel {
	private static NetworkPageUiBinder uiBinder = GWT.create(NetworkPageUiBinder.class);
	interface NetworkPageUiBinder extends UiBinder<Widget, NetworkPage> { }

	@UiField InlineHyperlink previousLink;
	@UiField InlineHyperlink nextLink;
	
	public NetworkPage() {
		initWidget(uiBinder.createAndBindUi(this));
		
		previousLink.setTargetHistoryToken(Constants.PARAM_PAGE + "=" + Constants.VALUE_MASTER_USER);
		nextLink.setTargetHistoryToken(Constants.PARAM_PAGE + "=" + Constants.VALUE_NAME);
	}

	@Override
	public boolean canChangeTo(Step s) {
		return EnumSet.of(Step.WELCOME, Step.USERS, Step.MASTER_USER, Step.NET_CONFIG).contains(s) ||
				(s == Step.NAME && true);
	}
}
