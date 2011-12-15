package com.nublic.app.browser.web.client.UI;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class NavigationBar extends Composite {
	private static NavigationBarUiBinder uiBinder = GWT.create(NavigationBarUiBinder.class);
	interface NavigationBarUiBinder extends UiBinder<Widget, NavigationBar> { }

	@UiField HorizontalPanel rootPanel;
	
	public NavigationBar() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public void addItem(String name, String link) {
		if (rootPanel.getWidgetCount() != 0) {
			rootPanel.add(new Label(">"));
		}
		rootPanel.add(new Hyperlink(name, link));
	}
	
	public void removeLastItem() {
		int widgetCount = rootPanel.getWidgetCount();
		switch (widgetCount) {
		case 0:
			break;
		case 1:
			rootPanel.remove(0);
			break;
		default:
			rootPanel.remove(widgetCount -1);
			rootPanel.remove(widgetCount -2);
		}
	}
	
	public void reset() {
		rootPanel.clear();
	}

}
