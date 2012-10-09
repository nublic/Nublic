package com.nublic.app.init.client.ui;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.HasText;

public abstract class RealChangeHandler implements KeyUpHandler {
	HasText source;
	String lastString = "";
	
	public RealChangeHandler(HasText source) {
		this.source = source;
	}
	
	public abstract void onRealChange(String newText);
	
	@Override
	public void onKeyUp(KeyUpEvent event) {
		String text = source.getText();
		if (text.compareTo(lastString) != 0) {
			lastString = text;
			onRealChange(text);
		}
	}
}
