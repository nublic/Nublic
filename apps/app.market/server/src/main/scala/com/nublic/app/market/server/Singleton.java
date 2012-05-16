package com.nublic.app.market.server;

import org.freedesktop.dbus.DBusConnection;
import org.freedesktop.dbus.exceptions.DBusException;

class Singleton {
    static AptInterface iface = null;
    private static DBusConnection conn;
    private static String BusName = "com.nublic.apt";
    private static String ObjectPath = "/com/nublic/Apt";
    
    public static DBusConnection getConnection() throws DBusException {
        if (conn == null) {
            conn = DBusConnection.getConnection(DBusConnection.SYSTEM);
        }
        return conn;
    }
    
    public static AptInterface getApt() throws DBusException {
        if (iface == null) {
            iface = (AptInterface) getConnection().getRemoteObject(BusName, ObjectPath, AptInterface.class);
        }
        return iface;
    }
}
