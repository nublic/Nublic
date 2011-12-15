package com.nublic.filesAndUsers.java;

import org.freedesktop.dbus.DBusConnection;
import org.freedesktop.dbus.exceptions.DBusException;

class Singletons {
	static UsersInterface users = null;
	static MirrorsInterface mirrors = null;
	static SyncedFoldersInterface synced = null;
	
	private static DBusConnection conn;
	
	private static String UsersBusName = "com.nublic.users";
	private static String FilesBusName = "com.nublic.files";
	
	private static String UsersObjectPath = "/com/nublic/Users";
	private static String MirrorsObjectPath = "/com/nublic/Mirrors";
	private static String SyncedObjectPath = "/com/nublic/SyncedFolders";
	
	static DBusConnection getConnection() throws DBusException {
		if (conn == null) {
			conn = DBusConnection.getConnection(DBusConnection.SYSTEM);
		}
		return conn;
	}
	
	static UsersInterface getUsers() throws DBusException {
		if (users == null) {
			users = (UsersInterface) getConnection().getRemoteObject(UsersBusName, UsersObjectPath, UsersInterface.class);
		}
		return users;
	}
	
	static MirrorsInterface getMirrors() throws DBusException {
		if (mirrors == null) {
			mirrors = (MirrorsInterface) getConnection().getRemoteObject(FilesBusName, MirrorsObjectPath, MirrorsInterface.class);
		}
		return mirrors;
	}
	
	static SyncedFoldersInterface getSyncedFolders() throws DBusException {
		if (synced == null) {
			synced = (SyncedFoldersInterface) getConnection().getRemoteObject(FilesBusName, SyncedObjectPath, SyncedFoldersInterface.class);
		}
		return synced;
	}
}
