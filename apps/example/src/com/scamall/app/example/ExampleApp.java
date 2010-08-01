/**
 * 
 */
package com.scamall.app.example;

import com.scamall.base.App;
import com.scamall.base.AppInfo;

/**
 * Example app for testing and reference.
 * 
 * @author Alejandro Serrano
 */
public class ExampleApp extends App {

	/**
	 * @see com.scamall.base.App#getId()
	 */
	@Override
	public String getId() {
		return "example";
	}

	/**
	 * @see com.scamall.base.App#getInfo()
	 */
	@Override
	public AppInfo getInfo() {
		return new ExampleAppInfo();
	}

	/**
	 * @see com.scamall.base.App#newSession(java.lang.String)
	 */
	@Override
	protected ExampleAppSession newSession(String user) {
		return new ExampleAppSession(this, user);
	}

}
