package com.nublic.filesAndUsers.java;

import java.util.ArrayList;
import java.util.List;

import org.freedesktop.dbus.exceptions.DBusException;

public class Mirror extends Folder {
	static String DATA_ROOT = "/var/nublic/data/mirrors/";
	int id;
	
	public Mirror(int id) {
		this.id = id;
	}
	
	public static List<Mirror> getAll() throws FileQueryException {
		try {
			ArrayList<Mirror> mirrors = new ArrayList<Mirror>();
			String m = Singletons.getMirrors().get_all_mirrors();
			if (m.isEmpty())
				return mirrors; 
			String[] ids = m.split(":");
			for (String mid : ids) {
				mirrors.add(new Mirror(Integer.parseInt(mid)));
			}
			return mirrors;
		} catch(DBusException e) {
			throw new FileQueryException();
		}
	}
	
	public boolean exists() throws FileQueryException {
		try {
			return Singletons.getMirrors().mirror_exists(id);
		} catch(DBusException e) {
			throw new FileQueryException();
		}
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getName() throws FileQueryException {
		try {
			return Singletons.getMirrors().get_mirror_name(id);
		} catch(DBusException e) {
			throw new FileQueryException();
		}
	}
	
	public void setName(String name) throws FileQueryException {
		try {
			Singletons.getMirrors().change_mirror_name(id, name);
		} catch(DBusException e) {
			throw new FileQueryException();
		}
	}
	
	public User getOwner() throws FileQueryException {
		try {
			return new User(Singletons.getMirrors().get_mirror_owner(id));
		} catch(DBusException e) {
			throw new FileQueryException();
		}
	}
	
	public static Mirror create(String name, String owner) throws FileQueryException {
		try {
			return new Mirror(Singletons.getMirrors().create_mirror(name, owner));
		} catch(DBusException e) {
			throw new FileQueryException();
		}
	}
	
	public void delete(boolean removeInFileSystem) throws FileQueryException {
		try {
			Singletons.getMirrors().delete_mirror(id, removeInFileSystem);
		} catch(DBusException e) {
			throw new FileQueryException();
		}
	}
	
	public String getPath() {
		return DATA_ROOT + id;
	}
}
