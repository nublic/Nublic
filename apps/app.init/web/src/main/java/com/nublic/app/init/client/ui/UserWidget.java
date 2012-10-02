package com.nublic.app.init.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class UserWidget extends Composite {
	private static UserWidgetUiBinder uiBinder = GWT.create(UserWidgetUiBinder.class);
	interface UserWidgetUiBinder extends UiBinder<Widget, UserWidget> {}

	@UiField TextBox nameBox;
	@UiField PasswordTextBox passwordBox;
	@UiField PasswordTextBox verificationBox;
	@UiField Button createButton;

	public UserWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		
		nameBox.addKeyUpHandler(new RealChangeHandler(nameBox) {
			@Override
			public void onRealChange(String newText) {
				Window.alert(newText);
			}
		});
	}
	
	abstract class RealChangeHandler implements KeyUpHandler {
		HasText source;
		String lastString = "";
		
		public RealChangeHandler(HasText source) {
			this.source = source;
		}
		
		public abstract void onRealChange(String newText);
		
		@Override
		public void onKeyUp(KeyUpEvent event) {
			String text = source.getText();
			if (text.compareTo(lastString) != 0) {
				lastString = text;
				onRealChange(text);
			}
		}
	}

}
