/**
 * 
 */
package com.scamall.manager;

/**
 * Represents an error during a manager operation
 * 
 * @author Alejandro Serrano
 */
public class ManagerException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4055361974688865395L;

	public ManagerException() {
		super();
	}
	
	public ManagerException(String message) {
		super(message);
	}
	
	public ManagerException(Throwable cause) {
		super(cause);
	}
	
	public ManagerException(String message, Throwable cause) {
		super(message, cause);
	}
}
