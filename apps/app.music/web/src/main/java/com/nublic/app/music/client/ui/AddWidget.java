package com.nublic.app.music.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class AddWidget extends Composite {
	private static AddWidgetUiBinder uiBinder = GWT.create(AddWidgetUiBinder.class);
	interface AddWidgetUiBinder extends UiBinder<Widget, AddWidget> { }

	@UiField Anchor addAnchor;
	@UiField TextBox addTextBox;
	
	public AddWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		
		addTextBox.setVisible(false);
	}
	
	@UiHandler("addAnchor")
	void onAddAnchorClick(ClickEvent event) {
		addAnchor.setVisible(false);
		addTextBox.setVisible(true);
	}
	@UiHandler("addTextBox")
	void onAddTextBoxBlur(BlurEvent event) {
		addAnchor.setVisible(true);
		addTextBox.setVisible(false);
	}
}
