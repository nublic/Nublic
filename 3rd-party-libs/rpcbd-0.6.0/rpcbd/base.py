# base.py

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
This is the core used to implement bi-directional jsonrpc peers, clients and servers.
'''

from __future__ import with_statement

import threading
import Queue
import logging
import uuid
import traceback
import sys

import core

# for logging
class NullHandler(logging.Handler):
    def emit(self, record):
        pass

# logging setup
nullhandler = NullHandler()
log = logging.getLogger('rpcbd.base')
log.addHandler(nullhandler)


# thread safe container types

class Register(object):
    def __init__(self):
        self.register = []
        self.lock = threading.RLock()
    def to_tuple(self):
        with self.lock:
            return tuple(self.register)
    def append(self, item):
        with self.lock:
            self.register.append(item)
            return len(self.register) - 1
    def remove(self, item):
        with self.lock:
            self.register.remove(item)
    def index(self, item):
        with self.lock:
            return self.register.index(item)
    def clear(self):
        with self.lock:
            self.register = []
    def __getitem__(self, index):
        with self.lock:
            return self.register[index]
    def __setitem__(self, index, value):
        with self.lock:
            self.register[index] = value
    def __len__(self):
        with self.lock:
            return len(self.register)
    def __iter__(self):
        with self.lock:
            t = self.to_tuple()
            return t.__iter__()
    def setitem(self, index, value):
        'Sets self[index] = value, extending self with [None, ... , None] if necessary'
        with self.lock:
            if len(self.register) <= index:
                self.register.extend([None]*max(0, index-len(self.register)))
                self.register.append(value)
            else:
                self.register[index] = value
            
class SimpleMap(object):
    def __init__(self):
        self.cache = {}
        self.lock = threading.RLock()
    def lookup(self, item):
        'Return the value of the item in the cache, or None if it is not in the cache'
        with self.lock:
            return self.cache.get(item)
    def add(self, item, value):
        with self.lock:
            self.cache[item] = value
    def remove(self, item):
        with self.lock:
            self.cache.pop(item)
            

# datatypes for this implementation - should not be used directly
    
class InPacket(object):
    def __init__(self, connection, jsonitem):
        assert isinstance(connection, Connection)
        assert isinstance(jsonitem, core.JsonItem)
        self.connection = connection
        self.jsonitem = jsonitem
        self.raw_data = jsonitem.raw_data
    def deserialise(self):
        rpc_object = self.connection.connection_group.jsonrpc_processor.from_json(self.raw_data)
        if isinstance(rpc_object, core.Request):
            return InRequest(rpc_object.method, rpc_object.params, rpc_object.req_id, self)
        elif isinstance(rpc_object, core.Notification):
            return InNotification(rpc_object.method, rpc_object.params, self)
        elif isinstance(rpc_object, core.Response):
            return InResponse(rpc_object.result, rpc_object.req_id, self)
        elif isinstance(rpc_object, core.RpcError):
            return InRpcError(rpc_object, self)
    def process_raw_output(self, raw_data):
        # this should be the raw output in response to this incoming packet
        self.connection._send_raw_data(raw_data + core.EOT)
    def __repr__(self):
        return '<InPacket: %s>' % self.raw_data
    
class InArray(object):
    def __init__(self, connection, array_obj):
        assert isinstance(connection, Connection)
        assert isinstance(array_obj, core.JsonArray)
        self.connection = connection
        self.array_obj = array_obj
        self.elements = Register()
        self.results = Register()
    def add_raw_output(self, inarray_element, raw_data):
        assert isinstance(inarray_element, InArrayElement)
        array_element = inarray_element.jsonitem
        index = self.array_obj.elements.index(array_element)
        self.results.setitem(index, raw_data)
        if self.array_obj.complete and len(self.results) == len(self.array_obj.elements) \
                and None not in self.results.to_tuple():
            output = '[ %s ]' % ', '.join([ result for result in self.results.to_tuple() if result.strip() != ''])
            self.connection._send_raw_data(output + core.EOT)
        
class InArrayElement(InPacket):
    def __init__(self, inarray_obj, array_element):
        assert isinstance(inarray_obj, InArray)
        assert isinstance(array_element, core.JsonArrayElement)
        InPacket.__init__(self, inarray_obj.connection, array_element)
        self.inarray_obj = inarray_obj
        self.index = self.inarray_obj.elements.append(self)
        self.final = array_element.final
    def process_raw_output(self, raw_data):
        self.inarray_obj.add_raw_output(self, raw_data)
    def __repr__(self):
        return '<InArrayElement: %s>' % self.raw_data
        
        
class InRequest(object):
    def __init__(self, method, params, req_id, packet):
        self.method = method
        self.params = params
        self.req_id = req_id
        assert isinstance(packet, InPacket)
        self.packet = packet
        self.connection = packet.connection
    def lookup_dispatcher(self):
        return self.connection.connection_group._lookup_dispatcher(self.method)
    def register(self):
        self.connection._register_inrequest(self)
    def unregister(self):
        self.connection._remove_inrequest(self)
    def __repr__(self):
        return '<InRequest %s: %s(%s)>' % (self.req_id, self.method, self.params)
        
class OutRequest(object):
    def __init__(self, method, params, connection, result_handler, request_key, batch = None):
        self.method = method
        self.params = params
        self.connection = connection
        self.result_handler = result_handler
        self.batch = batch
        self.req_id = None
        self.request_key = request_key
    def serialise(self):
        self.req_id = self.connection._get_req_id()
        request = core.Request(self.method, self.params, self.req_id)
        return self.connection.connection_group.jsonrpc_processor.to_json(request) 
    def send(self):
        raw_data = self.serialise()
        self.register()
        index = None
        if self.batch is None:
            self.connection._send_raw_data(raw_data + core.EOT)
        else:
            index = self.batch._add_request(raw_data)
        return index
    def register(self):
        self.connection._register_outrequest(self)
    def unregister(self):
        self.connection._remove_outrequest(self)
    def __repr__(self):
        return '<OutRequest %s: %s(%s)>' % (self.req_id, self.method, self.params)

class InNotification(object):
    def __init__(self, method, params, packet):
        self.method = method
        self.params = params
        assert isinstance(packet, InPacket)
        self.packet = packet
        self.connection = packet.connection
    def lookup_dispatcher(self):
        return self.connection.connection_group._lookup_dispatcher(self.method)
    def __repr__(self):
        return '<InNotification: %s(%s)>' % (self.method, self.params)

class OutNotification(object):
    def __init__(self, method, params, connection):
        self.method = method
        self.params = params
        self.connection = connection
    def serialise(self):
        notification = core.Notification(self.method, self.params)
        return self.connection.connection_group.jsonrpc_processor.to_json(notification) 
    def send(self):
        raw_data = self.serialise() 
        self.connection._send_raw_data(raw_data + core.EOT)
    def __repr__(self):
        return '<OutNotificaton: %s(%s)>' % (self.method, self.params)
        
class InResponse(object):
    def __init__(self, result, req_id, packet):
        self.result = result
        self.req_id = req_id
        assert isinstance(packet, InPacket)
        self.packet = packet
        self.connection = packet.connection
        self.private_key = None
    def __repr__(self):
        return '<InResponse %s: %s>' % (self.req_id, self.result)
        
class OutResponse(object):
    def __init__(self, result, orig_request):
        self.result = result
        self.orig_request = orig_request
        self.connection = self.orig_request.connection
        assert isinstance(self.connection, Connection)
        assert isinstance(self.orig_request, InRequest)
    def serialise(self):
        response = core.Response(self.result, self.orig_request.req_id)
        return self.connection.connection_group.jsonrpc_processor.to_json(response)
    def send(self):
        raw_data = self.serialise()
        #self.connection._send_raw_data(raw_data)
        self.orig_request.packet.process_raw_output(raw_data)
        self.orig_request.unregister()
    def __repr__(self):
        return '<OutResponse to %s: %s>' % (self.orig_request, self.result)
        
                
class InRpcError(object):
    def __init__(self, rpcerror_obj, packet):
        assert isinstance(rpcerror_obj, core.RpcError)
        self.rpcerror_obj = rpcerror_obj
        self.req_id = rpcerror_obj.req_id
        assert isinstance(packet, InPacket)
        self.packet = packet
        self.connection = packet.connection

class OutRpcError(object):
    def __init__(self, rpcerror_obj, orig_request = None, connection = None):
        assert isinstance(rpcerror_obj, core.RpcError)
        self.rpcerror_obj = rpcerror_obj
        self.orig_request = orig_request
        if self.orig_request:
            self.connection = orig_request.connection
        else:
            self.connection = connection
        if self.connection is None:
            raise ValueError('either orig_request or connection must be set')
    def serialise(self):
        if self.orig_request is not None:
            self.rpcerror_obj.req_id = self.orig_request.req_id
        return self.connection.connection_group.jsonrpc_processor.to_json(self.rpcerror_obj)
    def send(self):
        raw_data = self.serialise()
        if self.orig_request:
            self.orig_request.packet.process_raw_output(raw_data)
        else:
            self.connection._send_raw_data(raw_data + core.EOT)
            
class CallbackWrapper(object):
    def __init__(self, connection, cb_register, orig_handler):
        self.connection = connection
        self.orig_handler = orig_handler
        self.cb_register = cb_register
    def __call__(self, req_key, result):
        for cbname in self.cb_register:
            self.connection._remove_callback(cbname)
            log.debug('removed callback %s' % cbname)
        self.orig_handler(req_key, result)


class AsyncMethodProxy(object):
    def __init__(self, connection, proxy, method):
        assert isinstance(connection, Connection)
        self.connection = connection
        self.proxy = proxy
        self.result_handler = proxy._result_handler
        self.method = method
    def __call__(self, *args, **kwargs):
        if len(args) > 0 and len(kwargs) > 0:
            raise ValueError("Can not use both positional and named arguments")
        result_handler = self.result_handler
        if result_handler is not None:
            # update callable parameters into 'callback' strings
            cbregister = []
            if len(args) > 0:
                new_args = []
                for value in args:
                    if callable(value):
                        func = value
                        cbname = self.connection._add_callback(func)
                        cbregister.append(cbname)
                        new_args.append(cbname)
                        log.debug('added callback %s' % cbname)
                    else:
                        new_args.append(value)
                args = tuple(new_args)
            for (key, value) in kwargs.iteritems():
                if callable(kwargs[key]):
                    func = kwargs[key]
                    cbname = self.connection._add_callback(func)
                    cbregister.append(cbname)
                    kwargs[key] = cbname
                    log.debug('added callback %s' % cbname)
            if cbregister:
                result_handler = CallbackWrapper(self.connection, cbregister, self.result_handler)
        if len(kwargs) == 0:
            params = args
        else:
            params = kwargs
        request_key = None
        if self.result_handler is None:
            # notification
            out_rpc = OutNotification(self.method, params, self.connection)
        else:
            request_key = self.proxy._get_req_key()
            out_rpc = OutRequest(self.method, params, self.connection, result_handler, request_key)
        out_rpc.send()
        return request_key
        
class AsyncProxy(object):
    def __init__(self, connection, result_handler, proxy_txt_id):
        assert isinstance(connection, Connection)
        self._connection = connection
        self._result_handler = result_handler
        self._proxy_txt_id = proxy_txt_id
        self._next_req_id = 1
        self._req_id_lock = threading.RLock()
    def _get_req_key(self):
        with self._req_id_lock:
            req_key = '%s-%s' % (self._proxy_txt_id, self._next_req_id)
            self._next_req_id += 1
            return req_key
    def __call__(self, method):
        return AsyncMethodProxy(self._connection, self, method)    
    def __getattribute__(self, method):
        try:
            return object.__getattribute__(self, method)
        except AttributeError:
            return AsyncMethodProxy(self._connection, self, method)
        
class SyncResult(object):
    def __init__(self, result):
        self.result = result
        
class SyncCallback(object):
    def __init__(self, func, queue_to_caller):
        self.func = func
        self.queue_to_caller = queue_to_caller
        self.return_queue = Queue.Queue()
    def __call__(self, *args, **kwargs):
        self.args = args
        self.kwargs = kwargs
        self.queue_to_caller.put(self)
        result = self.return_queue.get()
        return result
        
class SyncMethodProxy(object):
    def __init__(self, connection, method, timeout, prefix):
        self.connection = connection
        self.method = method
        self.timeout = timeout
        self.prefix = prefix
        self.result_queue = Queue.Queue()        
    def _result_cb(self, req_key, result):
        self.result_queue.put(SyncResult(result))
    @property
    def _full_method_name(self):
        if self.prefix is None:
            return self.method
        else:
            return '%s.%s' % (self.prefix, self.method)
    def __getattribute__(self, method):
        try:
            return object.__getattribute__(self, method)
        except AttributeError:
            if method.startswith('_'):
                raise
            else:
                return SyncMethodProxy(self.connection, method, self.timeout, self._full_method_name)        
    def __call__(self, *args, **kwargs):
        # wrap up callable args
        if len(args) > 0:
            new_args = []
            for value in args:
                if callable(value):
                    new_args.append(SyncCallback(value, self.result_queue))
                else:
                    new_args.append(value)
            args = tuple(new_args)
        for (key, value) in kwargs.iteritems():
            if callable(kwargs[key]):
                func = kwargs[key]
                kwargs[key] = SyncCallback(func, self.result_queue)
        async_proxy = self.connection.create_async_proxy(result_handler = self._result_cb)
        getattr(async_proxy, self._full_method_name)(*args, **kwargs)
        while True:
            try:
                callback_or_result = self.result_queue.get(timeout = self.timeout)
            except Queue.Empty:
                raise core.TransportError('No response within timeout')
            if isinstance(callback_or_result, SyncCallback):
                cb = callback_or_result
                try:
                    cb_result = cb.func(*cb.args, **cb.kwargs)
                except Exception, e:
                    cb_result = core.RpcInternalError(data = str(e))
                cb.return_queue.put(cb_result)
            elif isinstance(callback_or_result, SyncResult):
                result = callback_or_result.result
                if isinstance(result, core.RpcError):
                    raise result
                break
            else:
                log.error('Unexpected item in callback and result queue.')
        return result


class SyncProxy(object):
    # used for requests only since we can use the AsyncProxy for notifications
    def __init__(self, connection):
        self._connection = connection
    def __call__(self, method, timeout = -1):
        if timeout == -1:
            timeout = self._connection.default_timeout
        return SyncMethodProxy(self._connection, method, timeout, None)
    def __getattribute__(self, method):
        try:
            return object.__getattribute__(self, method)
        except AttributeError:
            return SyncMethodProxy(self._connection, method, self._connection.default_timeout, None)
        
class BatchedCalls(object):
    # the compilation of a batch is NOT THREADSAFE! 
    # ie, all actions on the batch between the creation and the send should happen in the same thread
    def __init__(self, connection):
        assert isinstance(connection, Connection)
        self.results = Register()
        self._connection = connection
        self._flag = threading.Event()
        self._response_handler = None
        self._calls = [] 
        self._requests = []
        self._sent = False
    def reset(self):
        if self._sent and not self._flag.isSet():
            # sent a message but have not seen all the response yet
            raise ValueError('Can not reset until response seen.')  # probably should define a custom error here?
        self.results.clear()
        self._flag.clear()
        self._response_handler = None
        self._calls = [] 
        self._requests = []
        self._sent = False
    @property
    def request(self):
        if self._sent:
            raise ValueError('Batch sent but not reset')
        return BatchProxy(self)
    @property
    def notification(self):
        if self._sent:
            raise ValueError('Batch sent but not reset')
        return BatchProxy(self, notification = True)
    def send(self, response_handler = None, timeout = -1):
        if self._sent:
            raise ValueError('Batch sent but not reset')
        self._sent = True
        self._response_handler = response_handler
        self._connection._send_raw_data('[ ' + ', '.join(self._calls) + ' ]' + core.EOT)
        if response_handler is None:
            if timeout == -1:
                timeout = self._connection.default_timeout
            self._flag.wait(timeout = timeout)
            if self._flag.isSet():
                return self.results.to_tuple()
            else:
                raise core.TransportError('Timeout before all responses back')
    def _result_handler(self, req_key, result, index = None):
        self.results.setitem(index, result)
        return self  # so we can link to _batch_handler
    def _batch_complete_handler(self):
        # called when incoming batch is complete
        if self._response_handler is None:
            self._flag.set()
        else:
            self._response_handler(self.results.to_tuple())
    def _add_notifcation(self, raw_data):
        self._calls.append(raw_data)
    def _add_request(self, raw_data):
        self._calls.append(raw_data)
        self._requests.append(raw_data)  # or None?
        return len(self._requests) - 1
    
class BatchProxy(object):
    def __init__(self, batch, notification = False):
        self._batch = batch
        self._notification = notification
    def __getattribute__(self, method):
        try:
            return object.__getattribute__(self, method)
        except AttributeError:
            return BatchMethodProxy(self._batch, method, self._notification)
    
class BatchMethodProxy(object):
    def __init__(self, batch, method, notification):
        self._batch = batch
        self._method = method
        self._notification = notification
        self._connection = self._batch._connection
    def __call__(self, *args, **kwargs):
        if len(args) > 0 and len(kwargs) > 0:
            raise ValueError("Can not use both positional and named arguments")
        if len(kwargs) == 0:
            params = args
        else:
            params = kwargs
        if self._notification:
            out_rpc = OutNotification(self._method, params, self._connection, self._batch)
        else:
            out_rpc = OutRequest(self._method, params, self._connection, self._batch._result_handler, None, self._batch)
        return out_rpc.send()
        
# end datatypes

# core compoents of the rpc system (used by all engine types)
    
class ConnectionGroup(object):
    def __init__(self, server, name, jsonrpc_processor = None):
        self.server = server
        self.name = name
        if jsonrpc_processor is None:
            jsonrpc_processor = self.server.default_rpc_processor
        if jsonrpc_processor is None:
            raise ValueError("jsonrpc_processor must be set if the server's default_rpc_processor is not")        
        assert isinstance(jsonrpc_processor, core.JsonRpcProcessor)
        self.jsonrpc_processor = jsonrpc_processor
        self._dispatchers = Register()
        self._connections = Register()
        self._dispatcher_cache = SimpleMap()
        self.server._connection_groups.append(self)
    @property
    def connections(self):
        return self._connections.to_tuple()
    def __repr__(self):
        return "<ConnectionGroup '%s' at %s>" % (self.name, id(self))
    def add_dispatcher(self, dispatcher):
        assert isinstance(dispatcher, Dispatcher)
        self._dispatchers.append(dispatcher)
    def _lookup_dispatcher(self, method_name):
        'Returns the dispatcher class for the given method, or None if not found'
        dispatcher = self._dispatcher_cache.lookup(method_name)
        if dispatcher is None:
            for dispatcher in self._dispatchers.to_tuple():
                if dispatcher._accept_method(method_name):
                    break
            else:
                raise core.RpcMethodNotFoundError
        return dispatcher    

    
# class to be subclassed by server implementation
    
class Connection(object):
    def __init__(self, connection_group, description, default_timeout):
        ''' description = description of the connection, eg "TCP/IP Remote xxxx:xxx Local xxxx:xxx"
        '''
        assert isinstance(connection_group, ConnectionGroup)
        assert isinstance(connection_group.server, ClientServerBase)
        self.description = description
        self.connection_group = connection_group
        self.default_timeout = default_timeout
        self.server = connection_group.server
        self.jsonrpc_processor = connection_group.jsonrpc_processor
        self._handler_instances = SimpleMap()  # cache of handler instances, keyed by class
        self._inrequest_register = Register()  # InRequests that we have not yet sent a response to
        self._outrequest_register = Register()  # OutRequests that we have not yet received a response to
        self._req_id_lock = threading.RLock()
        self._next_req_id = 1
        self._proxy_id_lock = threading.RLock()
        self._next_proxy_id = 1
        self.connection_group._connections.append(self)
        self.send_lock = threading.Lock()
        self._splitter = core.JsonSplitter()
        self._callback_mapping = SimpleMap()  # requests of the form rpc.callback.XXXXX will be stored here as XXXX
        self._array_map = SimpleMap()  # maps JsonArray objects to InArray objects
        log.info('Created connection with id: %s' % id(self))
    def create_async_proxy(self, result_handler = None, proxy_txt_id = None):
        if proxy_txt_id is None:
            proxy_txt_id = 'proxy-%s' % self._get_proxy_id()
        return AsyncProxy(self, result_handler, proxy_txt_id)
    def create_batchedcalls_proxy(self):
        return BatchedCalls(self)
    @property
    def notification(self):
        return self.create_async_proxy(result_handler = None)
    @property
    def request(self):
        return SyncProxy(self)
    def close(self):
        self._transport_level_close()
        log.info('Closed connection with id: %s' % id(self))
        for request in self._inrequest_register:
            log.warning('INCOMING Requests: A response to request %s was not sent' % request)
        for request in self._outrequest_register:
            log.warning('OUTGOING Requests: A response to request %s was not seen' % request)
            try:
                request.result_handler(core.TransportError('Connection was closed.'))  # track if peer or us!
            except:
                log.error('Request handler call failed')
    def _get_proxy_id(self):
        with self._proxy_id_lock:
            proxy_id = self._next_proxy_id
            self._next_proxy_id += 1
            return proxy_id        
    def _get_req_id(self):
        with self._req_id_lock:
            req_id = self._next_req_id
            self._next_req_id += 1
            return req_id
    def _register_inrequest(self, request):
        self._inrequest_register.append(request)
    def _remove_inrequest(self, request):
        self._inrequest_register.remove(request)
    def _register_outrequest(self, request):
        self._outrequest_register.append(request)
    def _remove_outrequest(self, request):
        self._outrequest_register.remove(request)
    def _get_handler_instance(self, kls):
        handler_instance = self._handler_instances.lookup(kls)
        if handler_instance is None:
            handler_instance = kls(connection = self)
            self._handler_instances.add(kls, handler_instance)
        return handler_instance
    def _add_callback(self, func):
        callback_name = 'rpc.callback.%s' % uuid.uuid4()
        self._callback_mapping.add(callback_name, func)
        return callback_name
    def _remove_callback(self, callback_name):
        self._callback_mapping.remove(callback_name)
    def _process_incoming_data(self, raw_data):
        for jsonitem in self._splitter.add_data(raw_data):
            if isinstance(jsonitem, core.JsonObject):
                inpacket = InPacket(self, jsonitem)                
                self.server._process_incoming_jsonitem(self, inpacket)
            elif isinstance(jsonitem, core.JsonArrayElement):
                array_obj = jsonitem.array_obj
                inarray_obj = self._array_map.lookup(array_obj)
                if inarray_obj is None:
                    inarray_obj = InArray(self, array_obj)
                    self._array_map.add(array_obj, inarray_obj)
                inarrayelement = InArrayElement(inarray_obj, jsonitem)
                self.server._process_incoming_jsonitem(self, inarrayelement)                
            else:
                raise ValueError('Unexpected jsonitem type')
    def _send_raw_data(self, raw_data):
        log.info('--> %s: %s' % (id(self), raw_data))
        with self.send_lock:
            self._transport_level_send(raw_data)
    def _transport_level_send(self, raw_data):
        raise NotImplemented        
    def _transport_level_close(self):
        raise NotImplemented

            
class Dispatcher(object):
    '''To be subclassed'''
    def __init__(self, server):
        self.server = server
    def _accept_method(self, method_name):
        raise NotImplementedError
    def _dispatch(self, rpc_object):
        raise NotImplementedError
    
def _do_dispatch(rpc_object, func, args, kwargs):
    error_obj = None
    try:
        result = func(*args, **kwargs)
    except TypeError, e:
        error_obj = core.RpcInvalidParamsError(data = str(e))
        print traceback.print_exc()
    except Exception, e:
        error_obj = core.RpcInternalError(data = str(e))
        print traceback.print_exc()
    if error_obj:
        # log error locally
        tback = traceback.format_exc()
        ##exc_type, exc_value, exc_traceback = sys.exc_info()
        ##tb = traceback
        log.error(tback)
    if isinstance(rpc_object, InRequest):
        if error_obj:
            outerror = OutRpcError(error_obj, rpc_object)
            outerror.send()
        else:
            outresult = OutResponse(result, rpc_object)
            try:
                outresult.send()
            except TypeError, e:
                error_obj = core.RpcInternalError(data = str(e))
                outerror = OutRpcError(error_obj, rpc_object)
                outerror.send()
                print traceback.print_exc()
    
    
class Worker(object):
    def __init__(self, dispatcher):
        assert isinstance(dispatcher, StandardDispatcher)
        self.dispatcher = dispatcher
        self.worker_thread = threading.Thread(target = self._worker)
        self.worker_thread.setDaemon(True)
        self.time_to_stop = False
    def _worker(self):
        t = threading.currentThread()
        log.debug('Staring worker in thread %s' % t.getName())
        while not self.time_to_stop:
            try:
                rpc_object, func, args, kwargs = self.dispatcher.dispatch_queue.get(timeout = 5)
            except Queue.Empty:
                continue
            _do_dispatch(rpc_object, func, args, kwargs)
    def start(self):
        self.worker_thread.start()
    def stop(self):
        self.time_to_stop = True
    def join(self, timeout):
        self.worker_thread.join(timeout)
    

class StandardDispatcher(Dispatcher):
    '''A standard dispatcher exports the methods of handler classes,
    and either runs the methods 'in thread' or in the 'worker pool'
    depending on the annotation of the handler class.
    '''
    def __init__(self, server, numthreads = 5, maxqueue = 1000):
        Dispatcher.__init__(self, server)
        self.handler_methods = {}
        self.dispatch_queue = Queue.Queue(maxsize = maxqueue)
        self.workers = [ Worker(self) for x in range(numthreads) ]
        for worker in self.workers:
            worker.start()
    def add_handler_class(self, handler_cls, prefix=None):
        'Add in a handler class (not instance thereof)'
        if not issubclass(handler_cls, Handler):
            raise ValueError('handler must be subclass of Handler')
        if prefix is None:
            prefix = ''
        else:
            prefix = '%s.' % prefix
        for method_name in dir(handler_cls):
            func = getattr(handler_cls, method_name)
            if not callable(func):
                continue
            if hasattr(func, '_exposed_'):
                add_method = func._exposed_
            else:
                add_method = (method_name[0] != '_')
            if add_method:
                if not hasattr(func, '_mayblock_') and handler_cls.assume_methods_block not in (True, False):
                    errormsg = """
                    handler must set 'assume_methods_block' either True or False, or decorate every exposed method with @mayblock or @willnotblock.
                    this has not bee done for %s""" % method_name
                    raise ValueError(errormsg.strip())
                rpcname = prefix + method_name
                if self.handler_methods.has_key(rpcname):
                    raise ValueError('duplicate rpc method name %s' % rpcname)
                else:
                    self.handler_methods[rpcname] = (handler_cls, method_name)
                    log.info('Added rpc method for %s' % rpcname)
    def _accept_method(self, rpcname):
        return self.handler_methods.has_key(rpcname)
    def _dispatch(self, rpc_object):
        assert isinstance(rpc_object, (InRequest, InNotification))
        rpcname = rpc_object.method
        kls, method_name = self.handler_methods[rpcname]
        connection = rpc_object.connection
        handler = connection._get_handler_instance(kls)
        assert isinstance(handler, Handler)
        func = getattr(handler, method_name)
        try:
            mayblock = func._mayblock_
        except AttributeError:
            mayblock = handler.assume_methods_block
        args, kwargs = connection.connection_group.jsonrpc_processor.convert_params(rpc_object.params)
        if mayblock:
            log.debug("dispatching call to '%s' in worker pool" % rpcname)
            dispatch_item = (rpc_object, func, args, kwargs)
            try:
                self.dispatch_queue.put_nowait(dispatch_item)
            except Queue.Full:
                error_obj = core.RpcServerError(code = -32000, data = 'Server busy. Try again later.')
                outerror = OutRpcError(error_obj, rpc_object)
                outerror.send()
        else:
            # won't block, so run in current thread
            log.debug("dispatching call to '%s' in current thread" % rpcname)
            _do_dispatch(rpc_object, func, args, kwargs)
        

# class to be subclassed by server implementation

# NOTES: if no default connection group is set, an empty one is created and set as the default, if there is a default processor

class ClientServerBase(object):
    '''To be subclassed by client & server implementations'''
    def __init__(self, default_rpc_processor, default_handler = None):
        self.default_rpc_processor = default_rpc_processor
        self._connection_groups = Register()
        self._default_incoming_connection_group = ConnectionGroup(self, 'Default Incoming', default_rpc_processor)
        self._default_outgoing_connection_group = ConnectionGroup(self, 'Default Outgoing', default_rpc_processor)
        if default_handler:
            default_dispatcher = self.create_standard_dispatcher()
            default_dispatcher.add_handler_class(default_handler)
            self._default_incoming_connection_group.add_dispatcher(default_dispatcher)
            self._default_outgoing_connection_group.add_dispatcher(default_dispatcher)
    @property
    def connection_groups(self):
        return self._connection_groups.to_tuple()
    def _get_default_incoming_connection_group(self):
        return self._default_incoming_connection_group
    def _set_default_incoming_connection_group(self, connection_group):
        if connection_group not in self.connection_groups:
            raise ValueError('connection_group given does not belong to server')
        else:
            self._default_incoming_connection_group = connection_group
    default_incoming_connection_group = property(_get_default_incoming_connection_group, _set_default_incoming_connection_group)
    def _get_default_outgoing_connection_group(self):
        return self._default_outgoing_connection_group        
    def _set_default_outgoing_connection_group(self, connection_group):
        if connection_group not in self.connection_groups:
            raise ValueError('connection_group given does not belong to server')
        else:
            self._default_outgoing_connection_group = connection_group
    default_outgoing_connection_group = property(_get_default_outgoing_connection_group, _set_default_outgoing_connection_group)
    def create_connection_group(self, name, rpc_processor = None):
        return ConnectionGroup(self, name, rpc_processor)
    def create_standard_dispatcher(self):
        return StandardDispatcher(self)

    # -------------------------------------------------------
    # 
    # subclasses must define methods that create connections,
    # or allow connections to be created.
    # eg. listenTCP(...., connection_group = None)
    #     connectTCP(...., connection_group = None)
    #     listen_stdin(...)
    #     connect_stdout(...)
    # 
    # --------------------------------------------------------

    def _process_incoming_jsonitem(self, connection, inpacket):
        "raw_data should be a complete json 'packet'"
        
        assert isinstance(inpacket, InPacket)
        log.info('<-- %s: %s' % (id(connection), inpacket.raw_data))
        try:
            rpc_object = inpacket.deserialise()
        except core.RpcInvalidError:
            log.error('Invalid Rpc packet received: %s' % inpacket.raw_data)
            return
        if isinstance(rpc_object, InRequest):
            rpc_object.register()
            try:
                self._dispatch(rpc_object)
            except core.RpcError, e:
                outerror = OutRpcError(e, rpc_object)
                outerror.send()
        elif isinstance(rpc_object, InNotification):
            try:
                self._dispatch(rpc_object)
            except core.RpcError:
                pass  # never return errors to a Notification
            if isinstance(inpacket, InArrayElement):
                inpacket.process_raw_output('')  # mark as processed
        elif isinstance(rpc_object, InResponse):
            req_id = rpc_object.req_id
            id_found = False
            for request in rpc_object.connection._outrequest_register.to_tuple():
                if req_id == request.req_id:
                    id_found = True
                    if isinstance(inpacket, InArrayElement):
                        try:
                            batch_obj = request.result_handler(request.request_key, rpc_object.result, inpacket.index)
                            if inpacket.final:
                                batch_obj._batch_complete_handler()
                        except ValueError:
                            log.warning('Batch type result handler call failed. Trying standard call')
                            try:
                                request.result_handler(request.request_key, rpc_object.result)
                            except:
                                log.error('Request handler call failed')
                    else:
                        try:
                            ##print 'Calling %r with %s, %s' % (request.result_handler, request.request_key, rpc_object.result)
                            request.result_handler(request.request_key, rpc_object.result)
                        except Exception, e:
                            log.error('Request handler call failed: %s' % e)
                    request.unregister()
                    break
            if not id_found:
                # consider error since it should never happen
                log.error('Got response to unknown request id %s' % req_id)
        elif isinstance(rpc_object, InRpcError):
            req_id = rpc_object.req_id
            error_obj = rpc_object.rpcerror_obj
            if req_id is not None:
                id_found = False
                for request in rpc_object.connection._outrequest_register.to_tuple():
                    if req_id == request.req_id:
                        id_found = True
                        request.result_handler(request.request_key, error_obj)
                        request.unregister()
                        break
                if not id_found:
                    # consider error since it should never happen
                    log.error('Got incoming RpcError response to unknown request id %s' % req_id)
            else:
                # error with no req_id - could be parse error other end
                log.error('Got incoming RpcError reponse with null id')                
    
    def _dispatch(self, inrequest_or_innotification):
        'To be called by the server when a request comes in'
        if inrequest_or_innotification.method.startswith('rpc.callback.'):
            connection = inrequest_or_innotification.connection
            func = connection._callback_mapping.lookup(inrequest_or_innotification.method)
            if func is None:
                log.warning('Got call to invalid callback %s' % inrequest_or_innotification.method)
            else:
                args, kwargs = connection.connection_group.jsonrpc_processor.convert_params(inrequest_or_innotification.params)
                _do_dispatch(inrequest_or_innotification, func, args, kwargs)
        dispatcher = inrequest_or_innotification.lookup_dispatcher()
        dispatcher._dispatch(inrequest_or_innotification)            


# class to be subclassed by 'user'

class Handler(object):
    '''To be subclassed by jsonrpc method classes
    Note: an instance of the subclass will be created for each connection'''
    assume_methods_block = None  # must be set either True or False by subclass!
    def __init__(self, connection):
        self.connection = connection

# by default methods beginning with _ are private and not exposed,
# and all other methods are exposed
# the decorators below can override this

def exposed(func):
    func._exposed_ = True
    return func

def hidden(func):
    func._exposed_ = False
    return func

def mayblock(func):
    func._mayblock_ = True
    return func

def willnotblock(func):
    func._mayblock_ = False
    return func
