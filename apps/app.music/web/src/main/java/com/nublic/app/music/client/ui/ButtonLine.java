package com.nublic.app.music.client.ui;

import java.util.EnumSet;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.music.client.Constants;
import com.nublic.app.music.client.datamodel.handlers.AddAtEndButtonHandler;
import com.nublic.app.music.client.datamodel.handlers.DeleteButtonHandler;
import com.nublic.app.music.client.datamodel.handlers.EditButtonHandler;
import com.nublic.app.music.client.datamodel.handlers.PlayButtonHandler;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.event.dom.client.ClickEvent;

public class ButtonLine extends Composite {
	private static ButtonLineUiBinder uiBinder = GWT.create(ButtonLineUiBinder.class);
	interface ButtonLineUiBinder extends UiBinder<Widget, ButtonLine> { }

	// CSS Styles defined in the .xml file
//	interface ButtonLineStyle extends CssResource {
//		String nobackground();
//		String leftmargin();
//	}
//	@UiField ButtonLineStyle style;
	@UiField PushButton deleteButton;
	@UiField PushButton editButton;
	@UiField PushButton addAtEndButton;
	@UiField PushButton playButton;
	DeleteButtonHandler deleteHandler;
	EditButtonHandler editHandler;
	AddAtEndButtonHandler addAtEndHandler;
	PlayButtonHandler playHandler;
	
	
	public ButtonLine(EnumSet<ButtonLineParam> params, EnumSet<ButtonType> paramTypes) {
		if (!params.isEmpty()) {
			initWidget(uiBinder.createAndBindUi(this));
			
			deleteButton.setVisible(params.contains(ButtonLineParam.DELETE));
			editButton.setVisible(params.contains(ButtonLineParam.EDIT));
			addAtEndButton.setVisible(params.contains(ButtonLineParam.ADD_AT_END));
			playButton.setVisible(params.contains(ButtonLineParam.PLAY));
			
			for (ButtonType blpt : paramTypes) {
				switch (blpt.getParam()) {
				case DELETE:
					deleteButton.setTitle(Constants.tooltip(blpt));
					break;
				case PLAY:
					playButton.setTitle(Constants.tooltip(blpt));
					break;
				case EDIT:
					editButton.setTitle(Constants.tooltip(blpt));
					break;
				}
			}
		}
	}
	
	// Handlers for buttons' clicks
	public void setDeleteButtonHandler(DeleteButtonHandler deleteButtonHandler) {
		deleteHandler = deleteButtonHandler;
	}

	public void setEditButtonHandler(EditButtonHandler editButtonHandler) {
		editHandler = editButtonHandler;
	}
	
	public void setAddAtEndButtonHandler(AddAtEndButtonHandler addAtEndButtonHandler) {
		addAtEndHandler = addAtEndButtonHandler;
	}
	
	public void setPlayButtonHandler(PlayButtonHandler playButtonHandler) {
		playHandler = playButtonHandler;
	}

	@UiHandler("deleteButton")
	void onDeleteButtonClick(ClickEvent event) {
		deleteHandler.onDelete();
	}
	@UiHandler("editButton")
	void onEditButtonClick(ClickEvent event) {
		editHandler.onEdit();
	}
	@UiHandler("addAtEndButton")
	void onAddAtEndButtonClick(ClickEvent event) {
		addAtEndHandler.onAddAtEnd();
	}
	@UiHandler("playButton")
	void onPlayButtonClick(ClickEvent event) {
		playHandler.onPlay();
	}
}
