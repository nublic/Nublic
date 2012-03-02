package com.nublic.util.widgets;

import java.util.EnumSet;
import java.util.HashMap;

import com.google.common.collect.HashMultimap;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.event.dom.client.ClickEvent;

public class PopupContent extends Composite {

	private static PopupContentUiBinder uiBinder = GWT.create(PopupContentUiBinder.class);
	
	@UiField DockLayoutPanel dockPanel;
	@UiField Label titleLabel;
	@UiField HTMLPanel mainPanel;
	@UiField Button cancelButton;
	@UiField Button noButton;
	@UiField Button customButton;
	@UiField Button addButton;
	@UiField Button deleteButton;
	@UiField Button uploadButton;
	@UiField Button yesButton;
	@UiField Button okButton;
	
	HashMap<PopupButton, HasVisibility> buttons;
	HashMultimap<PopupButton, PopupButtonHandler> handlers;
	
	int innerHeight = 180;
	
	static int TOP_HEIGHT = 36;
	static int BOTTOM_HEIGHT = 45;
	static int EXTRA_PADDING = 20;
	static int INNER_PADDING = 10;

	interface PopupContentUiBinder extends UiBinder<Widget, PopupContent> {
	}

	public PopupContent(String title, EnumSet<PopupButton> buttonsToShow, Widget w, String customLabel) {
		initWidget(uiBinder.createAndBindUi(this));
		// Add button information
		buttons = new HashMap<PopupButton, HasVisibility>();
		buttons.put(PopupButton.CANCEL, cancelButton);
		buttons.put(PopupButton.NO, noButton);
		buttons.put(PopupButton.CUSTOM, customButton);
		buttons.put(PopupButton.ADD, addButton);
		buttons.put(PopupButton.DELETE, deleteButton);
		buttons.put(PopupButton.UPLOAD, uploadButton);
		buttons.put(PopupButton.YES, yesButton);
		buttons.put(PopupButton.OK, okButton);
		// Show selected buttons
		for (PopupButton b : buttons.keySet()) {
			buttons.get(b).setVisible(buttonsToShow.contains(b));
		}
		// Set labels for title and button
		titleLabel.setText(title);		
		if (customLabel != null) {
			customButton.setText(customLabel);
		}
		// Create palce to save handlers
		handlers = HashMultimap.create();
		// Add inner widget
		addWidget(w);
		// Set initial size
		this.setInnerHeight(180);
	}
	
	public PopupContent(String title, EnumSet<PopupButton> buttonsToShow, Widget w) {
		this(title, buttonsToShow, w, null);
	}
	
	public void addWidget(Widget w) {
		if (w != null) {
			mainPanel.add(w);
			setInnerHeight(innerHeight);
		}
	}

	public void addButtonHandler(PopupButton button, PopupButtonHandler handler) {
		handlers.put(button, handler);
	}
	
	public void setInnerHeight(int h) {
		this.dockPanel.setHeight(String.valueOf(h) + "px");
		int panelHeight = h - TOP_HEIGHT - BOTTOM_HEIGHT - EXTRA_PADDING;
		this.mainPanel.setHeight(String.valueOf(panelHeight) + "px");
		
		if (this.mainPanel.getWidgetCount() > 0) {
			Widget w = this.mainPanel.getWidget(0);
			int widgetHeight = panelHeight - INNER_PADDING;
			int widgetWidth = 380 - INNER_PADDING;
			
			w.setHeight(String.valueOf(widgetHeight) + "px");
			w.setWidth(String.valueOf(widgetWidth) + "px");
		}
		
		this.innerHeight = h;
	}
	
	void handle(PopupButton button, ClickEvent event) {
		for (PopupButtonHandler handler : handlers.get(button)) {
			handler.onClicked(button, event);
		}
	}
	
	@UiHandler("okButton")
	void onOkButtonClick(ClickEvent event) {
		handle(PopupButton.OK, event);
	}
	
	@UiHandler("yesButton")
	void onYesButtonClick(ClickEvent event) {
		handle(PopupButton.YES, event);
	}
	
	@UiHandler("uploadButton")
	void onUploadButtonClick(ClickEvent event) {
		handle(PopupButton.UPLOAD, event);
	}
	
	@UiHandler("deleteButton")
	void onDeleteButtonClick(ClickEvent event) {
		handle(PopupButton.DELETE, event);
	}
	
	@UiHandler("addButton")
	void onAddButtonClick(ClickEvent event) {
		handle(PopupButton.ADD, event);
	}
	
	@UiHandler("customButton")
	void onCustomButtonClick(ClickEvent event) {
		handle(PopupButton.CUSTOM, event);
	}
	
	@UiHandler("noButton")
	void onNoButtonClick(ClickEvent event) {
		handle(PopupButton.NO, event);
	}
	
	@UiHandler("cancelButton")
	void onCancelButtonClick(ClickEvent event) {
		handle(PopupButton.CANCEL, event);
	}
	
	@UiHandler("closeLabel")
	void onCloseLabelClick(ClickEvent event) {
		handle(PopupButton.CLOSE, event);
	}
}
