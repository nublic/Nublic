package com.nublic.app.manager.web.welcome;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class AppCell extends Composite {

	private static AppCellUiBinder uiBinder = GWT.create(AppCellUiBinder.class);
	@UiField Image image;
	@UiField Label name;
	@UiField Image star;
	
	private static String FAV_SELECTED = "images/fav-selected.png";
	private static String FAV_NOT_SELECTED = "images/fav-not-selected.png";

	interface AppCellUiBinder extends UiBinder<Widget, AppCell> {
	}

	public AppCell() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public AppCell(String image, String name, boolean favourite) {
		this();
		this.image.setUrl(image);
		this.name.setText(name);
	}
	
	public void setFavourite(boolean favourite) {
		String image = favourite ? FAV_SELECTED : FAV_NOT_SELECTED;
		this.star.setUrl(image);
	}

}
