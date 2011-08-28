package com.nublic.app.example.server

import org.scalatra._
import org.scalatra.liftjson._
import net.liftweb.json._
import net.liftweb.json.Serialization.{read, write}

class ExampleServer extends ScalatraFilter with JsonSupport {
  // JsonSupport adds the ability to return JSON objects
  
  get("/say/:name") {
    implicit val formats = Serialization.formats(NoTypeHints)
    var greeting = new Greeting("Hello " + params("name") + "!")
    write(greeting)  // write(...) converts an object to JSON
  }
  
  notFound {  // Executed when no other route succeeds
    JNull
  }

}
