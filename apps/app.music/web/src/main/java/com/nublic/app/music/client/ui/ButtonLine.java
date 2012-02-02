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
	@UiField PushButton editButton;
	@UiField PushButton addAtEndButton;
	@UiField PushButton playButton;
	Widget parentWidget;
	
	
	public ButtonLine(boolean edit, boolean addAtEnd, boolean play, Widget parent) {
		if (edit || addAtEnd || play) {
			initWidget(uiBinder.createAndBindUi(this));
			
			parentWidget = parent;
			
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
		
		new SemitransparentMouseEventHandler(editButton);
		new SemitransparentMouseEventHandler(addAtEndButton);
		new SemitransparentMouseEventHandler(playButton);
	}
	
	public class TransparentMouseEventHandler implements MouseOverHandler, MouseOutHandler {
		public void onMouseOver(final MouseOverEvent moe) {
			editButton.getElement().removeClassName(style.transparent());
			addAtEndButton.getElement().removeClassName(style.transparent());
			playButton.getElement().removeClassName(style.transparent());
		}
		public void onMouseOut(final MouseOutEvent moe) {
			editButton.getElement().addClassName(style.transparent());
			addAtEndButton.getElement().addClassName(style.transparent());
			playButton.getElement().addClassName(style.transparent());
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

}
