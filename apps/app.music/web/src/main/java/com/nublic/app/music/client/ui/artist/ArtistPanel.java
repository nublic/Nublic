package com.nublic.app.music.client.ui.artist;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.music.client.Constants;
import com.nublic.app.music.client.Utils;
import com.nublic.app.music.client.controller.Controller;
import com.nublic.app.music.client.controller.ViewKind;
import com.nublic.app.music.client.datamodel.ArtistInfo;
import com.nublic.app.music.client.datamodel.handlers.AddAtEndButtonHandler;
import com.nublic.app.music.client.datamodel.handlers.DeleteButtonHandler;
import com.nublic.app.music.client.datamodel.handlers.PlayButtonHandler;
import com.nublic.app.music.client.ui.ButtonLine;
import com.nublic.app.music.client.ui.ButtonLineParam;
import com.nublic.app.music.client.ui.TagKind;
import com.nublic.app.music.client.ui.ViewTabs;

public class ArtistPanel extends Composite implements ScrollHandler {
	private static ArtistPanelUiBinder uiBinder = GWT.create(ArtistPanelUiBinder.class);
	interface ArtistPanelUiBinder extends UiBinder<Widget, ArtistPanel> { }
	
	@UiField FlowPanel mainPanel;
	@UiField Label titleLabel;
	@UiField HorizontalPanel titlePanel;
	@UiField ViewTabs viewTabs;

	String collectionId;
	Set<ArtistWidget> unloadedWidgets = new HashSet<ArtistWidget>();
	List<ArtistInfo> artistList;

	public ArtistPanel(String collectionId) {
		initWidget(uiBinder.createAndBindUi(this));

		this.collectionId = collectionId;
		
		// Set title
		if (collectionId == null) {
			titleLabel.setText(Constants.I18N.allMusicName());
		} else {
			titleLabel.setText(Controller.INSTANCE.getModel().getTagCache().get(collectionId).getName());
		}
		
		createButtonLine();
		setViewLinks();

		// For handling lazy scroll loading of ArtistWidgets
		mainPanel.addDomHandler(this, ScrollEvent.getType());
		
		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				onScroll(null);
			}
		});
	}

	private void createButtonLine() {
		EnumSet<ButtonLineParam> buttonSet = EnumSet.of(ButtonLineParam.ADD_AT_END, ButtonLineParam.PLAY);
		if (collectionId != null) {
			buttonSet.add(ButtonLineParam.DELETE);
		}
		ButtonLine b = new ButtonLine(buttonSet);
		setDeleteButtonHandler(b);
		setAddAtEndButtonHandler(b);
		setPlayButtonHandler(b);
		titlePanel.insert(b, 1);
	}
	
	private void setViewLinks() {
		String albumTarget = Utils.getTargetHistoryToken(null, null, collectionId, ViewKind.ALBUMS.toString());
		viewTabs.setTarget(ViewKind.ALBUMS, albumTarget);
		String songTarget = Utils.getTargetHistoryToken(null, null, collectionId, ViewKind.SONGS.toString());
		viewTabs.setTarget(ViewKind.SONGS, songTarget);
		viewTabs.setSelected(ViewKind.ARTISTS);
	}

	// For handling lazy scroll loading of ArtistWidgets
	@Override
	public void onScroll(ScrollEvent event) {
		Set<ArtistWidget> loadedInThisScroll = new HashSet<ArtistWidget>();
		int panelTop = mainPanel.getAbsoluteTop();
		int panelBottom = panelTop + mainPanel.getOffsetHeight();
		for (ArtistWidget aw : unloadedWidgets) {
			int widgetTop = aw.getAbsoluteTop();
			int widgetBottom = widgetTop + aw.getOffsetHeight();
			// If widget enters a visible zone we load it
			if (widgetBottom > panelTop && widgetTop < panelBottom) {
				aw.lazyLoad();
				loadedInThisScroll.add(aw);
			}
		}
		unloadedWidgets.removeAll(loadedInThisScroll);
	}

	public void setArtistList(List<ArtistInfo> artistList) {
		this.artistList = artistList;
		
		for (ArtistInfo a : artistList) {
			ArtistWidget aw = new ArtistWidget(a, collectionId);
			unloadedWidgets.add(aw); // for handling lazy scroll loading
			mainPanel.add(aw);
		}
		// Couldn't find a way to do it on some widget event in Artist Widget (attachedHandler and LoadHandler don't work)
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				// This is executed when current events stack empties
				// Call onScroll when the widgets are loaded, so content of shown ones can be lazy loaded
				onScroll(null);
			}
		});
	}
	
	// Handlers for button line
	private void setDeleteButtonHandler(ButtonLine b) {
		b.setDeleteButtonHandler(new DeleteButtonHandler() {
			@Override
			public void onDelete() {
				Controller.INSTANCE.deleteTag(collectionId, TagKind.COLLECTION);
			}
		});
	}

	private void setAddAtEndButtonHandler(ButtonLine b) {
		b.setAddAtEndButtonHandler(new AddAtEndButtonHandler() {
			@Override
			public void onAddAtEnd() {
				Controller.INSTANCE.addAtEnd(null, null, collectionId);
			}
		});
	}

	private void setPlayButtonHandler(ButtonLine b) {
		b.setPlayButtonHandler(new PlayButtonHandler() {
			@Override
			public void onPlay() {
				Controller.INSTANCE.play(null, null, collectionId);
			}
		});
	}

}
