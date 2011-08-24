package com.scamall.app.image;

import com.vaadin.ui.Window;

public class ImageWindow extends Window {

	private static final long serialVersionUID = -2169129660809339024L;

	public ImageWindow() {
		// TODO Auto-generated constructor stub
	}
	private SingleImageView view;
	
	public ImageWindow(String name) {
		super(name);
	}
	
	public SingleImageView getView() {
		return view;
	}
	
	public void setView(SingleImageView view) {
		this.view = view;
	}
}
