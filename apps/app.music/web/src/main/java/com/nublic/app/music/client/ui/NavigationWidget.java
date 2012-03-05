package com.nublic.app.music.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class NavigationWidget extends Composite {
	private static NavigationWidgetUiBinder uiBinder = GWT.create(NavigationWidgetUiBinder.class);
	interface NavigationWidgetUiBinder extends UiBinder<Widget, NavigationWidget> { }

	public NavigationWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public void setSelected(boolean b) {
		if (b) {
			this.getElement().addClassName("active");
		} else {
			this.getElement().removeClassName("active");
		}
	}

}
