package com.nublic.app.init.client.ui.network;

import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.util.widgets.CheckFeedback;
import com.nublic.util.widgets.Feedback;

public class AccordionWidget extends Composite implements HasWidgets, HasText {
	private static AccordionWidgetUiBinder uiBinder = GWT.create(AccordionWidgetUiBinder.class);
	interface AccordionWidgetUiBinder extends UiBinder<Widget, AccordionWidget> { }

	@UiField IdGenerator idGenerator;
	@UiField HTMLPanel container;
	@UiField Label title;
	@UiField CheckFeedback feedback;

	public AccordionWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		
		feedback.setFeedback(Feedback.CHECK);
	}
	
	@Override
	public String getText() { return title.getText(); }

	@Override
	public void setText(String text) { title.setText(text); }

	// To implement HasWidgets and so receive inner code
	@Override
	public void add(Widget w) {	container.add(w); }
	
	@Override
	public void clear() { container.clear(); }
	
	@Override
	public Iterator<Widget> iterator() { return container.iterator(); }
	
	@Override
	public boolean remove(Widget w) { return container.remove(w); }

}
