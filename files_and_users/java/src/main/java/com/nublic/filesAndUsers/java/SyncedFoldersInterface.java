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

@DBusInterfaceName("com.nublic.files")
interface SyncedFoldersInterface extends DBusInterface {
	public boolean synced_folder_exists(int mid);
	public String get_all_synced_folders();
	public String get_synced_folder_name(int mid);
	public String get_synced_folder_owner(int mid);
	public int create_synced_folder(String name, String owner);
	public void change_synced_folder_name(int mid, String name);
	public void delete_synced_folder(int mid, boolean remove_in_fs);
	
	public static class synced_folder_created extends DBusSignal {
		String object_path;
		int id;
		String name;
		String owner;

		public synced_folder_created(String path, int id, String name, String owner) throws DBusException {
			super(path, id, name, owner);
			this.object_path = path;
			this.name = name;
			this.owner = owner;
		}
		
		public String getObjectPath() {
			return object_path;
		}
		
		public int getId() {
			return id;
		}

		public String getName() {
			return name;
		}
		
		public String getOwner() {
			return owner;
		}
	}
	
	public static class synced_folder_deleted extends DBusSignal {
		String object_path;
		int id;
		String name;

		public synced_folder_deleted(String path, int id, String name) throws DBusException {
			super(path, id, name);
			this.object_path = path;
			this.name = name;
		}
		
		public String getObjectPath() {
			return object_path;
		}
		
		public int getId() {
			return id;
		}

		public String getName() {
			return name;
		}
	}
}
