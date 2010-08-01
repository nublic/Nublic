/**
 * 
 */
package com.scamall.app.example;

import com.scamall.base.VaadinAppSession;
import com.vaadin.ui.Component;

/**
 * Example app session implementation for testing and reference.
 * 
 * @author Alejandro Serrano
 */
public class ExampleAppSession extends VaadinAppSession<ExampleApp> {

	ExampleApp app;
	String user;

	public ExampleAppSession(ExampleApp app, String user) {
		this.app = app;
		this.user = user;
	}

	/**
	 * @see com.scamall.base.AppSession#getApp()
	 */
	@Override
	public ExampleApp getApp() {
		return this.app;
	}

	/**
	 * @see com.scamall.base.AppSession#getUser()
	 */
	@Override
	public String getUser() {
		return this.user;
	}

	@Override
	public Component getMainComponent() {
		return new ExampleMainComponent();
	}

	@Override
	public Component getSettingsComponent() {
		return null;
	}

	@Override
	public Component getGlobalBarComponent() {
		return null;
	}

}
