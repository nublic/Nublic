package com.nublic.app.init.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.HTMLPanel;

public class CheckFeedback extends Composite {
	private static CheckFeedbackUiBinder uiBinder = GWT.create(CheckFeedbackUiBinder.class);
	interface CheckFeedbackUiBinder extends UiBinder<Widget, CheckFeedback> {}

	@UiField Image check;
	@UiField Image cross;
	@UiField Image loading;
	@UiField HTMLPanel checkInfo;
	@UiField HTMLPanel crossInfo;
	@UiField HTMLPanel loadingInfo;
	@UiField HTMLPanel noneInfo;
	String checkText = "";
	String crossText = "";
	String loadingText = "";
	String noneText = "";
	
	Feedback state;

	public CheckFeedback() {
		initWidget(uiBinder.createAndBindUi(this));
		
		setFeedback(Feedback.NONE);
	}
	
	public void setCheckInfo(String s) {
		checkText = s;
		checkInfo.getElement().setInnerText(checkText);
	}
	
	public void setCrossInfo(String s) {
		crossText = s;
		crossInfo.getElement().setInnerText(crossText);
	}
	
	public void setLoadingInfo(String s) {
		loadingText = s;
		loadingInfo.getElement().setInnerText(loadingText);
	}
	
	public void setNoneInfo(String s) {
		noneText = s;
		noneInfo.getElement().setInnerText(noneText);
	}
	
	public void setFeedback(Feedback f) {
		state = f;
		
		check.setVisible(f == Feedback.CHECK);
		cross.setVisible(f == Feedback.CROSS);
		loading.setVisible(f == Feedback.LOADING);
		
		checkInfo.setVisible(f == Feedback.CHECK && !checkText.isEmpty());
		crossInfo.setVisible(f == Feedback.CROSS && !crossText.isEmpty());
		loadingInfo.setVisible(f == Feedback.LOADING && !loadingText.isEmpty());
		noneInfo.setVisible(f == Feedback.NONE && !noneText.isEmpty());
	}
	
	public Feedback getState() {
		return state;
	}
	
	public boolean isChecked() {
		return state == Feedback.CHECK;
	}

}
