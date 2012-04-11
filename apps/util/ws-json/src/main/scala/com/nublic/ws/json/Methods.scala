package com.nublic.ws.json

class Method0[R](val method: String, val rpc: WebSocketJsonRpc) {
  def apply(implicit manifest: Manifest[R]): R = rpc.sendAndParse[R](method, Array())
}

class Method1[A <: AnyRef, R](val method: String, val rpc: WebSocketJsonRpc) {
  def apply(arg1: A)(implicit manifest: Manifest[R]): R = rpc.sendAndParse[R](method, Array(arg1))
}

class Method2[A <: AnyRef, B <: AnyRef, R](val method: String, val rpc: WebSocketJsonRpc) {
  def apply(arg1: A, arg2: B)
    (implicit manifest: Manifest[R]): R = 
      rpc.sendAndParse[R](method, Array(arg1, arg2))
}

class Method3[A <: AnyRef, B <: AnyRef, C <: AnyRef, R](val method: String, val rpc: WebSocketJsonRpc) {
  def apply(arg1: A, arg2: B, arg3: C)
    (implicit manifest: Manifest[R]): R = 
      rpc.sendAndParse[R](method, Array(arg1, arg2, arg3))
}

class Method4[A <: AnyRef, B <: AnyRef, C <: AnyRef, D <: AnyRef, R](val method: String, val rpc: WebSocketJsonRpc) {
  def apply(arg1: A, arg2: B, arg3: C, arg4: D)
    (implicit manifest: Manifest[R]): R = 
      rpc.sendAndParse[R](method, Array(arg1, arg2, arg3, arg4))
}

class Method5[A <: AnyRef, B <: AnyRef, C <: AnyRef, D <: AnyRef, E <: AnyRef, R](val method: String, val rpc: WebSocketJsonRpc) {
  def apply(arg1: A, arg2: B, arg3: C, arg4: D, arg5: E)
    (implicit manifest: Manifest[R]): R = 
      rpc.sendAndParse[R](method, Array(arg1, arg2, arg3, arg4, arg5))
}

class AsyncMethod0(val method: String, val rpc: WebSocketJsonRpc) {
  def apply(): Unit = rpc.asyncSend(method, Array())
}

class AsyncMethod1[A <: AnyRef](val method: String, val rpc: WebSocketJsonRpc) {
  def apply(arg1: A): Unit = rpc.asyncSend(method, Array(arg1))
}

class AsyncMethod2[A <: AnyRef, B <: AnyRef](val method: String, val rpc: WebSocketJsonRpc) {
  def apply(arg1: A, arg2: B): Unit = 
      rpc.asyncSend(method, Array(arg1, arg2))
}

class AsyncMethod3[A <: AnyRef, B <: AnyRef, C <: AnyRef](val method: String, val rpc: WebSocketJsonRpc) {
  def apply(arg1: A, arg2: B, arg3: C): Unit = 
      rpc.asyncSend(method, Array(arg1, arg2, arg3))
}

class AsyncMethod4[A <: AnyRef, B <: AnyRef, C <: AnyRef, D <: AnyRef](val method: String, val rpc: WebSocketJsonRpc) {
  def apply(arg1: A, arg2: B, arg3: C, arg4: D): Unit = 
      rpc.asyncSend(method, Array(arg1, arg2, arg3, arg4))
}

class AsyncMethod5[A <: AnyRef, B <: AnyRef, C <: AnyRef, D <: AnyRef, E <: AnyRef](val method: String, val rpc: WebSocketJsonRpc) {
  def apply(arg1: A, arg2: B, arg3: C, arg4: D, arg5: E): Unit = 
      rpc.asyncSend(method, Array(arg1, arg2, arg3, arg4, arg5))
}
