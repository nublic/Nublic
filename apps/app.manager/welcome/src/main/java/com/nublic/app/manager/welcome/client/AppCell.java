package com.nublic.app.manager.welcome.client;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.manager.welcome.client.notifications.Notification;
import com.nublic.app.manager.welcome.client.notifications.NotificationHackManager;
import com.nublic.app.manager.welcome.client.notifications.NotificationLine;

public class AppCell extends Composite {

	private static AppCellUiBinder uiBinder = GWT.create(AppCellUiBinder.class);
	@UiField Image image;
	@UiField Anchor name;
	// @UiField Image star;
	@SuppressWarnings("unused")
	private String appId;
	// private boolean favourite;
	
	@UiField VerticalPanel notificationPanel;
	
	// private static String FAV_SELECTED = "images/fav-selected.png";
	// private static String FAV_NOT_SELECTED = "images/fav-not-selected.png";

	interface AppCellUiBinder extends UiBinder<Widget, AppCell> {
	}
	
	public AppCell(String id, String image, String name, String url) {
		initWidget(uiBinder.createAndBindUi(this));
		this.appId = id;
		this.image.setUrl(image);
		this.name.setText(name);
		this.name.setHref(url);
		// this.favourite = favourite;
		// this.setFavouriteImage();
		fillNotifications(id);
	}

	private void fillNotifications(String id) {
		ArrayList<Notification> list = NotificationHackManager.getNotificationsOf(id);
		for (Notification n : list) {
			notificationPanel.add(new NotificationLine(n));
		}
	}
	
	/*public void setFavouriteImage() {
		String image = favourite ? FAV_SELECTED : FAV_NOT_SELECTED;
		this.star.setUrl(image);
	}*/

	/*@UiHandler("star")
	void onStarClick(ClickEvent event) {
		// Change favourite state
		favourite = !favourite;
		// Tell the manager
		// theUi.setFavourite(this.appId, favourite);
		this.setFavouriteImage();
	}*/
}
