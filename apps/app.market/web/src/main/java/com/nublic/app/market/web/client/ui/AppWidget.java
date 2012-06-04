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
import com.nublic.app.market.web.client.model.AppInfo;
import com.nublic.app.market.web.client.model.AppStatus;
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
		setButton(info.getStatus());
		installButton.addStyleName(info.getStatus().getCss());
		installButton.setText(info.getStatus().getI18NStr());
		installButton.setEnabled(info.getStatus().isClickable());
	}

	private void setButton(final AppStatus status) {
		installButton.addStyleName(status.getCss());
		installButton.setText(status.getI18NStr());
		installButton.setEnabled(status.isClickable());
		if (status.isClickable()) {
			installButton.addMouseOverHandler(new MouseOverHandler() {
				@Override
				public void onMouseOver(MouseOverEvent event) {
					installButton.setText(status.getHoverStr());
					installButton.removeStyleName(status.getCss());
					installButton.addStyleName(status.getHoverCss());
				}
			});
			installButton.addMouseOutHandler(new MouseOutHandler() {
				@Override
				public void onMouseOut(MouseOutEvent event) {
					installButton.setText(status.getI18NStr());
					installButton.removeStyleName(status.getHoverCss());
					installButton.addStyleName(status.getCss());
				}
			});
		}
	}
}
