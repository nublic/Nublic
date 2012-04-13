package com.nublic.ws.json

class Method0[R](val method: String, val rpc: WebSocketJsonRpc)(implicit m: Manifest[R]) {
  def apply(): Response[R] = rpc.sendAndParse[R](method, List())
  def async(cb: Callback[R]): Unit = rpc.asyncSend(method, List(), cb)
  def notify(): Unit = rpc.notify(method, List())
}

class Method1[A <: AnyRef, R](val method: String, val rpc: WebSocketJsonRpc)(implicit m: Manifest[R]) {
  def apply(arg1: A): Response[R] = rpc.sendAndParse[R](method, List(arg1))
  def async(arg1: A, cb: Callback[R]): Unit = rpc.asyncSend(method, List(arg1), cb)
  def notify(arg1: A): Unit = rpc.notify(method, List(arg1))
}

class Method2[A <: AnyRef, B <: AnyRef, R](val method: String, val rpc: WebSocketJsonRpc)(implicit m: Manifest[R]) {
  def apply(arg1: A, arg2: B): Response[R] = rpc.sendAndParse[R](method, List(arg1, arg2))
  def async(arg1: A, arg2: B, cb: Callback[R]): Unit = rpc.asyncSend(method, List(arg1, arg2), cb)
  def notify(arg1: A, arg2: B) = rpc.notify(method, List(arg1, arg2))
}

class Method3[A <: AnyRef, B <: AnyRef, C <: AnyRef, R](val method: String, val rpc: WebSocketJsonRpc)(implicit m: Manifest[R]) {
  def apply(arg1: A, arg2: B, arg3: C): Response[R] = rpc.sendAndParse[R](method, List(arg1, arg2, arg3))
  def async(arg1: A, arg2: B, arg3: C, cb: Callback[R]): Unit = rpc.asyncSend(method, List(arg1, arg2, arg3), cb)
  def notify(arg1: A, arg2: B, arg3: C): Unit = rpc.notify(method, List(arg1, arg2, arg3))
}

class Method4[A <: AnyRef, B <: AnyRef, C <: AnyRef, D <: AnyRef, R](val method: String, val rpc: WebSocketJsonRpc)(implicit m: Manifest[R]) {
  def apply(arg1: A, arg2: B, arg3: C, arg4: D): Response[R] = 
    rpc.sendAndParse[R](method, List(arg1, arg2, arg3, arg4))
  def async(arg1: A, arg2: B, arg3: C, arg4: D, cb: Callback[R]): Unit = 
    rpc.asyncSend(method, List(arg1, arg2, arg3, arg4), cb)
  def notify(arg1: A, arg2: B, arg3: C, arg4: D): Unit = 
    rpc.notify(method, List(arg1, arg2, arg3, arg4))
}

class Method5[A <: AnyRef, B <: AnyRef, C <: AnyRef, D <: AnyRef, E <: AnyRef, R](val method: String, val rpc: WebSocketJsonRpc)(implicit m: Manifest[R]) {
  def apply(arg1: A, arg2: B, arg3: C, arg4: D, arg5: E): Response[R] =
    rpc.sendAndParse(method, List(arg1, arg2, arg3, arg4, arg5))
  def async(arg1: A, arg2: B, arg3: C, arg4: D, arg5: E, cb: Callback[R]): Unit =
    rpc.asyncSend(method, List(arg1, arg2, arg3, arg4, arg5), cb)
  def notify(arg1: A, arg2: B, arg3: C, arg4: D, arg5: E): Unit =
    rpc.notify(method, List(arg1, arg2, arg3, arg4, arg5))

}
