/**
 * 
 */
package com.scamall.resource;

/** An abstract class for Resource Providers.
 * 
 *  All Providers of resources must extend this class. All Providers must be thread-safe
 * @author David Navarro Estruch
 *
 */
public abstract class Provider {

	/**
	 * Gets the Providers's internal ID.
	 * 
	 * @return The ID.
	 */
	public abstract String getId();

	
	/** Request a resource.
	 * 
	 * @param app The resource owner app
	 * @param key The key that the app will use to refer the resource.
	 * @param args Any number of additional args that a Provider can ask.
	 */
	abstract public void request(String app, String key, String[] args);

	/** Gets the value from a given resource by the Provider.
	 * 
	 * @param key The key that refers to the resource.
	 * @param subkey A subkey for an specific data of the resource.
	 * @return The subkey data of the key resource.
	 */
	abstract public String value(String key, String subkey);
	
	/** Releases a resource. After a release the resource must not be used again.
	 * 
	 * @param key The key that the app use to refer the resource.
	 * @param args Any number of additional args that a Provider can ask.
	 */
	abstract void release(String key, String[] args);
	
}
