// Copyright (c) 2011, Nublic

/** 
 * @author David Navarro Estruch
 */
package com.nublic.notification.java;

import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.DBusInterfaceName;

@DBusInterfaceName("com.nublic.notification")
public interface MessageConnection extends DBusInterface {


	public String new_message(String app, String user, String level, String text);

}
