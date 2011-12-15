package com.nublic.filewatcher.scala

import scala.actors.Actor
import scala.actors.Actor._
import java.util.logging.Logger

abstract class Processor(val name: String, val watcher: FileWatcherActor) extends Actor {

  def process(c: FileChange)
  
  def act() = {
    loop {
      react {
        case ForwardFileChange(id, c) => {
          try {
            process(c)
            Logger.getGlobal().finer("Processed " + name)
          } catch {
            case t: Throwable => {
              Logger.getGlobal().fine("ERROR IN " + name + " PROCESSOR: " + t.getMessage())
            }
          }
          watcher ! BackFileChange(name, id, c)
        }
      }
    }
  }
}