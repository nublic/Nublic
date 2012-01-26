package com.nublic.app.music.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.nublic.app.music.client.Resources;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.event.dom.client.ClickEvent;

public class PlaylistWidget extends Composite implements HasText {
	private static PlaylistWidgetUiBinder uiBinder = GWT.create(PlaylistWidgetUiBinder.class);
	interface PlaylistWidgetUiBinder extends UiBinder<Widget, PlaylistWidget> {}

	// CSS Styles defined in the .xml file
	interface PlaylistStyle extends CssResource {
		String selected();
		String playing();
	}

	@UiField Image playImage;
	@UiField Label nameLabel;
	@UiField PlaylistStyle style;
	boolean isBeingPlayed;
	boolean isSelected;
	
	public PlaylistWidget() {
		this("no name");
	}
	
	public @UiConstructor PlaylistWidget(String name) {
		initWidget(uiBinder.createAndBindUi(this));
		
		nameLabel.setText(name);
		playImage.setResource(Resources.INSTANCE.playMini());
		setPlaying(false);
		setSelected(false);
	}
	
	@Override
	public String getText() {
		return nameLabel.getText();
	}

	@Override
	public void setText(String text) {
		nameLabel.setText(text);		
	}

	public void setPlaying(boolean b) {
		isBeingPlayed = b;
		playImage.setVisible(b);
		if (b) {
			nameLabel.getElement().addClassName(style.playing());
		} else {
			nameLabel.getElement().removeClassName(style.playing());
		}
	}
	
	public boolean isPlaying() {
		return isBeingPlayed;
	}
	
	public void setSelected(boolean b) {
		isSelected = b;
		if (b) {
			nameLabel.getElement().addClassName(style.selected());
		} else {
			nameLabel.getElement().removeClassName(style.selected());
		}
	}
	
	public boolean isSelected() {
		return isSelected;
	}

	@UiHandler("nameLabel")
	void onNameLabelClick(ClickEvent event) {
		// TODO: define behaviour onClick
	}
}
