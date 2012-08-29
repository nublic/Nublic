package com.nublic.app.photos.web.client.view.album;

import java.util.HashSet;
import java.util.Map;
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
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.photos.common.model.CallbackListOfAlbums;
import com.nublic.app.photos.common.model.CallbackOneAlbum;
import com.nublic.app.photos.common.model.PhotosModel;
import com.nublic.app.photos.web.client.Constants;
import com.nublic.app.photos.web.client.controller.PhotosController;

public class ShowAllAlbumsWidget extends Composite implements ScrollHandler, ResizeHandler {

	private static ShowAllAlbumsWidgetUiBinder uiBinder = GWT.create(ShowAllAlbumsWidgetUiBinder.class);

	interface ShowAllAlbumsWidgetUiBinder extends UiBinder<Widget, ShowAllAlbumsWidget> {
	}
	
	@UiField HorizontalPanel titlePanel;
	@UiField Label titleLabel;
	@UiField FlowPanel mainPanel;
	
	PhotosController controller;
	Set<AlbumThumbnailWidget> unloadedWidgets = new HashSet<AlbumThumbnailWidget>();

	public ShowAllAlbumsWidget(PhotosController c) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.controller = c;
		
		// Set white background
		RootPanel.get().removeStyleName("darkBackground");
		
		// Add "new album" button
		NewAlbumWidget newWidget = new NewAlbumWidget(c);
		mainPanel.add(newWidget);
		
		// Set window title
		controller.changeTitle(Constants.I18N.allAlbums());
		
		PhotosModel.get().albums(new CallbackListOfAlbums() {
			
			@Override
			public void list(Map<Long, String> albums) {
				for (Map.Entry<Long, String> album : albums.entrySet()) {
					addNewAlbum(album.getKey(), album.getValue());
				}
				
				mainPanel.addDomHandler(ShowAllAlbumsWidget.this, ScrollEvent.getType());
				Window.addResizeHandler(ShowAllAlbumsWidget.this);
				
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					
					@Override
					public void execute() {
						onScroll(null);
					}
				});
			}
			
			@Override
			public void error() {
				// Do nothing
			}
		});
		
		PhotosModel.get().addAlbumAddedHandler(new CallbackOneAlbum() {
			
			@Override
			public void list(long id, String name) {
				addNewAlbum(id, name);
				onScroll(null);
			}
			
			@Override
			public void error() {
				// Do nothing
			}
		});
	}
	
	public void addNewAlbum(long key, String value) {
		AlbumThumbnailWidget toInsert = new AlbumThumbnailWidget(controller, key, value);
		
		// Find place to insert the album
		int index = -1;
		for (int i = 0; i < mainPanel.getWidgetCount(); i++) {
			if (mainPanel.getWidget(i) instanceof AlbumThumbnailWidget) {
				AlbumThumbnailWidget w = (AlbumThumbnailWidget)mainPanel.getWidget(i);
				if (toInsert.getName().compareToIgnoreCase(w.getName()) < 0) {
					index = i;
					break;
				}
			}
		}
		// Now insert
		if (index == -1) {
			mainPanel.add(toInsert);
		} else {
			mainPanel.insert(toInsert, index);
		}
		
		unloadedWidgets.add(toInsert);
	}
	
	@Override
	public void onScroll(ScrollEvent event) {
		Set<Widget> loadedInThisScroll = new HashSet<Widget>();
		int panelTop = mainPanel.getAbsoluteTop();
		int panelBottom = panelTop + mainPanel.getOffsetHeight();
		for (AlbumThumbnailWidget aw : unloadedWidgets) {
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

	@Override
	public void onResize(ResizeEvent r) {
		onScroll(null);
	}

}
