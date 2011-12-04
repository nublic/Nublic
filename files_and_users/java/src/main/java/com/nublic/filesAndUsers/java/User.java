package com.nublic.filesAndUsers.java;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.freedesktop.dbus.exceptions.DBusException;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

public class User {
	String username;
	Integer uid;
	
	public User(String username) {
		this.username = username;
		this.uid = null;
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
		if (this.uid == null) {
			try {
				uid = Singletons.getUsers().get_user_uid(username);
			} catch(DBusException e) {
				throw new UserQueryException();
			}
		}
		return uid;
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
	
	public boolean isOwner(String path) throws IOException {
		Path p = FileSystems.getDefault().getPath(path);
		return Files.getOwner(p).getName().equals(username);
	}
	
	public boolean canRead(Folder folder) throws FileQueryException, IOException {
		return canRead(folder.getPath());
	}
	
	public boolean canRead(File file) throws FileQueryException, IOException {
		return canRead(file.getPath());
	}
	
	public boolean canRead(String path) throws IOException {
		return true;
		
		// TODO: Take care of special for mirrors and synced folders: can read but not write
		
		/*if (isOwner(path))
			return true;
		
		Path p = FileSystems.getDefault().getPath(path);
		Set<PosixFilePermission> perms = Files.getPosixFilePermissions(p);
		return perms.contains(PosixFilePermission.GROUP_READ)
				|| perms.contains(PosixFilePermission.OTHERS_READ);*/
	}
	
	public boolean canWrite(Folder folder) throws FileQueryException, IOException {
		return canWrite(folder.getPath());
	}
	
	public boolean canWrite(File file) throws FileQueryException, IOException {
		return canWrite(file.getPath());
	}
	
	public boolean canWrite(String path) throws IOException {
		return true;
		
		/*if (isOwner(path))
			return true;
		
		Path p = FileSystems.getDefault().getPath(path);
		Set<PosixFilePermission> perms = Files.getPosixFilePermissions(p);
		return perms.contains(PosixFilePermission.GROUP_WRITE)
				|| perms.contains(PosixFilePermission.OTHERS_WRITE);*/
	}
	
	<T extends Folder> Collection<T> getOwned(Collection<T> elements) {
		return Collections2.filter(elements, new Predicate<T>() {
			public boolean apply(T m) {
				try {
					return m.getOwner().getUsername().equals(username);
				} catch (Exception e) {
					return false;
				}
			}
		});
	}
	
	<T extends Folder> Collection<T> getAccessible(Collection<T> elements) {
		return Collections2.filter(elements, new Predicate<T>() {
			public boolean apply(T m) {
				try {
					return canRead(m.getPath());
				} catch (Exception e) {
					return false;
				}
			}
		});
	}
	
	public Collection<Mirror> getOwnedMirrors() throws FileQueryException {
		return getOwned(Mirror.getAll());
	}
	
	public Collection<Mirror> getAccessibleMirrors() throws FileQueryException {
		return getAccessible(Mirror.getAll());
	}
	
	public Collection<SyncedFolder> getOwnedSyncedFolders() throws FileQueryException {
		return getOwned(SyncedFolder.getAll());
	}
	
	public Collection<SyncedFolder> getAccessibleSyncedFolders() throws FileQueryException {
		return getAccessible(SyncedFolder.getAll());
	}
}
