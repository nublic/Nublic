package com.nublic.app.music.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.music.client.Constants;
import com.nublic.app.music.client.Resources;

public class TagWidget extends Composite {
	private static TagWidgetUiBinder uiBinder = GWT.create(TagWidgetUiBinder.class);
	interface TagWidgetUiBinder extends UiBinder<Widget, TagWidget> { }

	// CSS Styles defined in the .xml file
	interface TagStyle extends CssResource {
		String innerImage();
	}

	@UiField TagStyle style;
	@UiField InlineHyperlink anchor;
//	@UiField Image icon;
	
	boolean isStopped = true;
	
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
//		this(name, getTargetToken(id, k), null);
		this(name, getTargetToken(id, k));
	}
	
//	public TagWidget(TagKind k, String name, String id, Image iconImage) {
//		this(name, getTargetToken(id, k), iconImage);
//	}

//	public TagWidget(String text, String targetHistoryToken, Image iconImage) {
	public TagWidget(String text, String targetHistoryToken) {
		initWidget(uiBinder.createAndBindUi(this));
		
//		if (iconImage == null) {
//			icon.setVisible(false);
//		} else {
//			icon.setUrl(iconImage.getUrl());
//		}

		anchor.setText(text);
		anchor.setTargetHistoryToken(targetHistoryToken);
	}
	
//	public void addIconAction(ClickHandler ch) {
//		icon.addClickHandler(ch);
//	}
	
	public void select(boolean setSelected) {
		Element li = this.getElement().getFirstChildElement().getFirstChildElement();
		if (setSelected) {
			li.addClassName("active");
		} else {
			li.removeClassName("active");
		}
	}
	
	public void play() {
		stop();

		Image play = new Image(Resources.INSTANCE.playMini());
		play.addStyleName(style.innerImage());

		isStopped = false;
		// Inserting in the <a> child of <li> (child of <ul>, child of this <div>)
		Element li = this.getElement().getFirstChildElement().getFirstChildElement();
		li.getFirstChildElement().insertFirst(play.getElement());
		li.addClassName("bold-link");
	}

	public void pause() {
		stop();

		Image pause = new Image(Resources.INSTANCE.pauseMini());
		pause.addStyleName(style.innerImage());

		isStopped = false;
		// Inserting in the <a> child of <li> (child of <ul>, child of this <div>)
		Element li = this.getElement().getFirstChildElement().getFirstChildElement();
		li.getFirstChildElement().insertFirst(pause.getElement());
		li.addClassName("bold-link");
	}

	public void stop() {
		if (!isStopped) {
			isStopped = true;
			// Removing from the <a> child of <li> (child of <ul>, child of this <div>)
			Element li = this.getElement().getFirstChildElement().getFirstChildElement();
			li.getFirstChildElement().getFirstChild().removeFromParent();
			li.removeClassName("bold-link");
		}
	}

}
