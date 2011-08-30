// Copyright (c) 2011, Nublic

/** 
 * @author David Navarro Estruch
 */

package com.nublic.notification.java;

import org.freedesktop.dbus.DBusConnection;
import org.freedesktop.dbus.exceptions.DBusException;



public class Notification {
	/**
	*
	* @author David Navarro Estruch
	*/
	private static String ObjectPath = "/com/nublic/notification/Messages";
	private static String ServiceBusName = "com.nublic.notification";
	private static DBusConnection conn;

	public void newMessage(String app, String user, String level, String text) {
		try {
			conn = DBusConnection.getConnection(DBusConnection.SYSTEM);
			MessageConnection c = (MessageConnection) conn.getRemoteObject(ServiceBusName, ObjectPath, MessageConnection.class);
			c.new_message(app, user, level, text);
		} catch(DBusException ex) {
			ex.printStackTrace();
		}
	}
}
