package com.nublic.app.init.client.ui.step;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class StepWidget extends Composite {
	private static StepWidgetUiBinder uiBinder = GWT.create(StepWidgetUiBinder.class);
	interface StepWidgetUiBinder extends UiBinder<Widget, StepWidget> { }
	
	interface StepStyle extends CssResource {
		String common();
	    String crumb();
	    String completed();
	    String selected();
	    String unreached();
	}

	@UiField StepStyle style;
	@UiField Image icon;
	@UiField Label name;
	@UiField VerticalPanel mainPanel;

	public StepWidget(String stepName, String iconURL) {
		initWidget(uiBinder.createAndBindUi(this));
		
		name.setText(stepName);
		icon.setUrl(iconURL);
	}


	public void setStatus(StepStatus s) {
		switch (s) {
		case COMPLETED:
			mainPanel.addStyleName(style.completed());
			mainPanel.removeStyleName(style.selected());
			mainPanel.removeStyleName(style.unreached());
			break;
		case SELECTED:
			mainPanel.removeStyleName(style.completed());
			mainPanel.addStyleName(style.selected());
			mainPanel.removeStyleName(style.unreached());
			break;
		case UNREACHED:
			mainPanel.removeStyleName(style.completed());
			mainPanel.removeStyleName(style.selected());
			mainPanel.addStyleName(style.unreached());
			break;
		}
	}

}
