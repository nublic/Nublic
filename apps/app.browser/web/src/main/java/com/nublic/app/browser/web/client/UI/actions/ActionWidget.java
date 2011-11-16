package com.nublic.app.browser.web.client.UI.actions;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.browser.web.client.UI.BrowserUi;

public abstract class ActionWidget extends Composite {
	private static ActionWidgetUiBinder uiBinder = GWT.create(ActionWidgetUiBinder.class);
	interface ActionWidgetUiBinder extends UiBinder<Widget, ActionWidget> { }

	String extraInfo = "";
	String actionText;
	PushButton imageButton;
	Anchor actionLink;
	BrowserUi stateProvider;
	
	public ActionWidget(String imageURL, String actionText, BrowserUi stateProvider) {
		initWidget(uiBinder.createAndBindUi(this));
		this.actionText = actionText;
		this.stateProvider = stateProvider;
		
		imageButton = new PushButton(new Image(imageURL));
		actionLink = new Anchor(actionText);
		
		actionLink.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				executeAction();
			}
		});
		imageButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				executeAction();
			}
		});
	}

	public void setExtraInfo(String info) {
		extraInfo = info;
		if (info != null && !info.equals("")) {
			actionLink.setText(actionText + " (" + extraInfo + ")");
		} else {
			actionLink.setText(actionText);
		}
	}
	
	// Warning, the implementation of this methods should use "global" variables selectionSet and currentPath
	public abstract void executeAction();
	public abstract Availability getAvailability();
}
