package com.nublic.util.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class BootstrapProgressBar extends Composite {

	private static BootstrapProgressBarUiBinder uiBinder = GWT.create(BootstrapProgressBarUiBinder.class);

	interface BootstrapProgressBarUiBinder extends UiBinder<Widget, BootstrapProgressBar> {
	}
	
	@UiField HTMLPanel parent;
	Element progressElement;
	int progress = 0;

	public BootstrapProgressBar(final String... extraClasses) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.addAttachHandler(new Handler() {
			
			@Override
			public void onAttachOrDetach(AttachEvent event) {
				// Add extra CSS classes
				if (event.isAttached()) {
					for (String c : extraClasses) {
						parent.addStyleName(c);
					}

					// Add the progress element
					progressElement = DOM.createDiv();
					progressElement.setClassName("bar");
					parent.getElement().appendChild(progressElement);
					paintProgress();
				}
			}
		});
	}
	
	public void setProgress(int progress) {
		this.progress = progress;
		if (progressElement != null) {
			paintProgress();
		}
	}

	private void paintProgress() {
		progressElement.setAttribute("style", "width: " + String.valueOf(progress) + "%;");
	}
	
	public void addInnerStyleName(String s) {
		parent.addStyleName(s);
	}
	
	public void removeInnerStyleName(String s) {
		parent.removeStyleName(s);
	}
}
