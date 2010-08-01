/**
 * 
 */
package com.scamall.app.example;

import com.scamall.base.AppSession;

/**
 * Example app session implementation for testing and reference.
 * 
 * @author Alejandro Serrano 
 */
public class ExampleAppSession extends AppSession<ExampleApp> {

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

}
