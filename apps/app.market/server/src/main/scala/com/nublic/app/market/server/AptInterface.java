// Copyright (c) 2012, Nublic

/** 
 * @author Alejandro Serrano Mena
 */
package com.nublic.app.market.server;

import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.DBusInterfaceName;
import org.freedesktop.dbus.DBusSignal;
import org.freedesktop.dbus.exceptions.DBusException;

@DBusInterfaceName("com.nublic.apt")
interface AptInterface extends DBusInterface {
    public boolean is_package_installed(String pkg);
    public boolean install_package(String pkg);
    public boolean remove_package(String pkg);
    public boolean update_cache();
    public boolean upgrade_system();
}
