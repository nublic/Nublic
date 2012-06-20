package com.nublic.app.market.web.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.market.web.client.controller.Controller;
import com.nublic.app.market.web.client.model.AppInfo;
import com.nublic.app.market.web.client.model.AppStatus;
import com.nublic.app.market.web.client.model.URLInfo;
import com.nublic.app.market.web.client.ui.slider.ImageSlider;

public class OpenApp extends Composite {
	private static OpenAppUiBinder uiBinder = GWT.create(OpenAppUiBinder.class);
	interface OpenAppUiBinder extends UiBinder<Widget, OpenApp> { }

	@UiField Anchor developer;
	@UiField HTMLPanel longDescription;
	@UiField Hyperlink backLink;
	@UiField Image image;
	@UiField ImageSlider imageSlider;
	@UiField InstallButton installButton;
	@UiField Label name;
	@UiField Label shortDescription;
	@UiField OpenStyle style;
	@UiField VerticalPanel bodyPanel;
	
	AppInfo info;
	
	public interface OpenStyle extends CssResource {
		String image();
		String space();
		String shortDescriptionText();
		String description();
		String buttonWidth();
		String longText();
	}
	
	public OpenApp(AppInfo info) {
		initWidget(uiBinder.createAndBindUi(this));
		this.info = info;
		setInfo(info);
		
		backLink.setTargetHistoryToken("");
		backLink.setText("< " + backLink.getText());
	}
	
	public void setInfo(AppInfo info) {
		image.setUrl(info.getIconURL());
		name.setText(info.getName());
		developer.setText(info.getDeveloper().getText());
		developer.setTarget("_blank");
		developer.setHref(info.getDeveloper().getUrl());
		shortDescription.setText(info.getShortDescription());
		longDescription.getElement().setInnerHTML(info.getLongDescription());
		installButton.setButtonFromStatus(info.getStatus());
		for (String url : info.getScreenshotList()) {
			imageSlider.addImage(url);
		}
		for (URLInfo url : info.getLinkList()) {
			Label l = new Label(url.getText() + ":");
			l.addStyleName(style.space()); l.addStyleName(style.longText());
			Anchor a = new Anchor(url.getUrl(), url.getUrl());
			a.setTarget("_blank");
			HorizontalPanel panel = new HorizontalPanel();
			panel.setVerticalAlignment(HorizontalPanel.ALIGN_BOTTOM);
			panel.add(l);
			panel.add(a);
			bodyPanel.add(panel);
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
	
	public String getId() {
		return info.getId();
	}

	public void changeStatus(AppStatus newStatus) {
		installButton.removePreviousButtonStatus();
		installButton.setButtonFromStatus(newStatus);
	}
}
