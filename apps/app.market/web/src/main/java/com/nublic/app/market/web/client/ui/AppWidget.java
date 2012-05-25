package com.nublic.app.market.web.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.market.web.client.model.AppInfo;
import com.nublic.util.widgets.AnchorPanel;
import com.google.gwt.user.client.ui.Button;

public class AppWidget extends Composite {
	private static AppWidgetUiBinder uiBinder = GWT.create(AppWidgetUiBinder.class);
	interface AppWidgetUiBinder extends UiBinder<Widget, AppWidget> {}

	@UiField AnchorPanel imageAnchor;
	@UiField Image image;
	@UiField Label name;
	@UiField Label developer;
	@UiField Label description;
	@UiField Button installButton;
	
	public AppWidget(AppInfo app) {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
