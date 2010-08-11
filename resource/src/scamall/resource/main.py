'''
Created on 10/08/2010

@author: David Navarro Estruch
'''

from optparse import OptionParser

def main():
    usage = "usage: \n" + \
            "\t%prog request app_id key_name resource_id [[-o optional_arg]]\n" + \
            "\t%prog release app_id key_name [[-o optional_arg]]\n" + \
            "\t%prog value   app_id key_name/subkey \n" + \
            "\n\n" + \
            "Optional args:\n" + \
            "\t-o --optional_arg: Provides a optional_arg"
    parser = OptionParser(usage)
    parser.add_option("-o", "--optional-args", action="append", type="string", dest="optional")
    (options, args) = parser.parse_args()
    
    if args[0] == 'request':
        print("Perform request") # @todo
    elif args[0] == 'release':
        print("Perform release") # @todo
    elif args[0] == 'value':
        print("Perform value")   # @todo
    else:
        parser.error("Invalid order")



if __name__ == '__main__':
    main()