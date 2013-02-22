package com.nublic.app.manager.settings.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.manager.settings.client.ui.password.PassWithViewer;

public class MasterPassWidget extends Composite {
	private static MasterPassWidgetUiBinder uiBinder = GWT.create(MasterPassWidgetUiBinder.class);
	interface MasterPassWidgetUiBinder extends UiBinder<Widget, MasterPassWidget> {	}

	@UiField PassWithViewer passBox;
	
	public MasterPassWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public String getPassword() {
		return passBox.getText();
	}

}
