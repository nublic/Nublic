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
import com.nublic.app.browser.web.client.UI.ContextChangeHandler;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HorizontalPanel;

public abstract class ActionWidget extends Composite implements ContextChangeHandler{
	private static ActionWidgetUiBinder uiBinder = GWT.create(ActionWidgetUiBinder.class);
	interface ActionWidgetUiBinder extends UiBinder<Widget, ActionWidget> { }

	@UiField HorizontalPanel rootPanel;
	
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
		
		imageButton.setVisible(false);
		actionLink.setVisible(false);
		
		rootPanel.add(imageButton);
		rootPanel.add(actionLink);
		
		stateProvider.addContextChangeHandler(this);
	}

	public void setExtraInfo(String info) {
		extraInfo = info;
		if (info != null && !info.equals("")) {
			actionLink.setText(actionText + " (" + extraInfo + ")");
		} else {
			actionLink.setText(actionText);
		}
	}
	
	// ContextChangeHadler
	@Override
	public void onContextChange() {
		Availability av = getAvailability();
		
		switch (av) {
			case AVAILABLE:
				setAvailability(true, true);
				break;
			case UNCLICKABLE:
				setAvailability(true, false);
				break;
			case HIDDEN:
				setAvailability(false, false);
				break;
		}
	}

	private void setAvailability(boolean visible, boolean enabled) {
		imageButton.setVisible(visible);
		actionLink.setVisible(visible);
		imageButton.setEnabled(enabled);
		actionLink.setEnabled(enabled);
	}

	// Warning, the implementation of this methods should use "global" variables selectionSet and currentPath
	public abstract void executeAction();
	public abstract Availability getAvailability();
}
