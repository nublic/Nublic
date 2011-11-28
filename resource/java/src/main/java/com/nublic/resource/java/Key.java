package com.nublic.resource.java;

import org.freedesktop.dbus.DBusConnection;
import org.freedesktop.dbus.exceptions.DBusException;

public class Key {
	
	private static String BASE_OBJECT_PATH = "/com/nublic/resource";
	private static String BUS_NAME = "com.nublic.resource";

	App app;
	String name;
	ResourceInterface i;
	
	Key(App app, String key) {
		this.app = app;
		this.name = key;
		this.i = null;
	}
	
	public App getApp() {
		return this.app;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getValue(String subkey) throws Exception {
		try {
			return getDBusInterface().value(subkey);
		} catch (DBusException e) {
			throw new Exception("Error in D-Bus connection");
		}
	}
	
	ResourceInterface getDBusInterface() throws DBusException {
		if (i == null) {
			String object_path = BASE_OBJECT_PATH + "/" + app.getName() + "/" + name;
			DBusConnection conn = DBusConnection.getConnection(DBusConnection.SYSTEM);
			i = (ResourceInterface) conn.getRemoteObject(BUS_NAME, object_path, ResourceInterface.class);
		}
		return i;
	}
}
