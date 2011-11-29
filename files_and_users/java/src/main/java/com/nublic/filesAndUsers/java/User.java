package com.nublic.filesAndUsers.java;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.freedesktop.dbus.exceptions.DBusException;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

public class User {
	String username;
	
	public User(String username) {
		this.username = username;
	}
	
	public static List<User> getAll() throws UserQueryException {
		try {
			String[] user_names = Singletons.getUsers().get_all_users().split(":");
			ArrayList<User> users = new ArrayList<User>();
			for (String name : user_names) {
				users.add(new User(name));
			}
			return users;
		} catch(DBusException e) {
			throw new UserQueryException();
		}
	}
	
	public boolean exists() throws UserQueryException {
		try {
			return Singletons.getUsers().user_exists(username);
		} catch(DBusException e) {
			throw new UserQueryException();
		}
	}
	
	public void create(String password, String shownName) throws UserQueryException {
		try {
			Singletons.getUsers().create_user(username, password, shownName);
		} catch(DBusException e) {
			throw new UserQueryException();
		}
	}
	
	public void delete() throws UserQueryException {
		try {
			Singletons.getUsers().delete_user(username);
		} catch(DBusException e) {
			throw new UserQueryException();
		}
	}
	
	public String getUsername() {
		return username;
	}
	
	public int getUserId() throws UserQueryException {
		try {
			return Singletons.getUsers().get_user_uid(username);
		} catch(DBusException e) {
			throw new UserQueryException();
		}
	}
	
	public String getShownName() throws UserQueryException {
		try {
			return Singletons.getUsers().get_user_shown_name(username);
		} catch(DBusException e) {
			throw new UserQueryException();
		}
	}
	
	public void setShownName(String shownName) throws UserQueryException {
		try {
			Singletons.getUsers().change_user_shown_name(username, shownName);
		} catch(DBusException e) {
			throw new UserQueryException();
		}
	}
	
	public void changePassword(String old, String new_) throws UserQueryException {
		try {
			Singletons.getUsers().change_user_password(username, old, new_);
		} catch(DBusException e) {
			throw new UserQueryException();
		}
	}
	
	public Collection<Mirror> getMirrors() throws FileQueryException {
		return Collections2.filter(Mirror.getAll(), new Predicate<Mirror>() {
			public boolean apply(Mirror m) {
				try {
					return m.getOwner().getUsername().equals(username);
				} catch (FileQueryException e) {
					return false;
				}
			}
		});
	}
	
	public Collection<SyncedFolder> getSyncedFolders() throws FileQueryException {
		return Collections2.filter(SyncedFolder.getAll(), new Predicate<SyncedFolder>() {
			public boolean apply(SyncedFolder f) {
				try {
					return f.getOwner().getUsername().equals(username);
				} catch (FileQueryException e) {
					return false;
				}
			}
		});
	}
}
