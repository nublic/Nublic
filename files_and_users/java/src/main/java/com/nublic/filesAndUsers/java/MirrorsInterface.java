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
interface MirrorsInterface extends DBusInterface {
	public boolean mirror_exists(int mid);
	public String get_all_mirrors();
	public String get_mirror_name(int mid);
	public String get_mirror_owner(int mid);
	public int create_mirror(String name, String owner);
	public void change_mirror_name(int mid, String name);
	public void delete_mirror(int mid, boolean remove_in_fs);
	
	public static class mirror_created extends DBusSignal {
		String object_path;
		int id;
		String name;
		String owner;

		public mirror_created(String path, int id, String name, String owner) throws DBusException {
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
	
	public static class mirror_deleted extends DBusSignal {
		String object_path;
		int id;
		String name;

		public mirror_deleted(String path, int id, String name) throws DBusException {
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
