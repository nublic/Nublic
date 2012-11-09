package com.nublic.app.init.client.ui.finished;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.init.client.model.Step;
import com.nublic.app.init.client.ui.CentralPanel;

public class FinishedPage extends CentralPanel {
	private static FinishedPageUiBinder uiBinder = GWT.create(FinishedPageUiBinder.class);
	interface FinishedPageUiBinder extends UiBinder<Widget, FinishedPage> { }

	public FinishedPage() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public boolean canChangeTo(Step s) {
		return s == Step.FINISHED;
	}
}
