package com.nublic.app.photos.server

import scala.collection.JavaConversions._
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer
import org.apache.solr.client.solrj.SolrQuery
import org.apache.solr.client.solrj.SolrRequest
import org.apache.solr.common.SolrInputDocument

object Solr {
  val SOLR_SERVER_URL = "http://localhost:8080/solr"
  val solrServer = new CommonsHttpSolrServer(SOLR_SERVER_URL);
  
  def getMimeType(filepath: String): String = {
    var query = new SolrQuery("path:\"" + filepath + "\"")
    query.setFields("mime")
    query.setRows(1)
    val response = solrServer.query(query)
    val docs = response.getResults()
      
    if (docs.isEmpty()) {
      "unknown"
    } else {
      val doc = docs.get(0)
      doc.get("mime").asInstanceOf[String]
    }
  }
  
  def getInputDocument(filepath: String): Option[SolrInputDocument] = {
    var query = new SolrQuery("path:\"" + filepath + "\"")
    query.setRows(1)
    val response = solrServer.query(query)
    val docs = response.getResults()
    if (docs.isEmpty()) {
      None
    } else {
      val doc = docs.get(0)
      val input = new SolrInputDocument()
      for (field_name <- doc.getFieldNames()) {
        for (field_value <- doc.getFieldValues(field_name)) {
          input.addField(field_name, field_value)
        }
      }
      Some(input)
    }
  }
  
  def update(doc: SolrInputDocument) = {
    solrServer.add(doc)
    solrServer.commit()
  }
}