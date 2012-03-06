package com.nublic.app.music.client.ui;

import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.music.client.Constants;

public class NavigationPanel extends Composite {
	private static NavigationPanelUiBinder uiBinder = GWT.create(NavigationPanelUiBinder.class);
	interface NavigationPanelUiBinder extends UiBinder<Widget, NavigationPanel> { }

	Element allMusic;
	HashMap<String, Element> collections = new HashMap<String, Element>();
	HashMap<String, Element> playlists = new HashMap<String, Element>();
	Element activeElement;
	
	public NavigationPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.addAttachHandler(new Handler() {
			@Override
			public void onAttachOrDetach(AttachEvent event) {
				if (event.isAttached()) {
					addAllMusic();
					addCollection("Hola", "caracola");
					addCollection("Adios", "portazo");
					addPlaylist(Constants.CURRENT_PLAYLIST_NAME, Constants.CURRENT_PLAYLIST_ID);
					activeElement = allMusic;
					selectAllMusic();
				}
			}
		});
	}
	
	public void addAllMusic() {
		InlineHyperlink a = new InlineHyperlink("All music", "");
		Element e = addElement(a, "Library");
		allMusic = e;
	}
	
	public void addCollection(String name, String id) {
		InlineHyperlink a = new InlineHyperlink(name, Constants.PARAM_COLLECTION + "=" + id);
		Element e = addElement(a, Constants.PARAM_COLLECTION);
		collections.put(id, e);
	}
	
	public void addPlaylist(String name, String id) {
		InlineHyperlink a = new InlineHyperlink(name, Constants.PARAM_PLAYLIST + "=" + id);
		Element e = addElement(a, Constants.PARAM_PLAYLIST);
		playlists.put(id, e);
	}
	
	public Element addElement(InlineHyperlink a, String parentId) {
		Element li = DOM.createElement("li");
		li.appendChild(a.getElement());
		Element parent = DOM.getElementById(parentId);
		parent.appendChild(li);
		return li;
	}
	
	public void selectAllMusic() {
		select(allMusic);
	}
	
	public void selectCollection(String id) {
		select(collections.get(id));
	}
	
	public void selectPlaylist(String id) {
		select(playlists.get(id));
	}
	
	public void select(Element e) {
		unselect(activeElement);
		activeElement = e;
		e.addClassName("active");
	}
	
	public void unselect(Element e) {
		e.removeClassName("active");
	}

}
