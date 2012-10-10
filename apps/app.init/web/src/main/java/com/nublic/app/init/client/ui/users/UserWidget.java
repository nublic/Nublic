package com.nublic.app.init.client.ui.users;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.init.client.Constants;
import com.nublic.app.init.client.model.InitModel;
import com.nublic.app.init.client.model.handlers.AddUserHandler;
import com.nublic.app.init.client.model.handlers.CheckUserHandler;
import com.nublic.app.init.client.ui.CheckFeedback;
import com.nublic.app.init.client.ui.Feedback;
import com.nublic.app.init.client.ui.MainUi;
import com.nublic.app.init.client.ui.RealChangeHandler;
import com.nublic.util.error.ErrorPopup;

public class UserWidget extends Composite {
	private static UserWidgetUiBinder uiBinder = GWT.create(UserWidgetUiBinder.class);
	interface UserWidgetUiBinder extends UiBinder<Widget, UserWidget> {}

	@UiField TextBox nameBox;
	@UiField PasswordTextBox passwordBox;
	@UiField PasswordTextBox verificationBox;
	@UiField Button createButton;
	@UiField CheckFeedback nameFeedback;
	@UiField CheckFeedback passwordFeedback;
	@UiField CheckFeedback verificationFeedback;
	public final MyCheckUserHandler userChecker = new MyCheckUserHandler();

	public UserWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		
		nameBox.addKeyUpHandler(new RealChangeHandler(nameBox) {
			@Override
			public void onRealChange(String newText) {
				if (newText.isEmpty()) {
					nameFeedback.setFeedback(Feedback.NONE);
				} else {
					nameFeedback.setFeedback(Feedback.LOADING);
					InitModel.INSTANCE.checkUserAvailability(newText, userChecker);
				}
			}
		});
		
		passwordBox.addKeyUpHandler(new RealChangeHandler(passwordBox) {
			@Override
			public void onRealChange(String newText) {
				if (newText.isEmpty()) {
					passwordFeedback.setFeedback(Feedback.NONE);
				} else if (newText.length() < Constants.MIN_PASSWORD_LENGTH) {
					passwordFeedback.setFeedback(Feedback.CROSS);
				} else {
					passwordFeedback.setFeedback(Feedback.CHECK);
				}
				verificatePassword();
			}
		});
		
		verificationBox.addKeyUpHandler(new RealChangeHandler(verificationBox) {
			@Override
			public void onRealChange(String newText) {
				verificatePassword();
			}
		});
	}
	
	public void verificatePassword() {
		String verificationString = verificationBox.getText();
		if (verificationString.isEmpty()) {
			verificationFeedback.setFeedback(Feedback.NONE);
		} else {
			if (verificationString.compareTo(passwordBox.getText()) == 0) {
				verificationFeedback.setFeedback(Feedback.CHECK);
			} else {
				verificationFeedback.setFeedback(Feedback.CROSS);
			}
		}
	}
	
	class MyCheckUserHandler implements CheckUserHandler {
		@Override
		public void onUserChecked(String userName, boolean available) {
			if (userName.compareTo(nameBox.getText()) == 0) {
				// Current name in box has been checked
				if (available) {
					nameFeedback.setFeedback(Feedback.CHECK);
				} else {
					nameFeedback.setFeedback(Feedback.CROSS);
				}
			}
			// else ignore
		}
	}
	
	@UiHandler("createButton")
	void onCreateButtonClick(ClickEvent event) {
		if (nameFeedback.isChecked() && passwordFeedback.isChecked() && verificationFeedback.isChecked()) {
			InitModel.INSTANCE.addUser(nameBox.getText(), passwordBox.getText(), new AddUserHandler() {
				@Override
				public void onUserAdded(String name) {
					MainUi.INSTANCE.addCompletedUser(name);
					emptyBoxes();
				}
			});
		} else {
			ErrorPopup.showError(Constants.I18N.allFieldsError());
		}
	}

	public void emptyBoxes() {
		nameBox.setText("");
		passwordBox.setText("");
		verificationBox.setText("");
		nameFeedback.setFeedback(Feedback.NONE);
		passwordFeedback.setFeedback(Feedback.NONE);
		verificationFeedback.setFeedback(Feedback.NONE);
	}
}