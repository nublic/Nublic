package com.nublic.filesAndUsers.java;

import java.util.ArrayList;
import java.util.List;

import org.freedesktop.dbus.exceptions.DBusException;

public class SyncedFolder extends Folder {
	static String DATA_ROOT = "/var/nublic/data/synced/";
	int id;
	
	public SyncedFolder(int id) {
		this.id = id;
	}
	
	public static List<SyncedFolder> getAll() throws FileQueryException {
		try {
			String[] ids = Singletons.getSyncedFolders().get_all_synced_folders().split(":");
			ArrayList<SyncedFolder> mirrors = new ArrayList<SyncedFolder>();
			for (String mid : ids) {
				mirrors.add(new SyncedFolder(Integer.parseInt(mid)));
			}
			return mirrors;
		} catch(DBusException e) {
			throw new FileQueryException();
		}
	}
	
	public boolean exists() throws FileQueryException {
		try {
			return Singletons.getSyncedFolders().synced_folder_exists(id);
		} catch(DBusException e) {
			throw new FileQueryException();
		}
	}
	
	public String getName() throws FileQueryException {
		try {
			return Singletons.getSyncedFolders().get_synced_folder_name(id);
		} catch(DBusException e) {
			throw new FileQueryException();
		}
	}
	
	public void setName(String name) throws FileQueryException {
		try {
			Singletons.getSyncedFolders().change_synced_folder_name(id, name);
		} catch(DBusException e) {
			throw new FileQueryException();
		}
	}
	
	public User getOwner() throws FileQueryException {
		try {
			return new User(Singletons.getSyncedFolders().get_synced_folder_owner(id));
		} catch(DBusException e) {
			throw new FileQueryException();
		}
	}
	
	public static SyncedFolder create(String name, String owner) throws FileQueryException {
		try {
			return new SyncedFolder(Singletons.getSyncedFolders().create_synced_folder(name, owner));
		} catch(DBusException e) {
			throw new FileQueryException();
		}
	}
	
	public void delete(boolean removeInFileSystem) throws FileQueryException {
		try {
			Singletons.getSyncedFolders().delete_synced_folder(id, removeInFileSystem);
		} catch(DBusException e) {
			throw new FileQueryException();
		}
	}
	
	public String getPath() {
		return DATA_ROOT + id;
	}
}
