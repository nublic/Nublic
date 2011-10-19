package com.nublic.filewatcher.scala;

import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.DBusInterfaceName;
import org.freedesktop.dbus.DBusSignal;
import org.freedesktop.dbus.exceptions.DBusException;

@DBusInterfaceName("com.nublic.filewatcher")
public interface FileWatcher extends DBusInterface {
	public static class file_changed extends DBusSignal {
		String object_path;
		String ty;
		String pathname;
		String src_pathname;
		boolean dir;
		String context;

		public file_changed(String path, String ty, String pathname, String src_pathname, boolean isdir, String context) throws DBusException {
			super(path, ty, pathname, src_pathname, isdir, context);
			this.object_path = path;
			this.ty = ty;
			this.pathname = pathname;
			this.src_pathname = src_pathname;
			this.dir = isdir;
			this.context = context;
		}
		
		public String getObjectPath() {
			return object_path;
		}

		public String getType() {
			return ty;
		}

		public String getPathname() {
			return pathname;
		}

		public String getSourcePathname() {
			return src_pathname;
		}

		public boolean isDir() {
			return dir;
		}
		
		public String getContext() {
			return context;
		}
		
		public FileChange getChange() {
			return FileChange.parse(ty, pathname, src_pathname, context, dir);
		}
	}
}
