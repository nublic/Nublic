/**
 * 
 */
package com.scamall.loader;

import com.scamall.Permission;

/**
 * Represents the special permission needed in a block of Java code in order to
 * use the app loader.
 * 
 * @author Alejandro Serrano
 */
public class LoaderPermission extends Permission {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7227799810832750534L;

	public LoaderPermission(String name) {
		super(name);
	}

	/**
	 * Checks two LoaderPermission objects for equality. Two loader permissions
	 * are equal if they have the same name.
	 * 
	 * @see java.security.Permission#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof LoaderPermission))
			return false;
		return ((LoaderPermission) obj).getName().equals(this.getName());
	}

	/**
	 * Returns the canonical string representation of the actions, which
	 * currently is the empty string "", since there are no actions for a
	 * LoaderPermission.
	 * 
	 * @see java.security.Permission#getActions()
	 */
	@Override
	public String getActions() {
		return "";
	}

	/**
	 * Returns the hash code value for this object.
	 * 
	 * @see java.security.Permission#hashCode()
	 */
	@Override
	public int hashCode() {
		return (int) serialVersionUID + this.getName().hashCode();
	}

	/**
	 * Checks if the specified permission is "implied" by this object. Loader
	 * permission 'all' implies any other loader permission. For any other
	 * permission, they are implied only if equal.
	 * 
	 * @param permission
	 *            The permission to check implication.
	 * @see java.security.Permission#implies(java.security.Permission)
	 */
	@Override
	public boolean implies(java.security.Permission permission) {
		// Special case: 'all' implies any other permission
		if (this.getName().equals("all"))
			return true;
		return this.equals(permission);
	}

}
