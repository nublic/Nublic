package com.nublic.app.manager.settings.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.manager.settings.client.Constants;
import com.nublic.app.manager.settings.client.Model;
import com.nublic.app.manager.settings.client.RealChangeHandler;
import com.nublic.app.manager.settings.client.comm.ChangePassCallback;
import com.nublic.app.manager.settings.client.comm.User;
import com.nublic.app.manager.settings.client.comm.UserMessageCallback;
import com.nublic.util.widgets.CheckFeedback;
import com.nublic.util.widgets.EditableLabel;
import com.nublic.util.widgets.Feedback;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Button;

public class PersonalPage extends Composite {
	private static PersonalPageUiBinder uiBinder = GWT.create(PersonalPageUiBinder.class);
	interface PersonalPageUiBinder extends UiBinder<Widget, PersonalPage> { }

	@UiField EditableLabel shownName;
	@UiField Label systemName;
	@UiField Label changedLabel;
	@UiField PasswordTextBox oldPass;
	@UiField PasswordTextBox newPass;
	@UiField PasswordTextBox verificatePass;
	@UiField CheckFeedback oldPassFeedback;
	@UiField CheckFeedback newPassFeedback;
	@UiField CheckFeedback verificationPassFeedback;
	@UiField Button changePasswordButton;
	@UiField PersonalStyle style;
	Timer t = null;

	// CSS Styles defined in the .xml file
	interface PersonalStyle extends CssResource {
		String opaque();
	}
	
	public PersonalPage() {
		initWidget(uiBinder.createAndBindUi(this));

		Model.INSTANCE.getUserInfo(new UserMessageCallback() {
			@Override
			public void onUserMessage(User u) {
				shownName.setText(u.getShownName());
				systemName.setText(u.getSystemName());
			}
		});
		

		newPass.addKeyUpHandler(new RealChangeHandler(newPass) {
			@Override
			public void onRealChange(String newText) {
				if (newText.isEmpty()) {
					newPassFeedback.setFeedback(Feedback.NONE);
				} else if (newText.length() < Constants.MIN_PASSWORD_LENGTH) {
					newPassFeedback.setFeedback(Feedback.CROSS);
				} else {
					newPassFeedback.setFeedback(Feedback.CHECK);
				}
				verificatePassword();
			}
		});
		
		verificatePass.addKeyUpHandler(new RealChangeHandler(verificatePass) {
			@Override
			public void onRealChange(String newText) {
				verificatePassword();
			}
		});
	}
	
	public void verificatePassword() {
		String verificationString = verificatePass.getText();
		if (verificationString.isEmpty()) {
			verificationPassFeedback.setFeedback(Feedback.NONE);
		} else {
			if (verificationString.compareTo(newPass.getText()) == 0) {
				verificationPassFeedback.setFeedback(Feedback.CHECK);
			} else {
				verificationPassFeedback.setFeedback(Feedback.CROSS);
			}
		}
	}

	@UiHandler("shownName")
	void onShownNameValueChange(ValueChangeEvent<String> event) {
		Model.INSTANCE.setUserShownName(event.getValue());
	}
	
	@UiHandler("changePasswordButton")
	void onChangePasswordButtonClick(ClickEvent event) {
		if (verificationPassFeedback.getState() == Feedback.CHECK && newPassFeedback.getState() == Feedback.CHECK) {
			Model.INSTANCE.changePassword(oldPass.getText(), newPass.getText(), new ChangePassCallback() {
				@Override
				public void onPasswordChanged(boolean succeed) {
					if (succeed) {
						cleanup();
						showFeedback();
					} else {
						oldPassFeedback.setFeedback(Feedback.CROSS);
						hideFeedback();
					}
				}
			});
		}
	}

	private void showFeedback() {
		if (t == null) {
			t = new Timer() {
				@Override
				public void run() {
					changedLabel.addStyleName(style.opaque());
				}
			};
		}
		t.schedule(250);
	}
	
	private void hideFeedback() {
		changedLabel.removeStyleName(style.opaque());
	}

	private void cleanup() {
		hideFeedback();
		oldPass.setText("");
		newPass.setText("");
		verificatePass.setText("");
		oldPassFeedback.setFeedback(Feedback.NONE);
		newPassFeedback.setFeedback(Feedback.NONE);
		verificationPassFeedback.setFeedback(Feedback.NONE);
	}
}
