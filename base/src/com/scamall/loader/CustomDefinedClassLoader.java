/**
 * 
 */
package com.scamall.loader;

import java.io.InputStream;

import org.xeustechnologies.jcl.ProxyClassLoader;

/**
 * Allows JCL to use any class loader apart from the usually defined.
 * 
 * @author Alejandro Serrano
 */
public class CustomDefinedClassLoader extends ProxyClassLoader {

	/**
	 * The custom class loader that will be used.
	 */
	ClassLoader loader;

	/**
	 * Use it as the first loader if possible.
	 */
	protected int order = 0;

	public CustomDefinedClassLoader(ClassLoader loader) {
		this.loader = loader;
	}

	/**
	 * @see org.xeustechnologies.jcl.ProxyClassLoader#loadClass(java.lang.String,
	 *      boolean)
	 */
	@Override
	public Class<?> loadClass(String className, boolean resolveIt) {
		try {
			return loader.loadClass(className);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	/**
	 * @see org.xeustechnologies.jcl.ProxyClassLoader#loadResource(java.lang.String)
	 */
	@Override
	public InputStream loadResource(String name) {
		return loader.getResourceAsStream(name);
	}

}
