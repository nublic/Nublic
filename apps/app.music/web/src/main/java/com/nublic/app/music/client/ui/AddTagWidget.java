package com.nublic.app.music.client.ui;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.music.client.datamodel.handlers.PutTagHandler;

public class AddTagWidget extends Composite implements HasText {
	private static AddTagWidgetUiBinder uiBinder = GWT.create(AddTagWidgetUiBinder.class);
	interface AddTagWidgetUiBinder extends UiBinder<Widget, AddTagWidget> {	}
	
	@UiField HTMLPanel panel1;
	@UiField HTMLPanel panel2;
	@UiField PushButton add1;
	@UiField PushButton add2;
	@UiField TextBox textBox;
	boolean buttonHasFocus = false;
	boolean textHasFocus = false;
	List<PutTagHandler> tagHandlerList = new ArrayList<PutTagHandler>();

	public AddTagWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiHandler("add1")
	void onAdd1Click(ClickEvent event) {
		setPanel2();
	}

	// To handle lost of focus by entire widget
	@UiHandler("textBox")
	void onTextBoxBlur(BlurEvent event) {
		textHasFocus = false;
		if (!buttonHasFocus) {
			setPanel1();
		}
	}
	@UiHandler("add2")
	void onAdd2Blur(BlurEvent event) {
		buttonHasFocus = false;
		if (!textHasFocus) {
			setPanel1();
		}
	}
	@UiHandler("textBox")
	void onTextBoxFocus(FocusEvent event) {
		textHasFocus = true;
	}
	@UiHandler("add2")
	void onAdd2MouseDown(MouseDownEvent event) {
		buttonHasFocus = true;
	}
	// End of focus handling
	
	// Change between panel with a single button and panel with textbox
	private void setPanel1() {
		panel1.setVisible(true);
		panel2.setVisible(false);
	}
	
	private void setPanel2() {
		panel1.setVisible(false);
		panel2.setVisible(true);
		textBox.setFocus(true);
		textBox.setText("New collection");
		textBox.selectAll();
	}

	// Actual add tag action
	@UiHandler("add2")
	void onAdd2Click(ClickEvent event) {
		addTag();
	}

	@UiHandler("textBox")
	void onTextBoxKeyDown(KeyDownEvent event) {
		switch (event.getNativeKeyCode()) {
		case KeyCodes.KEY_ENTER:
			addTag();
			break;
		case KeyCodes.KEY_ESCAPE:
			setPanel1();
			break;
		}
	}
	
	private void addTag() {
		for (PutTagHandler pth : tagHandlerList) {
			pth.onPutTag();
		}
		setPanel1();
	}

	// Handlers to call when action is done
	public void addPutTagHandler(PutTagHandler putTagHandler) {
		tagHandlerList.add(putTagHandler);
	}

	@Override
	public String getText() {
		return textBox.getText();
	}

	@Override
	public void setText(String text) {
		textBox.setText(text);
	}

}
