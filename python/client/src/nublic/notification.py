
import dbus
import dbus.mainloop.glib

def send_notification(app, user, level, text):
    dbus_loop = dbus.mainloop.glib.DBusGMainLoop()
    bus = dbus.SystemBus(mainloop = dbus_loop)
    o = bus.get_object('com.nublic.notification', '/com/nublic/notification/Messages')
    iface = dbus.Interface(o, dbus_interface='com.nublic.notification')
    return iface.new_message(app, user, level, text)