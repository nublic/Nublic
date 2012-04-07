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
import com.nublic.app.photos.web.client.controller.PhotosController;
import com.nublic.app.photos.web.client.model.CallbackListOfAlbums;
import com.nublic.app.photos.web.client.model.PhotosModel;

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
		
		PhotosModel.get().albums(new CallbackListOfAlbums() {
			
			@Override
			public void list(Map<Long, String> albums) {
				for (Map.Entry<Long, String> album : albums.entrySet()) {
					AlbumThumbnailWidget w = new AlbumThumbnailWidget(controller, album.getKey(), album.getValue());
					mainPanel.add(w);
					unloadedWidgets.add(w);
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
