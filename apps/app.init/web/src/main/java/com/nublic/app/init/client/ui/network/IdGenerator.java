package com.nublic.app.init.client.ui.network;

import com.google.gwt.user.client.DOM;

public class IdGenerator {
	String seed1;
	String seed2;
	
	public IdGenerator() {
		seed1 = DOM.createUniqueId();
		seed2 = DOM.createUniqueId();
	}

	public String topId() { return seed1; }
	public String innerId() { return seed2; }
}
