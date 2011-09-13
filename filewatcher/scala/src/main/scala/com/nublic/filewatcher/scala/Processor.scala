package com.nublic.filewatcher.scala

import scala.actors.Actor
import scala.actors.Actor._

abstract class Processor(val name: String, val watcher: FileWatcherActor) extends Actor {

  def process(c: FileChange)
  
  def act() = {
    loop {
      react {
        case ForwardFileChange(id, c) => {
          process(c)
          watcher ! BackFileChange(name, id, c)
        }
      }
    }
  }
}