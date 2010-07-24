/**
 * 
 */
package com.scamall.base;

import com.scamall.notification.Notification;

/**
 * The parent class of all Scamall app sessions.
 * When subclassing it, the derived class should
 * change the parametrised type to the kind of
 * app it's deriving from.
 * @author Alejandro Serrano
 *
 * @param <A> The app class.
 */
public abstract class AppSession<A extends App> {

	/**
	 * Gets the app the session belongs to.
	 * @return The associated app.
	 */
	public abstract A getApp();
	
	/**
	 * Gets the user the session was being created from.
	 * @return The associated user.
	 */
	public abstract String getUser();
	
	/**
	 * Sends a notification from this app to the user in the session.
	 * @param n Notification to send.
	 */
	public void sendNotification(Notification n) {
		this.getApp().sendNotification(this.getUser(), n);
	}
}
