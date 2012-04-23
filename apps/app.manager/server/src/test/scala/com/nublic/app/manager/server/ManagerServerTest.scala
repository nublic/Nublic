package com.nublic.app.manager.server

import org.scalatra._
import org.scalatra.test.scalatest._
import org.junit.runner.RunWith
import org.scalatest.matchers._
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner]) // makes test run with Maven Surefire
class ManagerServerTests extends ScalatraFunSuite with ShouldMatchers {
  addServlet(classOf[ManagerServer], "/*")
  // addFilter(classOf[ManagerServer], "/*")

  test("GET / returns status 200") {
    get("/") {
      status should equal(200)
    }
  }
}
