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
import com.nublic.app.browser.web.client.Resources;

public class NavigationBar extends Composite {
	private static NavigationBarUiBinder uiBinder = GWT.create(NavigationBarUiBinder.class);
	interface NavigationBarUiBinder extends UiBinder<Widget, NavigationBar> { }

	@UiField HTMLPanel rootPanel;
	@UiField NavStyle style;
	
	Element listElement;
	Stack<Element> elements;
	
	// CSS Styles defined in the .xml file
	interface NavStyle extends CssResource {
		String padding();
		String labelspadding();
		String verticalalignmiddle();
		String homesize();
	}
	
	public NavigationBar() {
		initWidget(uiBinder.createAndBindUi(this));
		
		elements = new Stack<Element>();
		listElement = DOM.createElement("ul");
		listElement.setClassName("breadcrumb");
		addInitialHome();
		rootPanel.getElement().appendChild(listElement);
	}
	
	private void addInitialHome() {
		Element e = DOM.createElement("li");
		Element anchor = DOM.createAnchor();
		anchor.setAttribute("href", "#");

		Element image = DOM.createImg();
		image.setAttribute("src", Resources.INSTANCE.home().getSafeUri().asString());
		image.addClassName(style.homesize());

		anchor.appendChild(image);
		e.appendChild(anchor);

		Element divider = DOM.createSpan();
		divider.setClassName("divider");
		divider.setInnerText("/");
		e.appendChild(divider);
		listElement.appendChild(e);
	}

	public void addItem(String name, String link) {
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
