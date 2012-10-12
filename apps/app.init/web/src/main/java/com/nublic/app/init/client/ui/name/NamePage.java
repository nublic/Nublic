package com.nublic.app.init.client.ui.name;

import java.util.EnumSet;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.init.client.Constants;
import com.nublic.app.init.client.model.InitModel;
import com.nublic.app.init.client.model.Step;
import com.nublic.app.init.client.model.handlers.CheckNublicNameHandler;
import com.nublic.app.init.client.ui.CentralPanel;
import com.nublic.app.init.client.ui.CheckFeedback;
import com.nublic.app.init.client.ui.Feedback;
import com.nublic.app.init.client.ui.FooterPagination;
import com.nublic.app.init.client.ui.RealChangeHandler;

public class NamePage extends CentralPanel {
	private static NamePageUiBinder uiBinder = GWT.create(NamePageUiBinder.class);
	interface NamePageUiBinder extends UiBinder<Widget, NamePage> { }

	@UiField FooterPagination footer;
	@UiField TextBox nameBox;
	@UiField CheckFeedback nameFeedback;
	@UiField Label feedbackLink;
	@UiField VerticalPanel feedbackPanel;
	public final MyCheckNublicNameHandler nublicNameChecker = new MyCheckNublicNameHandler();
	
	public NamePage() {
		initWidget(uiBinder.createAndBindUi(this));

		footer.setLinks(Constants.PARAM_PAGE + "=" + Constants.VALUE_NET_CONFIG,
				Constants.PARAM_PAGE + "=" + Constants.VALUE_FINISHED);

		feedbackPanel.setVisible(false);
		nameBox.addKeyUpHandler(new RealChangeHandler(nameBox) {
			@Override
			public void onRealChange(String newText) {
				if (newText.isEmpty()) {
					nameFeedback.setFeedback(Feedback.NONE);
					feedbackPanel.setVisible(false);
					footer.unhighlightNext();
				} else {
					nameFeedback.setFeedback(Feedback.LOADING);
					feedbackPanel.setVisible(false);
					footer.unhighlightNext();
					InitModel.INSTANCE.checkNublicNameAvailability(newText, nublicNameChecker);
				}
			}
		});
	}
	
	class MyCheckNublicNameHandler implements CheckNublicNameHandler {
		@Override
		public void onNublicNameChecked(String nublicName, boolean available) {
			if (nublicName.compareTo(nameBox.getText()) == 0) {
				// Current name in box has been checked
				if (available) {
					nameFeedback.setFeedback(Feedback.CHECK);
					feedbackLink.setText(nublicName + ".nublic.me");
					feedbackPanel.setVisible(true);
					footer.highlightNext();
				} else {
					nameFeedback.setFeedback(Feedback.CROSS);
					feedbackPanel.setVisible(false);
					footer.unhighlightNext();
				}
			}
			// else ignore
		}
	}

	@Override
	public boolean canChangeTo(Step s) {
		return EnumSet.of(Step.WELCOME, Step.USERS, Step.MASTER_USER, Step.NET_CONFIG, Step.NAME).contains(s) ||
				(s == Step.FINISHED && false);
	}

}
