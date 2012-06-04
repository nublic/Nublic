package com.nublic.app.market.web.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.market.web.client.controller.Controller;
import com.nublic.app.market.web.client.model.AppInfo;
import com.nublic.util.widgets.AnchorPanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.event.dom.client.ClickEvent;

public class AppWidget extends Composite {
	private static AppWidgetUiBinder uiBinder = GWT.create(AppWidgetUiBinder.class);
	interface AppWidgetUiBinder extends UiBinder<Widget, AppWidget> {}

	@UiField AnchorPanel imageAnchor;
	@UiField Image image;
	@UiField Hyperlink name;
	@UiField Anchor developer;
	@UiField Label description;
	@UiField Button installButton;
	
	AppInfo info;
	
	public AppWidget(AppInfo info) {
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
		setButton();
	}

	private void setButton() {
		installButton.addStyleName(info.getStatus().getCss());
		installButton.setText(info.getStatus().getI18NStr());
		installButton.setEnabled(info.getStatus().isClickable());
		if (info.getStatus().isClickable()) {
			installButton.addMouseOverHandler(new MouseOverHandler() {
				@Override
				public void onMouseOver(MouseOverEvent event) {
					installButton.setText(info.getStatus().getHoverStr());
					installButton.removeStyleName(info.getStatus().getCss());
					installButton.addStyleName(info.getStatus().getHoverCss());
				}
			});
			installButton.addMouseOutHandler(new MouseOutHandler() {
				@Override
				public void onMouseOut(MouseOutEvent event) {
					installButton.setText(info.getStatus().getI18NStr());
					installButton.removeStyleName(info.getStatus().getHoverCss());
					installButton.addStyleName(info.getStatus().getCss());
				}
			});
		}
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
}
