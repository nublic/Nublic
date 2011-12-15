package com.nublic.app.browser.web.client.UI;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
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
	@UiField NavStyle style;
	
	public NavigationBar() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	// CSS Styles defined in the .xml file
	interface NavStyle extends CssResource {
		String padding();
		String verticalalignmiddle();
	}
	
	public void addItem(String name, String link) {
		if (rootPanel.getWidgetCount() != 0) {
			Label greater = new Label(">");
			rootPanel.add(greater);
			greater.getElement().addClassName(style.padding());
			greater.getElement().addClassName(style.verticalalignmiddle());
		}
		Hyperlink hyperL = new Hyperlink(name, link);
		rootPanel.add(hyperL);
		hyperL.getElement().addClassName(style.verticalalignmiddle());
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
