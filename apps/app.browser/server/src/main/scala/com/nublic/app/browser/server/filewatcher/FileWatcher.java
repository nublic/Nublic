package com.nublic.app.browser.server.filewatcher;

import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.DBusInterfaceName;
import org.freedesktop.dbus.DBusSignal;
import org.freedesktop.dbus.exceptions.DBusException;

@DBusInterfaceName("com.nublic.filewatcher")
public interface FileWatcher extends DBusInterface {
	public class file_change extends DBusSignal {
		String ty;
		String pathname;
		String src_pathname;
		boolean dir;

		public file_change(String ty, String pathname, String src_pathname, boolean is_dir) throws DBusException {
			super(ty, pathname, src_pathname, is_dir);
			this.ty = ty;
			this.pathname = pathname;
			this.src_pathname = src_pathname;
			this.dir = is_dir;
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
	}
}
