python-rpcbd - A lightweight Bidirectional Rpc System for Python.

    * Multi-lingual. Implements JsonRpc v1.0 and JsonRpc v2.0, so is designed
       to interface with programs written in other languages, not just python.
    * Bidirectional. Each end of the connection can act as client and server,
        regardless of which end initiated the connection.
    * Transport agnostic. (Currently only TCP connections are implemented, 
       but other transports are planned.)
    * Fully asynchronous. Requests can be sent and received at any time.
       Additional requests may be sent over the same connection, without
       waiting for the responses to earlier requests.
    * Threadsafe. Requests may be sent from any thread.
    * Simple. The API is designed to be simple and intuitive to use.
       One can use callbacks in a natural way.
    * Interactive. Can be used from a Python Interactive Prompt
        in a natural way, even when running as a 'server'.
    * Completely Free. Is licensed under the very liberal MIT License.

Examples and basic documentation are at http://www.openminddev.net/rpcbd/python/


Release Notes:

* 0.6.0 Release - 25 Aug 2010

This release is a major break from prior version, and is incompatible at the protocol level.
In this release the ascii character EOT (character 0x04) is used to mark the end of each
jsonrpc message.

See http://groups.google.com/group/json-rpc/browse_thread/thread/71795cf1b0fe6bf3/174d07504312e5c8
 for discussion.

This release is a transitional release and the code still needs a major tidy up to
 reflect the changes cleanly.




