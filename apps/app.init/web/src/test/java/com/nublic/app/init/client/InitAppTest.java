package com.nublic.app.init.client;

import com.google.gwt.junit.client.GWTTestCase;

/**
 * GWT JUnit tests must extend GWTTestCase.
 */
public class InitAppTest extends GWTTestCase {

	/**
	 * Must refer to a valid module that sources this class.
	 */
	public String getModuleName() {
		return "com.nublic.app.init.InitAppJUnit";
	}

	public void testNothing() {
		assertTrue(true);
	}
}
