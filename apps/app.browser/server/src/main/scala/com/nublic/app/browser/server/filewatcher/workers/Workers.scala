package com.nublic.app.browser.server.filewatcher.workers

import com.nublic.app.browser.server.filewatcher.DocumentWorker
import scala.collection.mutable.LinkedList

object Workers {
  // List here all available workers in the system
  val workers = List(OfficeWorker, PdfWorker, ImageWorker)
  
  // Internal maps
  private var _byMimeType: scala.collection.mutable.Map[String, DocumentWorker] = scala.collection.mutable.Map()  
  private var _byViewName: scala.collection.mutable.Map[String, List[DocumentWorker]] = scala.collection.mutable.Map()
  
  for(worker <- workers) {
    for(mimeType <- worker.supportedMimeTypes) {
      _byMimeType.update(mimeType, worker)
    }
    for(view <- worker.supportedViews) {
      val prevViews = _byViewName.getOrElse(view, Nil)
      _byViewName.update(view, worker :: prevViews)
    }
  }
  
  val byMimeType: scala.collection.Map[String, DocumentWorker] = _byMimeType.toMap
  val byViewName: scala.collection.Map[String, List[DocumentWorker]] = _byViewName.toMap
}