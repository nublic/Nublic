#!/usr/bin/python
'''
Created on 10/08/2010

It works for usage during the install of applications.

@author: David Navarro Estruch
'''

from optparse import OptionParser

from scamall.resource.select_provider import SelectProvider

def main():
    usage = "usage: \n" + \
            "\t%prog request app_id key_name resource [[-o optional_arg]]\n" + \
            "\t%prog release app_id key_name [[-o optional_arg]]\n" + \
            "\t%prog value   app_id key_name/subkey \n" + \
            "\n" + \
            "Optional args:\n" + \
            "\t-o --optional_arg: Provides a optional_arg"
    parser = OptionParser(usage)
    parser.add_option("-o", "--optional-args", action="append", type="string", dest="optional")
    (options, args) = parser.parse_args()
    
    if len(args) == 0:
        parser.error("Invalid order")

    if args[0] == 'request':
        if len(args) != 4:
            parser.error("Invalid number of parameters")
        app = args[1]
        key = args[2]
        resource_id = args[3]
        provider = SelectProvider().generate_provider(resource_id)
        if provider == None:
            parser.error("Invalid resource")
        provider.request(app, key)        
    elif args[0] == 'release':
        if len(args) != 3:
            parser.error("Invalid number of parameters")
        app = args[1]
        key = args[2]
        provider = SelectProvider().get_provider(app, key)
        if provider == None:
            parser.error("Invalid resource")
        provider.release(app, key)
    elif args[0] == 'value':
        if len(args) != 3:
            parser.error("Invalid number of parameters")
        app = args[1]
        key_subkey = args[2]
        key, separator, subkey = key_subkey.partition("/")
        provider = SelectProvider().get_provider(app, key)
        if separator != "/": # Doesn't have a subkey value
            print(provider.value(app, key, None))
        else:
            print(provider.value(app, key, subkey))
    else:
        parser.error("Invalid order")

if __name__ == '__main__':
    main()