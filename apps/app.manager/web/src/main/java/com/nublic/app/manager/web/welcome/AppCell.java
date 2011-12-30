package com.nublic.app.manager.web.welcome;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.nublic.app.manager.web.client.ManagerUi;

public class AppCell extends Composite {

	private static AppCellUiBinder uiBinder = GWT.create(AppCellUiBinder.class);
	@UiField Image image;
	@UiField Hyperlink name;
	@UiField Image star;
	private String appId;
	private boolean favourite;
	private ManagerUi theUi;
	
	private static String FAV_SELECTED = "images/fav-selected.png";
	private static String FAV_NOT_SELECTED = "images/fav-not-selected.png";

	interface AppCellUiBinder extends UiBinder<Widget, AppCell> {
	}
	
	public AppCell(ManagerUi ui, String id, String image, String name, String token, boolean favourite) {
		initWidget(uiBinder.createAndBindUi(this));
		this.theUi = ui;
		this.appId = id;
		this.image.setUrl(image);
		this.name.setText(name);
		this.name.setTargetHistoryToken(token);
		this.favourite = favourite;
		this.setFavouriteImage();
	}
	
	public void setFavouriteImage() {
		String image = favourite ? FAV_SELECTED : FAV_NOT_SELECTED;
		this.star.setUrl(image);
	}

	@UiHandler("star")
	void onStarClick(ClickEvent event) {
		// Change favourite state
		favourite = !favourite;
		// Tell the manager
		// theUi.setFavourite(this.appId, favourite);
		this.setFavouriteImage();
	}
}
