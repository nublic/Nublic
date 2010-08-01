/**
 * 
 */
package com.scamall.app.example;

import com.scamall.base.AppInfo;

/**
 * App information for the example Scamall app.
 * 
 * @author Alejandro Serrano
 */
public class ExampleAppInfo implements AppInfo {

	/**
	 * @see com.scamall.base.AppInfo#getName()
	 */
	@Override
	public String getName() {
		return "Greetings";
	}

}
