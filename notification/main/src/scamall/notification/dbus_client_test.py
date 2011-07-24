
#!/usr/bin/python

import dbus
 
bus = dbus.SystemBus()
valueService = bus.get_object('com.scamall.notification', '/com/scamall/notification/Messages')
message_sender = valueService.get_dbus_method('new_message')
print message_sender("app3", "us1", "critical", "Mensaje")

