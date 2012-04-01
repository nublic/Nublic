package com.nublic.app.photos.web.client.view.album;

import java.util.HashSet;
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
import com.nublic.app.photos.web.client.PhotosApp;
import com.nublic.app.photos.web.client.controller.PhotosController;
import com.nublic.app.photos.web.client.model.AlbumInfo;
import com.nublic.app.photos.web.client.model.AlbumOrder;
import com.nublic.app.photos.web.client.model.CallbackOneAlbum;
import com.nublic.app.photos.web.client.model.CallbackRowCount;
import com.nublic.app.photos.web.client.model.PhotosModel;

public class ShowAsCellsWidget extends Composite implements ScrollHandler, ResizeHandler {

	private static ShowAsCellsWidgetUiBinder uiBinder = GWT.create(ShowAsCellsWidgetUiBinder.class);

	interface ShowAsCellsWidgetUiBinder extends UiBinder<Widget, ShowAsCellsWidget> {
	}
	
	@UiField HorizontalPanel titlePanel;
	@UiField Label titleLabel;
	@UiField FlowPanel mainPanel;
	
	PhotosController controller;
	long id;
	Set<ThumbnailWidget> unloadedWidgets = new HashSet<ThumbnailWidget>();

	public ShowAsCellsWidget(PhotosController c, long id, AlbumOrder order) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.controller = c;
		
		// Set white background
		RootPanel.get().removeStyleName("darkBackground");
		
		// Set title label
		this.id = id;
		if (id == -1) {
			titleLabel.setText("All photos");
		} else {
			PhotosModel.get().album(id, new CallbackOneAlbum() {
				@Override
				public void list(long id, String name) {
					titleLabel.setText(name);
				}
				@Override
				public void error() {
					titleLabel.setText("Unknown album");
				}
			});
		}
		
		// Set inner widgets
		PhotosModel.get().startNewAlbum(id, order);
		PhotosModel.get().rowCount(new CallbackRowCount() {
			
			@Override
			public void rowCount(AlbumInfo info, long rowCount) {
				for (long i = 0; i < rowCount; i++) {
					ThumbnailWidget photo = new ThumbnailWidget(controller, info, i);
					mainPanel.add(photo);
					unloadedWidgets.add(photo);
					PhotosApp.getUi().getDragController().makeDraggable(photo);
				}
				
				mainPanel.addDomHandler(ShowAsCellsWidget.this, ScrollEvent.getType());
				Window.addResizeHandler(ShowAsCellsWidget.this);
				
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
		for (ThumbnailWidget aw : unloadedWidgets) {
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
	public void onResize(ResizeEvent arg0) {
		onScroll(null);
	}
	
	public void dispose() {
		for (int i = 0; i < mainPanel.getWidgetCount(); i++) {
			PhotosApp.getUi().getDragController().makeNotDraggable(mainPanel.getWidget(i));
		}
	}

}
