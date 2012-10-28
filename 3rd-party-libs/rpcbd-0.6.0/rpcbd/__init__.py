# __init__.py

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


import inspect
import core

# Import Rpc Error classes

from core import RpcError, RpcInternalError, RpcInvalidError, RpcInvalidParamsError, \
                              RpcMethodNotFoundError, RpcParseError, RpcServerError, TransportError

from base import Handler, exposed, hidden, mayblock, willnotblock

# Import servers

from rpcbd.threaded_peer import ThreadedTCPJsonRpcPeer

JSONRPC_V1 = core.JsonRpcV1()
JSONRPC_V2 = core.JsonRpcV2()    

__all__ = sorted(name for name, obj in locals().items()
                 if not (name.startswith('_') or inspect.ismodule(obj)))
                 
__version__ = '0.6.0'

del inspect
