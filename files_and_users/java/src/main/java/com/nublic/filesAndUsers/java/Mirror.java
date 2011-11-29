package com.nublic.filesAndUsers.java;

import java.util.ArrayList;
import java.util.List;

import org.freedesktop.dbus.exceptions.DBusException;

public class Mirror {
	int id;
	
	public Mirror(int id) {
		this.id = id;
	}
	
	public static List<Mirror> getAll() throws FileQueryException {
		try {
			String[] ids = Singletons.getMirrors().get_all_mirrors().split(":");
			ArrayList<Mirror> mirrors = new ArrayList<Mirror>();
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
}
