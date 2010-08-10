/**
 * 
 */
package com.scamall.manager;

import com.scamall.loader.Loader;

/**
 * 
 * 
 * @author Alejandro Serrano
 */
public class Singleton {

	private static Loader loader = null;
	
	public static Loader getLoader() {
		if (loader == null) {
			loader = new Loader(Singleton.class.getClassLoader());
		}
		return loader;
	}
}
