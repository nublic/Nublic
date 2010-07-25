/**
 * 
 */
package com.scamall.manager;

import com.scamall.Permission;

/**
 * @author Alejandro Serrano
 * 
 */
public class ManagerPermission extends Permission {

	/**
	 * The serial ID neccessary for serialization.
	 */
	private static final long serialVersionUID = 7227799810832750534L;

	public ManagerPermission(String name) {
		super(name);
	}

	/**
	 * Checks two ManagerPermission objects for equality. Two manager
	 * permissions are equal if they have the same name.
	 * 
	 * @see java.security.Permission#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ManagerPermission))
			return false;
		return ((ManagerPermission) obj).getName().equals(this.getName());
	}

	/**
	 * Returns the canonical string representation of the actions, which
	 * currently is the empty string "", since there are no actions for a
	 * ManagerPermission.
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
	 * Checks if the specified permission is "implied" by this object. Manager
	 * permission 'all' implies any other manager permission. For any other
	 * permission, they are implies only if equal.
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
