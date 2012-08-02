
import dbus
import dbus.mainloop.glib

class App:
    def __init__(self, name):
        self.name = name
    
    def get(self, key):
        return Key(self, key)

class Key:
    def __init__(self, app, key):
        self.app = app
        self.key = key
    
    def value(self, subkey):
        dbus_loop = dbus.mainloop.glib.DBusGMainLoop()
        bus = dbus.SystemBus(mainloop = dbus_loop)
        o = bus.get_object('com.nublic.resource', '/com/nublic/resource/' + self.app.name + '/' + self.key)
        iface = dbus.Interface(o, dbus_interface='com.nublic.resource.value')
        return iface.value(subkey)