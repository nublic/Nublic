/**
 * 
 */
package com.scamall;

/**
 * The root of all Scamall permissions that compliment the ones shipped with the
 * Java Development Kit.
 * 
 * @author Alejandro Serrano
 * 
 */
public abstract class Permission extends java.security.Permission {

	/**
	 * The serial ID neccessary for serialization.
	 */
	private static final long serialVersionUID = -4977842885098807135L;

	public Permission(String name) {
		super(name);
	}
}
