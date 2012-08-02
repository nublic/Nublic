
from dbus_in_other_thread import call_without_return

def send_notification(app, user, level, text):
    return call_without_return('com.nublic.notification',
                               '/com/nublic/notification/Messages',
                               'com.nublic.notification',
                               lambda i: i.new_message(app, user, level, text))
