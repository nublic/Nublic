// Copyright (c) 2011, Nublic

/** 
 * @author David Navarro Estruch
 * @author Alejandro Serrano Mena
 */
package com.nublic.resource.java;

import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.DBusInterfaceName;

@DBusInterfaceName("com.nublic.resource.value")
public interface ResourceInterface extends DBusInterface {
	public String value(String subkey);
}
