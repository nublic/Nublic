package com.nublic.filewatcher.scala

import scala.actors.Actor
import scala.actors.Actor._
//import java.util.logging.Logger

abstract class Processor(val name: String, val watcher: FileWatcherActor, val throwException: Boolean) extends Actor {

  def process(c: FileChange)
  
  def act() = {
    loop {
      react {
        case ForwardFileChange(id, c) => {
          try {
            process(c)
//            Logger.getGlobal().severe("Processed " + name)
          } catch {
            case t: Throwable => {
              if (throwException) {
                throw new Exception("Error in file processing", t);
              } else {
//                Logger.getGlobal().severe("ERROR IN " + name + " PROCESSOR: " + t.getMessage())
              }
            }
          }
          watcher ! BackFileChange(name, id, c)
        }
      }
    }
  }
}