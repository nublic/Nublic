package com.nublic.app.manager.web.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class TopBar extends Composite {

	private static TopBarUiBinder uiBinder = GWT.create(TopBarUiBinder.class);
	
	@UiField HTMLPanel root;
	
	Element primaryNav;
	Element secondaryNav;
	
	String currentActiveElementId = null;
	HashMap<String, Element> elementsInPrimaryTab;
	HashMap<String, String> mapTextsInPrimaryTab;
	ArrayList<String> textsInPrimaryTab;
	HashMap<String, Element> elementsInSecondaryTab;
	HashMap<String, String> mapTextsInSecondaryTab;
	ArrayList<String> textsInSecondaryTab;

	interface TopBarUiBinder extends UiBinder<Widget, TopBar> {
	}

	public TopBar() {
		initWidget(uiBinder.createAndBindUi(this));
		elementsInPrimaryTab = new HashMap<String, Element>();
		elementsInSecondaryTab = new HashMap<String, Element>();
		mapTextsInPrimaryTab = new HashMap<String, String>();
		mapTextsInSecondaryTab = new HashMap<String, String>();
		textsInPrimaryTab = new ArrayList<String>();
		textsInSecondaryTab = new ArrayList<String>();
		
		// Create hierarchy of elements
		Element top = DOM.createDiv();
		top.setClassName("topbar");
		Element topInner = DOM.createDiv();
		topInner.setClassName("topbar-inner");
		topInner.addClassName("container-fluid");
		top.appendChild(topInner);
		// Create title
		Element title = DOM.createElement("h3");
		Element titleAnchor = DOM.createAnchor();
		titleAnchor.setAttribute("href", "#welcome");
		titleAnchor.setInnerText("Nublic");
		title.appendChild(titleAnchor);
		topInner.appendChild(title);
		// Primary navigation
		primaryNav = DOM.createElement("ul");
		primaryNav.setClassName("nav");
		topInner.appendChild(primaryNav);
		// Separation
		Element separation = DOM.createDiv();
		separation.setClassName("pull-left");
		topInner.appendChild(separation);
		// Secondary navigation
		secondaryNav = DOM.createElement("ul");
		secondaryNav.setClassName("nav");
		secondaryNav.addClassName("secondary-nav");
		topInner.appendChild(secondaryNav);
		
		root.getElement().appendChild(top);
	}
	
	public Set<String> getElementsInPrimaryBar() {
		return elementsInPrimaryTab.keySet();
	}
	
	public Set<String> getElementsInSecondaryBar() {
		return elementsInSecondaryTab.keySet();
	}
	
	public void addToPrimaryTab(String id, String image, String text, String link) {
		if (!elementsInPrimaryTab.containsKey(id) && !elementsInSecondaryTab.containsKey(id)) {
			addToTab(id, image, text, link, elementsInPrimaryTab, mapTextsInPrimaryTab, textsInPrimaryTab, primaryNav);
		}
	}
	
	public void addToSecondaryTab(String id, String image, String text, String link) {
		if (!elementsInPrimaryTab.containsKey(id) && !elementsInSecondaryTab.containsKey(id)) {
			addToTab(id, image, text, link, elementsInSecondaryTab, mapTextsInSecondaryTab, textsInSecondaryTab, secondaryNav);
		}
	}
	
	public void removeFromTabs(String id) {
		if (elementsInPrimaryTab.containsKey(id)) {
			primaryNav.removeChild(elementsInPrimaryTab.get(id));
			elementsInPrimaryTab.remove(id);
			textsInPrimaryTab.remove(mapTextsInPrimaryTab.get(id));
			mapTextsInPrimaryTab.remove(id);
		} else if (elementsInSecondaryTab.containsKey(id)) {
			secondaryNav.removeChild(elementsInSecondaryTab.get(id));
			elementsInSecondaryTab.remove(id);
			textsInSecondaryTab.remove(mapTextsInSecondaryTab.get(id));
			mapTextsInSecondaryTab.remove(id);
		}
	}
	
	private void addToTab(String id, String image, String text, String link, HashMap<String, Element> map, 
			HashMap<String, String> mapTexts, ArrayList<String> texts, Element nav) {
		// Find position
		texts.add(text);
		Collections.sort(texts);
		int index = texts.indexOf(text);
		// Create element
		Element e = createElement(image, text, link);
		// Insert element
		if (index >= nav.getChildCount()) {
			nav.appendChild(e);
		} else {
			Node after = nav.getChild(index);
			nav.insertBefore(e, after);
		}
		// Add to map
		mapTexts.put(id, text);
		map.put(id, e);
	}
	
	private Element createElement(String image, String text, String link) {
		Element e = DOM.createElement("li");
		Element l = DOM.createAnchor();
		l.setAttribute("href", link);
		if (image != null) {
			Element i = DOM.createImg();
			i.setAttribute("src", image);
			l.appendChild(i);
		}
		Element lb = DOM.createLabel();
		lb.setInnerText(text);
		l.appendChild(lb);
		e.appendChild(l);
		return e;
	}

	public void deselectAll() {
		if (currentActiveElementId != null) {
			Element e = elementsInPrimaryTab.get(currentActiveElementId);
			if (e == null) {
				e = elementsInSecondaryTab.get(currentActiveElementId);
			}
			if (e != null) {
				e.removeClassName("active");
			}
			currentActiveElementId = null;
		}
	}
	
	public void select(String id) {
		deselectAll();
		Element e = elementsInPrimaryTab.get(id);
		if (e == null) {
			e = elementsInSecondaryTab.get(id);
		}
		if (e != null) {
			e.addClassName("active");
		}
		currentActiveElementId = id;
	}
}
