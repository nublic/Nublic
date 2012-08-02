
from dbus_in_other_thread import call_expecting_return

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
        return call_expecting_return('com.nublic.resource',
                                     '/com/nublic/resource/' + self.app.name + '/' + self.key,
                                     'com.nublic.resource.value',
                                     lambda i: i.value(subkey))
