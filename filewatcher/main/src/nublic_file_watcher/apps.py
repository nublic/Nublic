'''
Created on 05/12/2011

@author: Alejandro Serrano Mena
@copyright: 2011 Nublic
'''

import os
import os.path
import json
from dbus_signals import *

NUBLIC_APP_DATA_ROOT = "/var/lib/nublic/apps"
FILE_WATCHER_DIRS = "/var/nublic/cache/filewatcher.dirs"

def load_app_config():
    if os.path.exists(FILE_WATCHER_DIRS):
        fp = open(FILE_WATCHER_DIRS, 'r')
        o = json.load(fp)
        fp.close()
        return o
    else:
        r = dict()
        r[u'apps'] = dict()
        return r

def write_app_config(config):
    fp = open(FILE_WATCHER_DIRS, 'w')
    o = json.dump(config, fp)
    fp.close()
    return o

def create_initial_signalers(config, apps):
    signalers = []
    for app in apps:
        if app in config[u'apps']:
            signalers.append(DbusSignaler(app.title(), config[u'apps'][app]))
        else:
            the_app = apps[app]
            if the_app.supports_filewatcher():
                fw_folders = the_app.filewatcher.paths
                if u'__all__' in fw_folders:
                    # Add watcher upon everything
                    config[u'apps'][app] = [ u'' ]
                    signalers.append(DbusSignaler(app.title(), [ u'' ]))
                else:
                    # Add a watcher with no contexts
                    config[u'apps'][app] = []
                    signalers.append(DbusSignaler(app.title(), []))
    write_app_config(config)
    return signalers

def load_all_apps():
    r = dict()
    for (dirpath, _, filenames) in os.walk(NUBLIC_APP_DATA_ROOT):
        for filename in filenames:
            if filename.endswith(".json"):
                json_name = os.path.join(dirpath, filename)
                app = load_app_json(json_name)
                r[app.app_id] = app
    return r

def load_app_json(filename):
    fp = open(filename, 'r')
    o = json.load(fp)
    fp.close()
    k = as_app_data(o)
    return k

class AppData():
    def __init__(self, app_id, name, developer, dark_icon, light_icon, color_icon, web, filewatcher):
        self.app_id = app_id
        self.name = name
        self.developer = developer
        self.dark_icon = dark_icon
        self.light_icon = light_icon
        self.color_icon = color_icon
        self.web = web
        self.filewatcher = filewatcher
    
    def supports_filewatcher(self):
        return (not (self.filewatcher is None)) and self.filewatcher.supported 

def as_app_data(d):
    app_id = d[u'id']
    name = as_app_name(d[u'name'])
    developer = d[u'developer']
    color_icon = d[u'color_icon']
    dark_icon = d[u'dark_icon']
    if dark_icon is None:
        dark_icon = color_icon
    light_icon = d[u'light_icon']
    if light_icon is None:
        light_icon = color_icon
    if u'web' in d:
        web = as_app_web_info(d[u'web'])
    else:
        web = None
    if u'filewatcher' in d:
        filewatcher = as_app_filewatcher_info(d[u'filewatcher'])
    else:
        filewatcher = None
    return AppData(app_id, name, developer, dark_icon, light_icon, color_icon, web, filewatcher)

class AppName():
    def __init__(self, default, localized):
        self.default = default
        self.localized = localized

def as_app_name(d):
    default = d[u'default']
    localized = d[u'localized']
    return AppName(default, localized)

class AppWebInfo():
    def __init__(self, path):
        self.path = path

def as_app_web_info(d):
    path = d[u'path']
    return AppWebInfo(path)

class AppFilewatcherInfo():
    def __init__(self, supported, paths):
        self.supported = supported
        self.paths = paths

def as_app_filewatcher_info(d):
    supported = d[u'supported']
    paths = d[u'paths']
    return AppFilewatcherInfo(supported, paths)
