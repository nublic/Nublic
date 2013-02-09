
import dbus
import dbus.mainloop.glib


def call_expecting_return(bus_name, object_path, dbus_iface, m):
    #q = Queue()
    #p = Process(target=_internal_call_expecting_return, args=(q,bus_name,object_path,dbus_iface,m,))
    #p.start()
    #return q.get()
    return _internal_call_expecting_return(None, bus_name, object_path, dbus_iface, m)


def _internal_call_expecting_return(queue, bus_name, object_path, dbus_iface, m):
    bus = dbus.SystemBus()
    o = bus.get_object(bus_name, object_path)
    iface = dbus.Interface(o, dbus_interface=dbus_iface)
    #queue.put(m(iface))
    return m(iface)


def call_without_return(bus_name, object_path, dbus_iface, m):
    #p = Process(target=_internal_call_not_expecting_return, args=(bus_name,object_path,dbus_iface,m,))
    #p.start()
    _internal_call_not_expecting_return(bus_name, object_path, dbus_iface, m)


def _internal_call_not_expecting_return(bus_name, object_path, dbus_iface, m):
    bus = dbus.SystemBus()
    o = bus.get_object(bus_name, object_path)
    iface = dbus.Interface(o, dbus_interface=dbus_iface)
    m(iface)
