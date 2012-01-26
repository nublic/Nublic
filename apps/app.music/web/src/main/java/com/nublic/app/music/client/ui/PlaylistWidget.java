package com.nublic.app.music.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.nublic.app.music.client.Resources;

public class PlaylistWidget extends Composite implements HasText {
	private static PlaylistWidgetUiBinder uiBinder = GWT.create(PlaylistWidgetUiBinder.class);
	interface PlaylistWidgetUiBinder extends UiBinder<Widget, PlaylistWidget> {}

	@UiField Image playImage;
	@UiField Label nameLabel;
	boolean isBeingPlayed;

	public PlaylistWidget() {
		this("no name");
	}
	
	public PlaylistWidget(String name) {
		initWidget(uiBinder.createAndBindUi(this));
		
		nameLabel.setText(name);
		playImage.setResource(Resources.INSTANCE.playMini());
		setPlaying(false);
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
	}
	
	public boolean isPlaying() {
		return isBeingPlayed;
	}
}
