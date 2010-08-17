

import dbus
 
bus = dbus.SystemBus()
valueService = bus.get_object('com.scamall.resource', '/com/scamall/resource/app/key')
value = valueService.get_dbus_method('value', 'com.scamall.resource')
print value('database')

