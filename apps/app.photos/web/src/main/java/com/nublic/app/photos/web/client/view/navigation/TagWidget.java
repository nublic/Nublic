package com.nublic.app.photos.web.client.view.navigation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;

public class TagWidget extends Composite {
	private static TagWidgetUiBinder uiBinder = GWT.create(TagWidgetUiBinder.class);
	interface TagWidgetUiBinder extends UiBinder<Widget, TagWidget> { }

	// CSS Styles defined in the .xml file
	interface TagStyle extends CssResource {
		String innerImage();
	}

	long id;
	@UiField TagStyle style;
	@UiField InlineHyperlink anchor;

	public TagWidget(String text, long id) {
		initWidget(uiBinder.createAndBindUi(this));

		this.id = id;
		anchor.setText(text);
		anchor.setTargetHistoryToken("album=" + id);
	}
	
	public void select(boolean setSelected) {
		Element li = getLiElement();
		if (setSelected) {
			li.addClassName("active");
		} else {
			li.removeClassName("active");
		}
	}
	
	private Element getLiElement() {
//		return this.getElement().getFirstChildElement().getFirstChildElement();
		// <li> is child of <ul>, child of two <div>)
		return this.getElement().getFirstChildElement().getFirstChildElement();
	}
	
	public long getAlbumId() {
		return this.id;
	}

}
