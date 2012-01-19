// Copyright (c) 2011, Nublic

/** 
 * @author David Navarro Estruch
 * @author Alejandro Serrano Mena
 */
package com.nublic.filesAndUsers.java;

import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.DBusInterfaceName;
import org.freedesktop.dbus.DBusSignal;
import org.freedesktop.dbus.exceptions.DBusException;

@DBusInterfaceName("com.nublic.users")
interface UsersInterface extends DBusInterface {
	public boolean user_exists(String username);
	public String get_all_users();
	public void create_user(String username, String password, String name);
	public void change_user_password(String username, String old_password, String new_password);
	public int get_user_uid(String username);
	public String get_user_shown_name(String username);
	public void change_user_shown_name(String username, String name);
	public void delete_user(String username);
	public void assign_file(String username, String path);
	
	public static class user_created extends DBusSignal {
		String object_path;
		String username;
		String name;

		public user_created(String path, String username, String name) throws DBusException {
			super(path, username, name);
			this.object_path = path;
			this.username = username;
			this.name = name;
		}
		
		public String getObjectPath() {
			return object_path;
		}
		
		public String getUsername() {
			return username;
		}

		public String getName() {
			return name;
		}
	}
	
	public static class user_deleted extends DBusSignal {
		String object_path;
		String username;
		String name;

		public user_deleted(String path, String username, String name) throws DBusException {
			super(path, username, name);
			this.object_path = path;
			this.username = username;
			this.name = name;
		}
		
		public String getObjectPath() {
			return object_path;
		}
		
		public String getUsername() {
			return username;
		}

		public String getName() {
			return name;
		}
	}
	
	public static class user_shown_name_changed extends DBusSignal {
		String object_path;
		String username;
		String name;

		public user_shown_name_changed(String path, String username, String name) throws DBusException {
			super(path, username, name);
			this.object_path = path;
			this.username = username;
			this.name = name;
		}
		
		public String getObjectPath() {
			return object_path;
		}
		
		public String getUsername() {
			return username;
		}

		public String getName() {
			return name;
		}
	}
}
