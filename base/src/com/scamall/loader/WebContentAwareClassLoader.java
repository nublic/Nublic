/**
 * 
 */
package com.scamall.loader;

import java.net.URL;

import org.xeustechnologies.jcl.JarClassLoader;

/**
 * @author Alejandro Serrano
 *
 */
public class WebContentAwareClassLoader extends JarClassLoader {
	@Override
	public URL getResource(String name) {
		URL resource = super.getResource(name);
		if (resource == null) {
			resource = super.getResource("WebContent/" + name);
		}
		return resource;
	}
}
