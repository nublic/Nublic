package com.nublic.app.manager.server

case class ReturnUser(username: String, uid: Int, name: String)
case class ReturnMirror(id: Int, name: String)
case class ReturnSyncedFolder(id: Int, name: String)
