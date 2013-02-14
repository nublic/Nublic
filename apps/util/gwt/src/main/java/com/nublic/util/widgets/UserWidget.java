package com.nublic.util.widgets;

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
import com.nublic.util.gwt.RealChangeHandler;
import com.nublic.util.i18n.Constants;
import com.nublic.util.error.ErrorPopup;
import com.nublic.util.widgets.CheckFeedback;
import com.nublic.util.widgets.Feedback;

public class UserWidget extends Composite {
	private static UserWidgetUiBinder uiBinder = GWT.create(UserWidgetUiBinder.class);
	interface UserWidgetUiBinder extends UiBinder<Widget, UserWidget> {}

	@UiField TextBox nameBox;
	@UiField TextBox systemNameBox;
	@UiField PasswordTextBox passwordBox;
	@UiField PasswordTextBox verificationBox;
	@UiField Button createButton;
	@UiField CheckFeedback nameFeedback;
	@UiField CheckFeedback systemNameFeedback;
	@UiField CheckFeedback passwordFeedback;
	@UiField CheckFeedback verificationFeedback;
	boolean hasTriedCustomName = false;
	public final MyCheckUserHandler userChecker = new MyCheckUserHandler();
	AddUserHandler feedbackHandler;

	public UserWidget(AddUserHandler auh) {
		super();
		setFeedbackHandler(auh);
	}
	
	public UserWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		
		nameBox.addKeyUpHandler(new RealChangeHandler(nameBox) {
			@Override
			public void onRealChange(String newText) {
				if (newText.isEmpty()) {
					nameFeedback.setFeedback(Feedback.NONE);
				} else {
					nameFeedback.setFeedback(Feedback.CHECK);
				}
				if (!hasTriedCustomName) {
					updateSystemName(newText);
				}
			}
		});
		
		systemNameBox.addKeyUpHandler(new RealChangeHandler(systemNameBox) {
			@Override
			public void onRealChange(String newText) {
				hasTriedCustomName = true;
				if (newText.isEmpty()) {
					systemNameFeedback.setFeedback(Feedback.NONE);
				} else {
					if (UserUtils.checkValidName(newText)) {
						systemNameFeedback.setFeedback(Feedback.LOADING);
						UserUtils.checkUserAvailability(newText, userChecker);
					} else {
						systemNameFeedback.setCrossInfo(Constants.I18N.userNameInvalid());
						systemNameFeedback.setFeedback(Feedback.CROSS);
					}
				}
			}
		});
		
		passwordBox.addKeyUpHandler(new RealChangeHandler(passwordBox) {
			@Override
			public void onRealChange(String newText) {
				if (newText.isEmpty()) {
					passwordFeedback.setFeedback(Feedback.NONE);
				} else if (newText.length() < UserUtils.MIN_PASSWORD_LENGTH) {
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
	
	public void setFeedbackHandler(AddUserHandler auh) {
		feedbackHandler = auh;
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
			if (userName.compareTo(systemNameBox.getText()) == 0) {
				// Current name in box has been checked
				if (available) {
					systemNameFeedback.setFeedback(Feedback.CHECK);
				} else {
					systemNameFeedback.setCrossInfo(Constants.I18N.userNameNotAvailable());
					systemNameFeedback.setFeedback(Feedback.CROSS);
				}
			}
			// else ignore
		}
	}
	
	@UiHandler("createButton")
	void onCreateButtonClick(ClickEvent event) {
		if (nameFeedback.isChecked() &&
				systemNameFeedback.isChecked() &&
				passwordFeedback.isChecked() &&
				verificationFeedback.isChecked()) {
			UserUtils.addUser(systemNameBox.getText(),
					nameBox.getText(),
					passwordBox.getText(),
					feedbackHandler);
			emptyBoxes();
//					new AddUserHandler() {
//				@Override
//				public void onUserAdded(String systemName, String shownName) {
//					MainUi.INSTANCE.addCompletedUser(systemName, shownName);
//					emptyBoxes();
//				}
//			});
		} else {
			ErrorPopup.showError(Constants.I18N.allFieldsError());
		}
	}
	
	
	public void updateSystemName(String realName) {
		String userName = UserUtils.getSystemName(realName);
		systemNameBox.setText(userName);
		if (userName.isEmpty()) {
			systemNameFeedback.setFeedback(Feedback.NONE);
		} else {
			systemNameFeedback.setFeedback(Feedback.LOADING);
			UserUtils.checkUserAvailability(userName, userChecker);
		}
	}

	public void emptyBoxes() {
		nameBox.setText("");
		systemNameBox.setText("");
		passwordBox.setText("");
		verificationBox.setText("");
		nameFeedback.setFeedback(Feedback.NONE);
		systemNameFeedback.setFeedback(Feedback.NONE);
		passwordFeedback.setFeedback(Feedback.NONE);
		verificationFeedback.setFeedback(Feedback.NONE);
		hasTriedCustomName = false;
	}
}
