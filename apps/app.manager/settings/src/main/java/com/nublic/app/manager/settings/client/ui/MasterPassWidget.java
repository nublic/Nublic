package com.nublic.app.manager.settings.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class MasterPassWidget extends Composite {
	private static MasterPassWidgetUiBinder uiBinder = GWT.create(MasterPassWidgetUiBinder.class);
	interface MasterPassWidgetUiBinder extends UiBinder<Widget, MasterPassWidget> {	}

	public MasterPassWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
