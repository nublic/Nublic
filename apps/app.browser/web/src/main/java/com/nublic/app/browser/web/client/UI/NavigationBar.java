package com.nublic.app.browser.web.client.UI;

import java.util.Stack;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class NavigationBar extends Composite {
	private static NavigationBarUiBinder uiBinder = GWT.create(NavigationBarUiBinder.class);
	interface NavigationBarUiBinder extends UiBinder<Widget, NavigationBar> { }

	@UiField HTMLPanel rootPanel;
	@UiField NavStyle style;
	
	Element listElement;
	Stack<Element> elements;
	
	public NavigationBar() {
		initWidget(uiBinder.createAndBindUi(this));
		
		elements = new Stack<Element>();
		listElement = DOM.createElement("ul");
		listElement.setClassName("breadcrumb");
		// Add initial divider
		Element e = DOM.createElement("li");
		Element firstDivider = DOM.createSpan();
		firstDivider.setClassName("divider");
		firstDivider.setInnerText("/");
		e.appendChild(firstDivider);
		listElement.appendChild(e);
		
		rootPanel.getElement().appendChild(listElement);
	}
	
	// CSS Styles defined in the .xml file
	interface NavStyle extends CssResource {
		String padding();
		String labelspadding();
		String verticalalignmiddle();
	}
	
	public void addItem(String name, String link) {
		/*if (rootPanel.getWidgetCount() != 0) {
			Label greater = new Label(">");
			rootPanel.add(greater);
			greater.getElement().addClassName(style.labelspadding());
			greater.getElement().addClassName(style.verticalalignmiddle());
		}
		Hyperlink hyperL = new Hyperlink(name, link);
		rootPanel.add(hyperL);
		hyperL.getElement().addClassName(style.verticalalignmiddle());*/
		
		Element e = DOM.createElement("li");
		Element anchor = DOM.createAnchor();
		anchor.setAttribute("href", "#" + link);
		anchor.setInnerText(name);
		e.appendChild(anchor);
		Element divider = DOM.createSpan();
		divider.setClassName("divider");
		divider.setInnerText("/");
		e.appendChild(divider);
		
		elements.push(e);
		listElement.appendChild(e);
	}
	
	public void removeLastItem() {
		/*int widgetCount = rootPanel.getWidgetCount();
		switch (widgetCount) {
		case 0:
			break;
		case 1:
			rootPanel.remove(0);
			break;
		default:
			rootPanel.remove(widgetCount -1);
			rootPanel.remove(widgetCount -2);
		}*/
		if (!elements.isEmpty()) {
			Element e = elements.pop();
			listElement.removeChild(e);
		}
	}

	public void reset() {
		while (!elements.isEmpty()) {
			removeLastItem();
		}
	}

}
