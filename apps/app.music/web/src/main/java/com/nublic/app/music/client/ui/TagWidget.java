package com.nublic.app.music.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.music.client.Constants;

public class TagWidget extends Composite {
	private static TagWidgetUiBinder uiBinder = GWT.create(TagWidgetUiBinder.class);
	interface TagWidgetUiBinder extends UiBinder<Widget, TagWidget> { }

	@UiField InlineHyperlink anchor;
	@UiField Image icon;
	
//	List<ClickHandler> iconActionList = new ArrayList<ClickHandler>();
	
	private static String getTargetToken(String id, TagKind k) {
		if (k == null) {
			return "";
		} else {
			switch (k) {
			case COLLECTION:
				return Constants.PARAM_COLLECTION + "=" + id;
			case PLAYLIST:
				return Constants.PARAM_PLAYLIST + "=" + id;
			default:
				return "";
			}
		}
	}

	public TagWidget(TagKind k, String name, String id) {
		this(name, getTargetToken(id, k), null);
	}
	
	public TagWidget(TagKind k, String name, String id, Image iconImage) {
		this(name, getTargetToken(id, k), iconImage);
	}

	public TagWidget(String text, String targetHistoryToken, Image iconImage) {
		initWidget(uiBinder.createAndBindUi(this));
		
		if (iconImage == null) {
			icon.setVisible(false);
		} else {
			icon.setUrl(iconImage.getUrl());
		}

		anchor.setText(text);
		anchor.setTargetHistoryToken(targetHistoryToken);
	}
	
	public void addIconAction(ClickHandler ch) {
		icon.addClickHandler(ch);
	}

}
