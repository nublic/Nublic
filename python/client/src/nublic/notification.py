from dbus_in_other_thread import call_without_return
from rpcbd_client import rpcbd_call

def send_notification(app, user, level, text, use_dbus=False):
    if use_dbus:
        return call_without_return('com.nublic.notification',
                                   '/com/nublic/notification/Messages',
                                   'com.nublic.notification',
                                   lambda i: i.new_message(app, user, level, text))
    else:
        rpcbd_call(5441, lambda i: i.new_message(app, user, level, text))
