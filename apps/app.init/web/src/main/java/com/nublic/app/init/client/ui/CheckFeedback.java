package com.nublic.app.init.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;

public class CheckFeedback extends Composite {
	private static CheckFeedbackUiBinder uiBinder = GWT.create(CheckFeedbackUiBinder.class);
	interface CheckFeedbackUiBinder extends UiBinder<Widget, CheckFeedback> {}

	@UiField Image check;
	@UiField Image cross;
	@UiField Image loading;

	public CheckFeedback() {
		initWidget(uiBinder.createAndBindUi(this));
		
		setFeedback(Feedback.NONE);
	}
	
	public void setFeedback(Feedback f) {
		check.setVisible(f == Feedback.CHECK);
		cross.setVisible(f == Feedback.CROSS);
		loading.setVisible(f == Feedback.LOADING);
	}

}
