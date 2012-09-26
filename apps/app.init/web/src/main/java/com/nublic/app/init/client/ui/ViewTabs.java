package com.nublic.app.music.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.music.client.controller.ViewKind;

public class ViewTabs extends Composite {
	private static ViewTabsUiBinder uiBinder = GWT.create(ViewTabsUiBinder.class);
	interface ViewTabsUiBinder extends UiBinder<Widget, ViewTabs> { }

	@UiField InlineHyperlink artistViewLink;
	@UiField InlineHyperlink albumViewLink;
	@UiField InlineHyperlink songViewLink;
	
	public ViewTabs() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public void setSelected(String kind) {
		setSelected(ViewKind.parse(kind));
	}
	
	public void setSelected(ViewKind k) {
		switch (k) {
		case ARTISTS:
			setArtistActive();
			break;
		case ALBUMS:
			setAlbumActive();
			break;
		case SONGS:
			setSongActive();
			break;
		}
	}
	
	public void setVisible(String s) {
		setVisible(ViewKind.parse(s));
	}
	
	public void setVisible(ViewKind k) {
		this.setVisible(true);
		
		switch (k) {
		case ARTISTS:
			artistViewLink.setVisible(true);
			break;
		case ALBUMS:
			albumViewLink.setVisible(true);
			break;
		case SONGS:
			songViewLink.setVisible(true);
			break;
		}
	}
	
	public void hideTab(ViewKind k) {
		switch (k) {
		case ARTISTS:
			artistViewLink.setVisible(false);
			break;
		case ALBUMS:
			albumViewLink.setVisible(false);
			break;
		case SONGS:
			songViewLink.setVisible(false);
			break;
		}
	}
	
	public void setTarget(ViewKind k, String target) {
		switch (k) {
		case ARTISTS:
			artistViewLink.setTargetHistoryToken(target);
			break;
		case ALBUMS:
			albumViewLink.setTargetHistoryToken(target);
			break;
		case SONGS:
			songViewLink.setTargetHistoryToken(target);
			break;
		}
	}

	public void setArtistActive() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				Element e = DOM.getElementById("artistLi");
				e.addClassName("active");
				artistViewLink.setTargetHistoryToken(History.getToken());
			}
		});
	}
	
	public void setAlbumActive() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				Element e = DOM.getElementById("albumLi");
				e.addClassName("active");
				albumViewLink.setTargetHistoryToken(History.getToken());
			}
		});
	}
	
	public void setSongActive() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				Element e = DOM.getElementById("songLi");
				e.addClassName("active");
				songViewLink.setTargetHistoryToken(History.getToken());
			}
		});
	}

}
