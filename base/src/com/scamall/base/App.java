/**
 * 
 */
package com.scamall.base;

import com.scamall.notification.Notification;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * The parent class of all Scamall apps.
 * It must be subclasses when creating a new app.
 * 
 * @author Alejandro Serrano
 */
public abstract class App {

	/**
	 * Gets the app's internal ID.
	 * @return The ID.
	 */
	public abstract String getId();
	
	/**
	 * Get the app's information.
	 * @return The information.
	 */
	public abstract AppInfo getInfo();
	
	/**
	 * Creates a new app session.
	 * @param user User for which the app is creating a session.
	 * @return The new session.
	 */
	public abstract AppSession<? extends App> newSession(String user);
	
	
	/**
	 * Sends a global notification belonging to this app.
	 * @param n Notification to send.
	 * @throws NotImplementedException
	 */
	public void sendNotification(Notification n) throws NotImplementedException {
		throw new NotImplementedException();
	}
	
	/**
	 * Sends a notification from this app to a specific user.
	 * @param user User that will receive the notification.
	 * @param n Notification to send.
	 * @throws NotImplementedException
	 */
	public void sendNotification(String user, Notification n) throws NotImplementedException {
		throw new NotImplementedException();
	}
}
