package com.nublic.app.manager.settings.client.ui.password;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class PassWithViewer extends Composite implements HasText {
	private static PassWithViewerUiBinder uiBinder = GWT.create(PassWithViewerUiBinder.class);
	interface PassWithViewerUiBinder extends UiBinder<Widget, PassWithViewer> {}

	@UiField Image eyeImage;
	@UiField PasswordTextBox passBox;
	@UiField TextBox textBox;
	@UiField DeckPanel deck;
	
	public PassWithViewer() {
		initWidget(uiBinder.createAndBindUi(this));
		
		eyeImage.addMouseDownHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				switchToText();
			}
		});
		eyeImage.addMouseUpHandler(new MouseUpHandler() {
			@Override
			public void onMouseUp(MouseUpEvent event) {
				switchToPass();
			}
		});
		eyeImage.addMouseOutHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				switchToPass();
			}
		});
		deck.showWidget(0);
	}
	
	private void switchToText() {
		textBox.setText(passBox.getText());
		deck.showWidget(1);
		textBox.setFocus(true);
	}
	
	private void switchToPass() {
		deck.showWidget(0);
		passBox.setFocus(true);
	}

	@Override
	public String getText() {
		return passBox.getText();
	}

	@Override
	public void setText(String text) {
		passBox.setText(text);
	}

}
