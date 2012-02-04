package com.nublic.app.music.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;
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
	interface ButtonLineStyle extends CssResource {
		String nobackground();
		String leftmargin();
		String semitransparent();
		String transparent();
	}
	@UiField ButtonLineStyle style;
	@UiField PushButton deleteButton;
	@UiField PushButton editButton;
	@UiField PushButton addAtEndButton;
	@UiField PushButton playButton;
	Widget parentWidget;
	boolean delete;
	boolean edit;
	boolean addAtEnd;
	boolean play;
	DeleteButtonHandler deleteHandler;
	EditButtonHandler editHandler;
	AddAtEndButtonHandler addAtEndHandler;
	PlayButtonHandler playHandler;
	
	
	public ButtonLine(boolean delete, boolean edit, boolean addAtEnd, boolean play, Widget parent) {
		if (delete || edit || addAtEnd || play) {
			initWidget(uiBinder.createAndBindUi(this));
			
			parentWidget = parent;
			this.delete = delete;
			this.edit = edit;
			this.addAtEnd = addAtEnd;
			this.play = play;
			
			deleteButton.setVisible(delete);
			editButton.setVisible(edit);
			addAtEndButton.setVisible(addAtEnd);
			playButton.setVisible(play);
			
			addMouseOverHandler();
		}
	}
	
	// For handling mouse in and out efects over push buttons
	private void addMouseOverHandler() {
		TransparentMouseEventHandler mouseHandler = new TransparentMouseEventHandler();
		parentWidget.addDomHandler(mouseHandler, MouseOverEvent.getType());
		parentWidget.addDomHandler(mouseHandler, MouseOutEvent.getType());
		
		if (delete)	new SemitransparentMouseEventHandler(deleteButton);
		if (edit) new SemitransparentMouseEventHandler(editButton);
		if (addAtEnd) new SemitransparentMouseEventHandler(addAtEndButton);
		if (play) new SemitransparentMouseEventHandler(playButton);
	}
	
	public class TransparentMouseEventHandler implements MouseOverHandler, MouseOutHandler {
		public void onMouseOver(final MouseOverEvent moe) {
			if (delete) deleteButton.getElement().removeClassName(style.transparent());
			if (edit) editButton.getElement().removeClassName(style.transparent());
			if (addAtEnd) addAtEndButton.getElement().removeClassName(style.transparent());
			if (play) playButton.getElement().removeClassName(style.transparent());
		}
		public void onMouseOut(final MouseOutEvent moe) {
			if (delete) deleteButton.getElement().addClassName(style.transparent());
			if (edit) editButton.getElement().addClassName(style.transparent());
			if (addAtEnd) addAtEndButton.getElement().addClassName(style.transparent());
			if (play) playButton.getElement().addClassName(style.transparent());
		}
	}
	
	public class SemitransparentMouseEventHandler implements MouseOverHandler, MouseOutHandler {
		PushButton internalButton;
		
		public SemitransparentMouseEventHandler(PushButton b) {
			internalButton = b;
			internalButton.addDomHandler(this, MouseOverEvent.getType());
			internalButton.addDomHandler(this, MouseOutEvent.getType());
		}

		public void onMouseOver(final MouseOverEvent moe) {
			internalButton.getElement().removeClassName(style.semitransparent());
		}
		public void onMouseOut(final MouseOutEvent moe) {
			internalButton.getElement().addClassName(style.semitransparent());
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
