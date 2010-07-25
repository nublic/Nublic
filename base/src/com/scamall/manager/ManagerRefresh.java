/**
 * 
 */
package com.scamall.manager;

import java.rmi.Remote;

/**
 * Provides a way in which external commands may issue a reloading of app
 * configuration.
 * 
 * @author Alejandro Serrano
 */
public interface ManagerRefresh extends Remote {

	/**
	 * Reloads app configuration, loading and unloading the apps that may have
	 * changed.
	 */
	void refresh();
}
