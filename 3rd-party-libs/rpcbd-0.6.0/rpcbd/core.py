# jsonrpc.py

# Copyright (c) 2010 Rasjid Wilcox
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in
# all copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
# THE SOFTWARE.

'''
This has the low level jsonrpc serialisation and deserialisation libraries,
and rpc objects,
and is not tied to any particular implementation.'''

STX = '\x02' # ascii 'start of text'
ETX = '\x03' # ascii 'end of text'
EOT = '\x04' # ascii 'end of transmission'
MAX_BIN_SIZE = 1024*1024
MAX_MESSAGE = MAX_BIN_SIZE + 2048   # limit to 1MB blocks + a little extra for the jsonrpc header

import types
import logging
import string
import re

from base import NullHandler, Register

# logging setup
h = NullHandler()
log = logging.getLogger('rpcbd.jsonrpc')
log.addHandler(h)


class RpcError(Exception):
    'Exeption class received from or to be sent to the remote peer. Defined as per JsonRpc spec'
    def __init__(self, code, message, data = None, req_id = None):
        self.code = code
        self.message = message
        self.data = data
        self.req_id = req_id
    def __repr__(self):
        return '%s(%s, %s, data=%s, req_id=%s)' % (self.__class__.__name__, self.code, repr(self.message), repr(self.data), self.req_id)
    def __str__(self):
        return repr(self)
    
# specific code errors
        
class RpcParseError(RpcError):
    code = -32700
    def __init__(self, message = 'Parse Error', data = None, req_id = None):
        RpcError.__init__(self, self.__class__.code, message, data, req_id)
    
class RpcInvalidError(RpcError):
    code = -32600
    def __init__(self, message = 'Invalid Rpc Packet', data = None, req_id = None):
        RpcError.__init__(self, self.__class__.code, message, data, req_id)

class RpcMethodNotFoundError(RpcError):
    code = -32601
    def __init__(self, message = 'Method Not Found', data = None, req_id = None):
        RpcError.__init__(self, self.__class__.code, message, data, req_id)

class RpcInvalidParamsError(RpcError):
    code = -32602
    def __init__(self, message = 'Invalid Params', data = None, req_id = None):
        RpcError.__init__(self, self.__class__.code, message, data, req_id)

class RpcInternalError(RpcError):
    code = -32603
    def __init__(self, message = 'Internal Error', data = None, req_id = None):
        RpcError.__init__(self, self.__class__.code, message, data, req_id)
        

SPECIFIC_ERRORS = [RpcParseError, RpcInvalidError, RpcMethodNotFoundError, 
                   RpcInvalidParamsError, RpcInternalError ]

# end specific code errors
        
class RpcServerError(RpcError):
    def __init__(self, code, message = 'Server Error', data = None, req_id = None):
        RpcError.__init__(self, code, message, data)
    
class TransportError(Exception):
    'Errors in the transport of the rpc call.'
    pass
        
class RpcObject(object):
    pass


class Request(RpcObject):
    def __init__(self, method, params, req_id):
        self.method = method
        self.params = params
        self.req_id = req_id
    def __repr__(self):
        return '<Request %s: %s(%s)>' % (self.req_id, self.method, self.params)
        

class Notification(RpcObject):
    def __init__(self, method, params):
        self.method = method
        self.params = params
    def __repr__(self):
        return '<Notification: %s(%s)>' % (self.method, self.params)

class Response(RpcObject):
    def __init__(self, result, req_id):
        self.result = result
        self.req_id = req_id
    def __repr__(self):
        return '<Response to %s: %s>' % (self.req_id, self.result)
        

class JsonRpcProcessor(object):
    'Base class for JsonRpc processors'
    def __init__(self, jsonlibrary = None):
        '''Initialise the JsonRpc processor.
           Setting jsonlibrary allow the default json serialisation library to be changed.
           Any alternative has to have the same interface as simplejson, or at least supoort 'dumps' and 'loads'
        '''
        if jsonlibrary is None:
            try:
                import json
            except ImportError:
                import simplejson as json
            self.jsonlibrary = json
        else:
            self.jsonlibrary = jsonlibrary
    def to_json(self, rpc_object):
        raise NotImplementedError
    def from_json(self, jsonstring):
        raise NotImplementedError

class JsonRpcV1(JsonRpcProcessor):
    def to_json(self, rpc_object):
        'Returns the json string for the rpc object passed in'
        dumps = self.jsonlibrary.dumps
        if isinstance(rpc_object, Request) or isinstance(rpc_object, Notification):
            if type(rpc_object.params) not in (types.TupleType, types.ListType):
                raise TypeError('params must be list or tuple for jsonrpc v1')
            if isinstance(rpc_object, Notification):
                output = '{"method": %s, "params": %s, "id": null}' \
                       % (dumps(rpc_object.method), dumps(rpc_object.params))
            else:
                output = '{"method": %s, "params": %s, "id": %s}' \
                       % (dumps(rpc_object.method), dumps(rpc_object.params),
                          dumps(rpc_object.req_id))
        elif isinstance(rpc_object, Response):
            output = '{"result": %s, "error": null, "id": %s}' \
                   % (dumps(rpc_object.result), dumps(rpc_object.req_id))
        elif isinstance(rpc_object, RpcError):
            if rpc_object.req_id is None:
                output = '{"result": null, "error": %s, "id": null)' \
                       % dumps(rpc_object)
            else:
                output = '{"result": null, "error": %s, "id": %s)' \
                       % (dumps(rpc_object), dumps(rpc_object.req_id))
        else:
            raise ValueError('Unrecognised rpc object')
        return output
    def from_json(self, jsonstring):
        loads = self.jsonlibrary.loads
        json_data = loads(jsonstring)
        if not isinstance(json_data, dict):
            raise RpcInvalidError(data = 'Rpc packet must be a json object')
        props = json_data.keys() 
        if set(props) == set(['method', 'params', 'id']) or set(props) == set(['method', 'id']):
            if 'params' not in props:
                params = ()
            else:
                params = json_data['params']
            req_id = json_data['id']
            method = json_data['method']
            if req_id is None:
                result = Notification(method, params)
            else:
                result = Request(method, params, req_id)
        elif set(props) == set(['result', 'error', 'id']):
            if json_data['error'] is None:
                # response
                result = Response(json_data['result'], json_data['id'])
            elif json_data['result'] is None:
                # error
                error_obj = json_data['error']
                if isinstance(error_obj, dict) and set(['code', 'message']).issubset(set(error_obj.keys())):
                    # v2 style error
                    data = error_obj.get('data')
                    result = RpcError(error_obj['code'], error_obj['message'], data)
                else:
                    result = RpcError(-1, 'Rpc Error', json_data['error'], json_data['id'])
            else:
                raise RpcInvalidError(data = 'Invalid response. One of "result" or "error" must be null.')
        else:
            raise RpcInvalidError(data = 'Not a recognised rpc packet.')
        return result
    def convert_params(self, params):
        'returns a (args, kwargw) tuple'
        args = ()
        kwargs = {}
        if isinstance(params, (tuple, list)):
            args = tuple(params)
        else:
            raise TypeError('params must be tuple or list')
        return (args, kwargs)
    
    
class JsonRpcV2(JsonRpcProcessor):    
    def to_json(self, rpc_object):
        dumps = self.jsonlibrary.dumps
        if isinstance(rpc_object, Request) or isinstance(rpc_object, Notification):
            if type(rpc_object.params) not in (types.TupleType, types.ListType, types.DictionaryType):
                raise TypeError('params must be list, tuple or dict for jsonrpc v2')
            if isinstance(rpc_object, Notification):
                output = '{"jsonrpc": "2.0", "method": %s, "params": %s}' \
                       % (dumps(rpc_object.method), dumps(rpc_object.params))
            else:
                output = '{"jsonrpc": "2.0", "method": %s, "params": %s, "id": %s}' \
                       % (dumps(rpc_object.method), dumps(rpc_object.params),
                          dumps(rpc_object.req_id))
        elif isinstance(rpc_object, Response):
            output = '{"jsonrpc": "2.0", "result": %s, "id": %s}' \
                   % (dumps(rpc_object.result), dumps(rpc_object.req_id))
        elif isinstance(rpc_object, RpcError):
            if rpc_object.data is None:
                jsonerr = '{"code" : %s, "message": %s}' % (dumps(rpc_object.code), dumps(rpc_object.message))
            else:
                jsonerr = '{"code" : %s, "message": %s, "data": %s}' % (dumps(rpc_object.code), dumps(rpc_object.message), dumps(rpc_object.data))
            if rpc_object.req_id is None:
                output = '{"jsonrpc": "2.0", "error": %s, "id": null}' % jsonerr
            else:
                output = '{"jsonrpc": "2.0", "error": %s, "id": %s}' \
                       % (jsonerr, dumps(rpc_object.req_id))
        else:
            raise ValueError('Unrecognised rpc object')
        return output
    def from_json(self, jsonstring):
        loads = self.jsonlibrary.loads
        json_data = loads(jsonstring)
        if isinstance(json_data, dict):
            return self._from_json(json_data)
        if isinstance(json_data, list):
            return [ self._from_json(item) for item in json_data ]
        raise RpcInvalidError(data = 'Not a recognised rpc packet.')
    def _from_json(self, json_data):
        if not isinstance(json_data, dict):
            raise RpcInvalidError(data = 'Rpc packet must be a json object')
        props = json_data.keys()
        rpcversion = str(json_data.get('jsonrpc'))
        if rpcversion != '2.0':
            raise RpcInvalidError(data = '"jsonrpc" must be "2.0"')
        if set(props).issubset(set(['jsonrpc', 'method', 'params', 'id'])) and set(['jsonrpc', 'method']).issubset(set(props)):
            if 'params' not in props:
                params = ()
            else:
                params = json_data['params']
                if isinstance(params, dict):
                    params = dictkeyclean(params)
            method = json_data['method']
            req_id = json_data.get('id')
            if req_id is None:
                result = Notification(method, params)
            else:
                result = Request(method, params, req_id)
        elif set(props) == set(['jsonrpc', 'result', 'id']):
            result = Response(json_data['result'], json_data['id'])
        elif set(props) == set(['jsonrpc', 'error', 'id']):
            error_obj = json_data['error']
            if isinstance(error_obj, dict) and set(['code', 'message']).issubset(set(error_obj.keys())) \
               and set(error_obj.keys()).issubset(set(['code', 'message', 'data'])):
                data = error_obj.get('data')
                errcode, message, req_id = error_obj['code'], error_obj['message'], json_data['id']
                result = None
                for errorcls in SPECIFIC_ERRORS:
                    if errorcls.code == errcode:
                        result = errorcls(message = message, data = data, req_id = req_id)
                        break
                if result is None:
                    result = RpcError(errcode, message, data, req_id)
            else:
                raise RpcInvalidError(data = 'Not a valid error object')
        else:
            raise RpcInvalidError(data = 'Not a recognised rpc packet.')
        return result
    def convert_params(self, params):
        'returns a (args, kwargw) tuple'
        args = ()
        kwargs = {}
        if isinstance(params, (tuple, list)):
            args = tuple(params)
        elif isinstance(params, dict):
            kwargs = params
        else:
            raise TypeError('params must be tuple, list or dict')
        return (args, kwargs)

        
def dictkeyclean(d):
    """Convert all keys of the dict 'd' to (ascii-)strings.

    :Raises: UnicodeEncodeError
    """
    new_d = {}
    for (k, v) in d.iteritems():
        new_d[str(k)] = v
    return new_d

class JsonItem(object):
    def __init__(self, raw_data):
        self.raw_data = raw_data    

class JsonObject(JsonItem):
    def __init__(self, raw_data):
        JsonItem.__init__(self, raw_data)
    def __repr__(self):
        return 'JsonObject(%r)' % self.raw_data
    def __str__(self):
        return self.raw_data
    
class JsonArrayElement(JsonItem):
    def __init__(self, array_obj, raw_data, final):
        JsonItem.__init__(self, raw_data)
        assert isinstance(array_obj, JsonArray)
        self.array_obj = array_obj
        self.array_obj.elements.append(self)
        self.final = final
    def __repr__(self):
        return '<JsonArrayElement: %s>' % self.raw_data
    def __str__(self):
        return self.raw_data

class JsonArray(object):
    def __init__(self):
        self.elements = Register()
        self.complete = False
    def create_element(self, raw_data):
        return JsonArrayElement(self, raw_data)
    def __repr__(self):
        return '<JsonArray(%s)>' % str(self)
    def __str__(self):
        return '[' + ','.join([ str(element) for element in self.elements.to_tuple()]) + ']'
    
class JsonResidual(object):
    def __init__(self, raw_data):
        self.raw_data = raw_data
    def __repr__(self):
        return 'JsonResidual(%r)' % self.raw_data
    def __str__(self):
        return self.raw_data

class JsonSplitter(object):
    '''Keep the old API, but use the new splitter below to do the real spliting work'''
    def __init__(self):
        self._new_splitter = MessageSplitter()
    @property
    def residual(self):
        return self._new_splitter.residual
    def add_data(self, data):
        results = []
        for (json_string, binary) in self._new_splitter.add_data(data):
            old_splitter = _JsonSplitter()  
            new_results = old_splitter.add_data(json_string) # note: we lose any residual - there *should* be none
            residual = old_splitter.residual.strip()
            if residual != '':
                raise RpcParseError(data = 'Invalid data: %s' % residual)
            results.extend(new_results)
        return results
        
class _JsonSplitter(object):
    def __init__(self):
        self._chunks = ['']
        self._offset = 0
        self._mode = ''   # blank = start, [ for array and { for object
        self._depth = 0
        self._obj_depth = 0  # used in array mode
        self._in_string = False
        self._skip_next = False  # just seen a \ in a string
        self._array_obj = None
        self._results = []
    @property
    def residual(self):
        return ''.join(self._chunks)
    def _got_object(self):
        strresult = (''.join(self._chunks[0:-1] + [self._chunks[-1][0:self._offset+1]])).strip()
        log.debug('Got object: %s' % strresult)
        result = JsonObject(strresult)
        self._results.append(result)
        self._process_residual()
    def _got_element(self, final):
        strresult = (''.join(self._chunks[0:-1] + [self._chunks[-1][0:self._offset]])).strip()
        log.debug('Got element: %s' % strresult)
        result = JsonArrayElement(self._array_obj, strresult, final)
        self._results.append(result)
        self._process_residual()
    def _process_residual(self):
        residual = (self._chunks[-1][self._offset+1:]).strip()
        ##print 'Got residual', residual
        if residual:
            self._chunks = [residual]
        else:
            self._chunks = ['']
        self._offset = -1
    def add_data(self, data):
        # return list of objects, either JsonObject, or a JsonArrayElement
        self._chunks.append(data)
        self._offset = 0
        while True:
            if self._offset < 0:
                self._offset = 0
            if self._offset >= len(self._chunks[-1]):
                break
            if self._skip_next:
                self._skip_next = False
                self._offset += 1
                continue
            char = self._chunks[-1][self._offset]
            if self._in_string:
                # if we are in a string, just worry about \ and "
                if char == '\\':
                    self._skip_next = True
                elif char == '"':
                    self._in_string = False
            else:
                if self._mode == '':
                    # initial search
                    if char in '{[':
                        pre = ''.join(self._chunks[0:-1] + [self._chunks[-1][0:self._offset]])
                        ##print 'pre is: %s' % pre
                        if pre.strip() != '':
                            raise RpcParseError(data = 'Invalid data: %s' % pre)
                        self._mode = char
                        self._depth = 1
                        if char == '[':
                            self._array_obj = JsonArray()
                            self._chunks[-1] = self._chunks[-1][self._offset+1:]  # skip initial '[' in elements
                            self._offset = -1
                        elif char == '{':
                            self._chunks[-1] = self._chunks[-1][self._offset:]
                            self.offset = 0
                        else:
                            raise RpcParseError(data = 'Internal error')
                elif self._mode == '{':
                    if char == '{':
                        self._depth += 1
                    elif char == '}':
                        self._depth -= 1
                        if self._depth == 0:
                            self._got_object()
                            self._mode = ''
                    elif char == '"':
                        self._in_string = True
                elif self._mode == '[':
                    if char == '[':
                        self._depth += 1
                    elif char == ']':
                        self._depth -= 1
                        if self._depth == 0:
                            self._got_element(final=True)
                            self._mode = ''
                            self._array_obj.complete = True
                            self._array_obj = None
                            self._obj_depth = 0
                    elif char == '"':
                        self._in_string = True
                    elif char == '{':
                        self._obj_depth += 1
                    elif char == '}':
                        self._obj_depth -= 1
                        if self._obj_depth < 0:
                            self._obj_depth = 0
                    elif char == ',' and self._depth == 1 and self._obj_depth == 0:
                        self._got_element(final=False)
            ##print char, self._offset, self._depth, self._obj_depth
            self._offset += 1
        output = self._results
        self._results = []
        return output


    
class MessageSplitter(object):
    def __init__(self):
        self._data = ''
        self._stx_pos = None
        self._etx_pos = None
        self._bin_size = None
    @property
    def residual(self):
        return self._data
    def _process_eot(self, eot_offset):
        if self._stx_pos is not None:
            json_msg = self._data[:self._stx_pos]
            binary = self._data[self._etx_pos + 1:eot_offset]
        else:
            json_msg = self._data[:eot_offset]
            binary = None            
        
        self._data = self._data[eot_offset + 1:]
        self._stx_pos = self._etx_pos = self._bin_size = None
        
        return (json_msg, binary)
    def _get_next(self):
        'Returns a (json_string, binary_data) tuple if there is a complete message, or None otherwise'
        if len(self._data) == 0:
            # no data
            return None
        
        # search for STX if we have not found it yet
        if self._stx_pos is None:
            try:
                self._stx_pos = self._data.index(STX)
            except ValueError:
                pass
            
        # if we have a EOT before the STX, then we have more than one message
        # and should process the first one and return
        try:
            eot_pos = self._data.index(EOT)
            if eot_pos < self._stx_pos:
                self._stx_pos = None
                return self._process_eot(eot_pos)
        except ValueError:
            pass
        
        # search for ETX if we have found STX but not ETX yet
        if self._stx_pos is not None and self._etx_pos is None:
            try:
                self._etx_pos = self._data.index(ETX)
            except ValueError:
                pass
        if self._stx_pos is not None and self._etx_pos is not None and self._bin_size is None:
            if self._etx_pos <= self._stx_pos:
                raise ValueError('ETX found before STX')
            
            # create bin length string
            bin_length_str = self._data[self._stx_pos+1:self._etx_pos]
            try:
                self._bin_size = long(bin_length_str)
            except ValueError:
                raise ValueError('Unable to parse binary length')
            if self._bin_size > MAX_BIN_SIZE:
                raise ValueError('Binary length exceeds maximum of %s bytes' % MAX_BIN_SIZE)
        if self._bin_size is not None:
            # binary mode
            wanted_eot_pos = self._etx_pos + self._bin_size + 1
            if len(self._data) <= wanted_eot_pos:
                # not enough data yet, so just return
                return None
            else:
                if self._data[wanted_eot_pos] != EOT:
                    raise ValueError('Invalid message - EOT not found at end of binary data')
                else:
                    return self._process_eot(wanted_eot_pos)
        if self._stx_pos is None:
            # check for EOT - if found then no binary and just process as is
            try:
                eot_pos = self._data.index(EOT)
                return self._process_eot(eot_pos)
            except ValueError:
                pass
        return None
        
    def add_data(self, data):
        '''yields (json_string, binary_data) tuples, where binary_data = None if there was no such data
        
        WARNING: never add more data during iteration'''
        
        self._data += data
        #result = []
        while True:
            item = self._get_next()
            if item is not None:
                #result.append(item)
                yield item
            else:
                break
        # check here if message too long
        if len(self._data) > MAX_MESSAGE:
            raise ValueError('Message too long')
        return #result
