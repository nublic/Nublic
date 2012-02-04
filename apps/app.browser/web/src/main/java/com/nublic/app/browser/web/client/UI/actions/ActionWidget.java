package com.nublic.app.browser.web.client.UI.actions;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
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

public abstract class ActionWidget extends Composite implements ContextChangeHandler {
	private static ActionWidgetUiBinder uiBinder = GWT.create(ActionWidgetUiBinder.class);
	interface ActionWidgetUiBinder extends UiBinder<Widget, ActionWidget> { }

	// CSS Styles defined in the .xml file
	interface ActionStyle extends CssResource {
		String margin();
		String fadeout();
	}
	
	@UiField HorizontalPanel rootPanel;
	@UiField ActionStyle style;
	
	String extraInfo = "";
	String actionText;
	PushButton imageButton;
	Anchor actionLink;
	protected final BrowserUi stateProvider;
	HandlerRegistration clickHandlerReg;
	boolean hasHandler;
	
	// TODO: mark this as Deprecated and solve problems..
	public ActionWidget(String imageURL, String actionText, BrowserUi stateProvider) {
		this(new Image(imageURL), actionText, stateProvider);
	}
	
	public ActionWidget(ImageResource res, String actionText, BrowserUi stateProvider) {
		this (new Image(res), actionText, stateProvider);
	}
	
	public ActionWidget(Image image, String actionText, BrowserUi stateProvider) {
		initWidget(uiBinder.createAndBindUi(this));
		this.actionText = actionText;
		this.stateProvider = stateProvider;
		
		imageButton = new PushButton(image);
		actionLink = new Anchor(actionText);

		imageButton.addClickHandler(new MyClickHandler());
		clickHandlerReg = actionLink.addClickHandler(new MyClickHandler());
		hasHandler = true;
		
		imageButton.setVisible(false);
		actionLink.setVisible(false);
		
		rootPanel.add(imageButton);
		rootPanel.add(actionLink);
		
		stateProvider.addContextChangeHandler(this);
	}
	
	private class MyClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			executeAction();
		}
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
		if (visible) {
			imageButton.getElement().addClassName(style.margin());
			// actionLink.getElement().addClassName(style.margin());
			imageButton.setEnabled(enabled);
			actionLink.setEnabled(enabled);
			if (enabled) {
				if (!hasHandler) {
					clickHandlerReg = actionLink.addClickHandler(new MyClickHandler());
					hasHandler = true;
					actionLink.getElement().removeClassName(style.fadeout());
				}
			} else {
				if (hasHandler) {
					clickHandlerReg.removeHandler();
					actionLink.getElement().addClassName(style.fadeout());
					hasHandler = false;
				}
			}
		} else {
			imageButton.getElement().removeClassName(style.margin());
			actionLink.getElement().removeClassName(style.margin());
		}
	}

	// Warning, the implementation of this methods should use selectedFiles and path from stateProvider
	public abstract void executeAction();
	public abstract Availability getAvailability();
}
