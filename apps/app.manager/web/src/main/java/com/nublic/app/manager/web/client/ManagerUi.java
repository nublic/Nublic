package com.nublic.app.manager.web.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.NamedFrame;
import com.google.gwt.user.client.ui.TabBar;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;

public class ManagerUi extends Composite {

	private static ManagerUiUiBinder uiBinder = GWT.create(ManagerUiUiBinder.class);
	@UiField NamedFrame innerFrame;
	@UiField TabBar appBar;

	interface ManagerUiUiBinder extends UiBinder<Widget, ManagerUi> {
	}

	public ManagerUi() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public void setFrameUrl(String url) {
		innerFrame.setUrl(url);
	}

	public void addTab(String name, String image, String url) {
		HorizontalPanel tab = new HorizontalPanel();
		tab.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		tab.add(new Image(image));
		tab.add(new Label(name));
		appBar.addTab(tab);
	}
}
