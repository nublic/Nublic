package com.nublic.app.init.client.ui.finished;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class LinkBox extends Composite {
	private static LinkBoxUiBinder uiBinder = GWT.create(LinkBoxUiBinder.class);
	interface LinkBoxUiBinder extends UiBinder<Widget, LinkBox> { }

	@UiField Label title;
	@UiField Image image;
	
	public LinkBox() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public void setTitle(String titleStr) {
		title.setText(titleStr);
	}
	
	public void setImage(ImageResource res) {
		image.setResource(res);
	}

	
//	@UiFactory
//	public LinkBox makeLinkBox(String title, ImageResource image) {
//		LinkBox lb = new LinkBox();
//		lb.setLabel(title);
//		lb.setImage(image);
//		return lb;
//	}
	

}
