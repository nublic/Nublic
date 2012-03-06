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
import com.nublic.app.music.client.datamodel.Controller;
import com.nublic.app.music.client.datamodel.Playlist;

public class NavigationPanel extends Composite {
	private static NavigationPanelUiBinder uiBinder = GWT.create(NavigationPanelUiBinder.class);
	interface NavigationPanelUiBinder extends UiBinder<Widget, NavigationPanel> { }

	// CSS Styles defined in the .xml file
//	interface NavStyle extends CssResource {
//		String bold();
//	}
//
//	@UiField NavStyle style;
	Element allMusic;
	HashMap<String, Element> collections = new HashMap<String, Element>();
	HashMap<String, Element> playlists = new HashMap<String, Element>();
	Element activeElement;
	Element playingElement;
	
	public NavigationPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.addAttachHandler(new Handler() {
			@Override
			public void onAttachOrDetach(AttachEvent event) {
				if (event.isAttached()) {
					addAllMusic();
					createCurrentPlaylist();
					activeElement = allMusic;
					selectAllMusic();
				}
			}
		});
	}
	
	public void createCurrentPlaylist() {
		Playlist current = new Playlist(Constants.CURRENT_PLAYLIST_ID, Constants.CURRENT_PLAYLIST_NAME);
		addPlaylist(current.getName(), current.getId());
		Controller.getModel().getPlaylistCache().put(current.getId(), current);
	}
	
	// Adding methods
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
	
	// Removing methods
	public void removeCollection(String id) {
		Element elementToRemove = collections.get(id);
		if (activeElement == elementToRemove) {
			activeElement = allMusic;
		}
		elementToRemove.removeFromParent();
	}
	
	public void removePlaylist(String id) {
		Element elementToRemove = playlists.get(id);
		if (activeElement == elementToRemove) {
			activeElement = allMusic;
		}
		elementToRemove.removeFromParent();
	}
	
	// Selecting methods
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
		if (e != null) {
			e.removeClassName("active");
		}
	}
	
	// Playing methods
	public void playPlaylist(String id) {
		play(playlists.get(id));
	}
	public void pausePlaylist(String id) {
		pause(playlists.get(id));
	}
	
	public void stop() {
		stop(playingElement);
	}
	
	public void stopPlaylist(String id) {
		stop(playlists.get(id));
	}
	
	public void play(Element e) {
		if (e != playingElement) {
			stop();
			playingElement = e;
		}
//		e.addClassName(style.bold());
		e.addClassName("bold-link");
	}
	
	public void pause(Element e) {
		if (e != playingElement) {
			stop();
			playingElement = e;
		}
//		DOM.getChild(e, 0).addClassName(style.bold());
//		e.getElementsByTagName("a").getItem(0).addClassName(style.bold());
//		e.addClassName(style.bold());
		e.addClassName("bold-link");
	}
	
	public void stop(Element e) {
		if (e != null) {
			playingElement = null;
//			e.removeClassName(style.bold());
			e.removeClassName("bold-link");
		}
	}

}
