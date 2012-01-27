package com.nublic.app.music.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.nublic.app.music.client.Constants;
import com.nublic.app.music.client.Resources;
import com.nublic.app.music.client.datamodel.Playlist;
import com.nublic.app.music.client.datamodel.Tag;
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
	String id;
	boolean isBeingPlayed;
	boolean isSelected;
	boolean isTag;
	
	public PlaylistWidget() {
		this("no name", null, false);
	}

	public @UiConstructor PlaylistWidget(String name) {
		this(name, null, false);
	}
	
	public PlaylistWidget(Playlist p) {
		this(p.getName(), p.getId(), false);
	}
	
	public PlaylistWidget(Tag t) {
		this(t.getName(), t.getId(), true);
	}
	
	public PlaylistWidget(String name, String id, boolean isTag) {
		initWidget(uiBinder.createAndBindUi(this));
	
		this.id = id;
		this.isTag = isTag;
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
		if (id == null) {
			// All music
			History.newItem("");
		} else if (isTag) {
			// Collection
			History.newItem(Constants.PARAM_COLLECTION + "=" + id);
		} else {
			// Playlist
			History.newItem(Constants.PARAM_PLAYLIST + "=" + id);
		}
	}

	public String getId() {
		return id;
	}
}
