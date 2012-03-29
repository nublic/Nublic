package com.nublic.app.music.client.ui.dnd;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasMouseDownHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class Draggable extends Composite implements HasMouseDownHandlers {
	private static DraggableUiBinder uiBinder = GWT.create(DraggableUiBinder.class);
	interface DraggableUiBinder extends UiBinder<Widget, Draggable> { }
	
	int row;
	
	public Draggable(int row) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.row = row;
	}

	public int getRow() { return row; }
	public void setRow(int row) { this.row = row; }

	@Override
	public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
		return addDomHandler(handler, MouseDownEvent.getType());
	}

}
