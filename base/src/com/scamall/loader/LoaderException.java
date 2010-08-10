/**
 * 
 */
package com.scamall.loader;

/**
 * Represents an error during a loader operation.
 * 
 * @author Alejandro Serrano
 */
public class LoaderException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4055361974688865395L;

	public LoaderException() {
		super();
	}
	
	public LoaderException(String message) {
		super(message);
	}
	
	public LoaderException(Throwable cause) {
		super(cause);
	}
	
	public LoaderException(String message, Throwable cause) {
		super(message, cause);
	}
}
