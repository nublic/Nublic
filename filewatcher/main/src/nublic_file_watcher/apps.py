'''
Created on 05/12/2011

@author: Alejandro Serrano Mena
@copyright: 2011 Nublic
'''

import os
import os.path
import json

NUBLIC_APP_DATA_ROOT = "/var/lib/nublic/apps"
FILE_WATCHER_DIRS = "/var/nublic/cache/filewatcher.dirs"

def load_app_config():
    fp = open(FILE_WATCHER_DIRS, 'r')
    o = json.load(fp)
    fp.close()
    return o

def write_app_config(config):
    fp = open(FILE_WATCHER_DIRS, 'w')
    o = json.dump(config, fp)
    fp.close()
    return o

def load_all_apps():
    r = dict
    for (dirpath, _, filenames) in os.walk(NUBLIC_APP_DATA_ROOT):
        for filename in filenames:
            if filename.endswith(".json"):
                json_name = os.path.join(dirpath, filename)
                app = load_app_json(json_name)
                r[app.app_id] = app
    return r

def load_app_json(filename):
    fp = open(filename, 'r')
    o = json.load(fp, object_hook = as_app_data)
    fp.close()
    return o

class AppData():
    def __init__(self, app_id, name, developer, icon, web, filewatcher):
        self.app_id = app_id
        self.name = name
        self.developer = developer
        self.icon = icon
        self.web = web
        self.filewatcher = filewatcher
    
    def supports_filewatcher(self):
        return not self.filewatcher is None and self.filewatcher.supported 

def as_app_data(d):
    app_id = d['id']
    name = as_app_name(d['name'])
    developer = d['developer']
    icon = d['icon']
    if 'web' in d:
        web = as_app_web_info(d['web'])
    else:
        web = None
    if 'filewatcher' in d:
        filewatcher = as_app_filewatcher_info(d['filewatcher'])
    else:
        filewatcher = None
    return AppData(app_id, name, developer, icon, web, filewatcher)

class AppName():
    def __init__(self, default, localized):
        self.default = default
        self.localized = localized

def as_app_name(d):
    default = d['default']
    localized = d['localized']
    return AppName(default, localized)

class AppWebInfo():
    def __init__(self, path):
        self.path = path

def as_app_web_info(d):
    path = d['path']
    return AppWebInfo(path)

class AppFilewatcherInfo():
    def __init__(self, supported, paths):
        self.supported = supported
        self.paths = paths

def as_app_filewatcher_info(d):
    supported = d['supported']
    paths = d['paths']
    return AppFilewatcherInfo(supported, paths)
