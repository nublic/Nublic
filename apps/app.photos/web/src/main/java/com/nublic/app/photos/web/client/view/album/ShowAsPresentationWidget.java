package com.nublic.app.photos.web.client.view.album;

import java.util.EnumSet;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.photos.web.client.model.AlbumInfo;
import com.nublic.app.photos.web.client.model.AlbumOrder;
import com.nublic.app.photos.web.client.model.CallbackOneAlbum;
import com.nublic.app.photos.web.client.model.CallbackOnePhoto;
import com.nublic.app.photos.web.client.model.CallbackRowCount;
import com.nublic.app.photos.web.client.model.PhotoInfo;
import com.nublic.app.photos.web.client.model.PhotosModel;
import com.nublic.util.gwt.LocationUtil;
import com.nublic.util.widgets.AnchorPanel;
import com.nublic.util.widgets.MessagePopup;
import com.nublic.util.widgets.PopupButton;

public class ShowAsPresentationWidget extends Composite implements ResizeHandler {

	private static ShowAsPresentationWidgetUiBinder uiBinder = GWT.create(ShowAsPresentationWidgetUiBinder.class);

	interface ShowAsPresentationWidgetUiBinder extends UiBinder<Widget, ShowAsPresentationWidget> {
	}
	
	@UiField HorizontalPanel titlePanel;
	@UiField Label titleLabel;
	@UiField AnchorPanel centralContainer;
	@UiField Image centralImage;
	@UiField Label photoTitleLabel;
	@UiField Label photoDateLabel;
	
	@UiField HorizontalPanel prevPanel;
	@UiField AnchorPanel prevLink1;
	@UiField AnchorPanel prevLink2;
	@UiField Image prevImage;
	
	@UiField HorizontalPanel nextPanel;
	@UiField AnchorPanel nextLink1;
	@UiField AnchorPanel nextLink2;
	@UiField Image nextImage;
	
	public String initialContainerStyle;
	public int LEFT_SPACE = 230;
	public int RIGHT_SPACE = 10;
	public int TOP_SPACE = 45;
	public int BOTTOM_SPACE = 95;

	long id;
	long position = -1;
	long rowCount = -1;
	
	boolean initialized = false;
	Object initLock = new Object();

	public ShowAsPresentationWidget(long id, AlbumOrder order) {
		initWidget(uiBinder.createAndBindUi(this));
		
		// Set dark background
		RootPanel.get().addStyleName("darkBackground");
		
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
		
		// Get initial album information
		PhotosModel.get().startNewAlbum(id, order);
		PhotosModel.get().rowCount(new CallbackRowCount() {
			
			@Override
			public void rowCount(AlbumInfo info, long rc) {
				rowCount = rc;
				synchronized(initLock) {
					initialized = true;
					if (position != -1) {
						_setPosition();
					}
				}
			}
			
			@Override
			public void error() {
				// Do nothing
			}
		});
		
		// Schedule resizing
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				initialContainerStyle = centralContainer.getElement().getAttribute("style");
				Window.addResizeHandler(ShowAsPresentationWidget.this);
				onResize(null);
			}
		});
	}


	@Override
	public void onResize(ResizeEvent e) {
		int height = Window.getClientHeight() - TOP_SPACE - BOTTOM_SPACE;
		int width = Window.getClientWidth() - LEFT_SPACE - RIGHT_SPACE;
		centralImage.getElement().setAttribute("style", "max-height: " + height + "px; max-width: " + width + "px;");
		centralContainer.getElement().setAttribute("style", initialContainerStyle + " line-height: " + height + "px;");
	}
	
	public void setPosition(long newPosition) {
		synchronized(initLock) {
			this.position = newPosition;
			if (initialized) {
				_setPosition();
			}
		}
	}
	
	private void _setPosition() {
		if (position != -1) {
			PhotosModel.get().photo(position, new CallbackOnePhoto() {
				
				@Override
				public void list(AlbumInfo info, PhotoInfo photo) {
					// Set inner image
					String imageUrl = LocationUtil.encodeURL(GWT.getHostPageBaseURL() + "server/view/" + photo.getId() + ".png");
					centralImage.setUrl(imageUrl);
					photoTitleLabel.setText(photo.getTitle());
					DateTimeFormat formatter = DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_FULL);
					photoDateLabel.setText("Taken on " + formatter.format(photo.getDate()));
					// Set prev and next buttons
					String nextTarget = "album=" + info.getId() + "&view=presentation&photo=" +
							(position < rowCount - 1 ? position + 1 : rowCount - 1);
					String prevTarget = "album=" + info.getId() + "&view=presentation&photo=" +
							(position > 0 ? position - 1 : 0);
					centralContainer.setHref("#" + nextTarget);
					nextLink1.setHref("#" + nextTarget);
					nextLink2.setHref("#" + nextTarget);
					prevLink1.setHref("#" + prevTarget);
					prevLink2.setHref("#" + prevTarget);
					// Show and hide elements
					if (position < rowCount - 1) {
						nextPanel.setVisible(true);
						PhotosModel.get().photo(position + 1, new CallbackOnePhoto() {
							@Override
							public void list(AlbumInfo info, PhotoInfo photo) {
								String imageUrl = LocationUtil.encodeURL(GWT.getHostPageBaseURL() + "server/view/" + photo.getId() + ".png");
								nextImage.setUrl(imageUrl);
							}
							@Override
							public void error() {
								// Do nothing
							}
						});
					} else {
						nextPanel.setVisible(false);
					}
					if (position > 0) {
						prevPanel.setVisible(true);
						PhotosModel.get().photo(position - 1, new CallbackOnePhoto() {
							@Override
							public void list(AlbumInfo info, PhotoInfo photo) {
								String imageUrl = LocationUtil.encodeURL(GWT.getHostPageBaseURL() + "server/view/" + photo.getId() + ".png");
								prevImage.setUrl(imageUrl);
							}
							@Override
							public void error() {
								// Do nothing
							}
						});
					} else {
						prevPanel.setVisible(false);
					}
				}
				
				@Override
				public void error() {
					MessagePopup popup = new MessagePopup("Error loading photo", 
							"Something strange happened while loading the photo", 
							EnumSet.of(PopupButton.OK));
					popup.center();
				}
			});
		}
	}

}
