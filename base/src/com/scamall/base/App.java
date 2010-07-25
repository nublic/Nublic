/**
 * 
 */
package com.scamall.base;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.scamall.notification.Notification;

/**
 * The parent class of all Scamall apps. It must be subclassed when creating a
 * new app.
 * 
 * @author Alejandro Serrano
 */
public abstract class App {

	static final String APP_SESSION_PREFIX = "--scamall-app-session-";

	/**
	 * Gets the app's internal ID.
	 * 
	 * @return The ID.
	 */
	public abstract String getId();

	/**
	 * Get the app's information.
	 * 
	 * @return The information.
	 */
	public abstract AppInfo getInfo();

	/**
	 * Creates a new app session.
	 * 
	 * @param user
	 *            User for which the app is creating a session.
	 * @return The new session.
	 */
	abstract AppSession<? extends App> newSession(String user);

	/**
	 * Gets the app session associated to a HTTP request. Creates a new session
	 * if none was created before in this session.
	 * 
	 * @param request
	 *            The Servlet request that contains the session.
	 * @return The associated session, or a new one if none was created before.
	 */
	public AppSession<? extends App> getSession(HttpServletRequest request) {
		HttpSession http_session = request.getSession();
		String user = request.getRemoteUser();

		String attrib_name = APP_SESSION_PREFIX + this.getId();
		@SuppressWarnings("unchecked")
		AppSession<? extends App> app_session = (AppSession<? extends App>) http_session
				.getAttribute(attrib_name);
		if (app_session == null) {
			// Save a new session if none was created before
			app_session = this.newSession(user);
			http_session.setAttribute(attrib_name, app_session);
		}
		return app_session;
	}

	/**
	 * Sends a global notification belonging to this app.
	 * 
	 * @param n
	 *            Notification to send.
	 * @throws UnsupportedOperationException
	 */
	public void sendNotification(Notification n)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Sends a notification from this app to a specific user.
	 * 
	 * @param user
	 *            User that will receive the notification.
	 * @param n
	 *            Notification to send.
	 * @throws UnsupportedOperationException
	 */
	public void sendNotification(String user, Notification n)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}
}
