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
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Hyperlink;

public class AppWidget extends Composite {
	private static AppWidgetUiBinder uiBinder = GWT.create(AppWidgetUiBinder.class);
	interface AppWidgetUiBinder extends UiBinder<Widget, AppWidget> {}

	@UiField AnchorPanel imageAnchor;
	@UiField Image image;
	@UiField Hyperlink name;
	@UiField Anchor developer;
	@UiField Label description;
	@UiField Button installButton;
	
	public AppWidget(AppInfo info) {
		initWidget(uiBinder.createAndBindUi(this));
		setInfo(info);
	}
	
	public void setInfo(AppInfo info) {
		image.setUrl(info.getIconURL());
		imageAnchor.setHref("#" + info.getAppPageTarget());
		name.setTargetHistoryToken(info.getAppPageTarget());
		name.setText(info.getName());
		developer.setText(info.getDeveloper().getText());
		developer.setTarget("_blank");
		developer.setHref(info.getDeveloper().getUrl());
		description.setText(info.getShortDescription());
	}

}
