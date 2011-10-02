package com.nublic.filewatcher.scala;

import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.DBusInterfaceName;
import org.freedesktop.dbus.DBusSignal;
import org.freedesktop.dbus.exceptions.DBusException;

@DBusInterfaceName("com.nublic.filewatcher")
public interface FileWatcher extends DBusInterface {
	public static class file_changed extends DBusSignal {
		String ty;
		String pathname;
		String src_pathname;
		boolean dir;

		public file_changed(String path, String ty, String pathname, String src_pathname, boolean isdir) throws DBusException {
			super(path, ty, pathname, src_pathname, isdir);
			this.ty = ty;
			this.pathname = pathname;
			this.src_pathname = src_pathname;
			this.dir = isdir;
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
		
		public FileChange getChange() {
			return FileChange.parse(ty, pathname, src_pathname, dir);
		}
	}
}
