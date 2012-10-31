from dbus_in_other_thread import call_expecting_return
from rpcbd_client import rpcbd_call_return

class App:
    def __init__(self, name, use_dbus=False):
        self.name = name
        self.use_dbus = use_dbus
    
    def get(self, key):
        return Key(self, key, self.use_dbus)

class Key:
    def __init__(self, app, key, use_dbus=False):
        self.app = app
        self.key = key
        self.use_dbus = use_dbus
    
    def value(self, subkey):
        if self.use_dbus:
            return call_expecting_return('com.nublic.resource',
                                         '/com/nublic/resource/' + self.app.name + '/' + self.key,
                                         'com.nublic.resource.value',
                                         lambda i: i.value(subkey))
        else:
            return rpcbd_call_return(5439, lambda i: i.value(self.app.name, self.key, subkey))

