package com.nublic.app.example.web.client;

import com.google.gwt.core.client.JavaScriptObject;

public class Greeting extends JavaScriptObject {

	// Overlay types always have protected, zero argument constructors.
	protected Greeting() {
	}

	// JSNI methods to get stock data.
	public final native String getGreeting() /*-{
		return this.greeting;
	}-*/;
}
