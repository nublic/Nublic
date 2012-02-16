package com.nublic.app.manager.settings.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.event.dom.client.ClickEvent;

public class SettingsPage extends Composite {

	private static SettingsPageUiBinder uiBinder = GWT.create(SettingsPageUiBinder.class);
	
	@UiField TabLayoutPanel tabs;

	InlineHyperlink[] links;
	@UiField InlineHyperlink userLink;
	@UiField InlineHyperlink workFoldersLink;
	// @UiField InlineHyperlink nublicLink;

	interface SettingsPageUiBinder extends UiBinder<Widget, SettingsPage> {
	}

	public SettingsPage() {
		initWidget(uiBinder.createAndBindUi(this));
		
		links = new InlineHyperlink[] { userLink, workFoldersLink, /*nublicLink*/ };
		selectTab(0);
	}
	
	public void selectTab(int n) {
		for (int i = 0; i < links.length; i++) {
			InlineHyperlink link = links[i];
			link.getElement().getParentElement().removeClassName("active");
			if (i == n) {
				link.getElement().getParentElement().addClassName("active");
			}
		}
		tabs.selectTab(n);
	}
	
	@UiHandler("userLink")
	void onUserLinkClick(ClickEvent event) {
		selectTab(0);
	}
	
	@UiHandler("workFoldersLink")
	void onWorkFoldersLinkClick(ClickEvent event) {
		selectTab(1);
	}
	
	/*@UiHandler("nublicLink")
	void onNublicLinkClick(ClickEvent event) {
		selectTab(2);
	}*/
}
