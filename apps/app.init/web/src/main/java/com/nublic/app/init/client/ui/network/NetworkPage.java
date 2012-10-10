package com.nublic.app.init.client.ui.network;

import java.util.EnumSet;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.init.client.Constants;
import com.nublic.app.init.client.model.Step;
import com.nublic.app.init.client.ui.CentralPanel;
import com.nublic.app.init.client.ui.FooterPagination;

public class NetworkPage extends CentralPanel {
	private static NetworkPageUiBinder uiBinder = GWT.create(NetworkPageUiBinder.class);
	interface NetworkPageUiBinder extends UiBinder<Widget, NetworkPage> { }

	@UiField FooterPagination footer;
	
	public NetworkPage() {
		initWidget(uiBinder.createAndBindUi(this));
		
		footer.setLinks(Constants.PARAM_PAGE + "=" + Constants.VALUE_MASTER_USER,
				Constants.PARAM_PAGE + "=" + Constants.VALUE_NAME);
	}

	@Override
	public boolean canChangeTo(Step s) {
		return EnumSet.of(Step.WELCOME, Step.USERS, Step.MASTER_USER, Step.NET_CONFIG).contains(s) ||
				(s == Step.NAME && true);
	}
}
