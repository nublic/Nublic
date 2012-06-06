package com.nublic.app.market.web.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.market.web.client.controller.Controller;
import com.nublic.app.market.web.client.model.AppInfo;
import com.nublic.app.market.web.client.model.AppStatus;
import com.nublic.util.widgets.AnchorPanel;

public class OpenApp extends Composite {
	private static OpenAppUiBinder uiBinder = GWT.create(OpenAppUiBinder.class);
	interface OpenAppUiBinder extends UiBinder<Widget, OpenApp> { }
	
	@UiField AnchorPanel imageAnchor;
	@UiField Image image;
	@UiField Hyperlink name;
	@UiField Anchor developer;
	@UiField Label description;
	@UiField InstallButton installButton;
	
	AppInfo info;
	
	public OpenApp(AppInfo info) {
		initWidget(uiBinder.createAndBindUi(this));
		this.info = info;
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
		installButton.setButtonFromStatus(info.getStatus());
	}

	@UiHandler("installButton")
	void onInstallButtonClick(ClickEvent event) {
		switch (info.getStatus()) {
		case INSTALLED:
			Controller.INSTANCE.uninstallApp(info.getId());
			break;
		case NOT_INSTALLED:
			Controller.INSTANCE.installApp(info.getId());
			break;
		}
	}
	
	public String getId() {
		return info.getId();
	}

	public void changeStatus(AppStatus newStatus) {
		installButton.removePreviousButtonStatus();
		installButton.setButtonFromStatus(newStatus);
	}
}
