package com.nublic.app.browser.server

import org.apache.solr.client.solrj.impl.HttpSolrServer
import org.apache.solr.client.solrj.SolrQuery
import org.apache.solr.client.solrj.SolrRequest

object Solr {
  val SOLR_SERVER_URL = "http://localhost:8080/solr"
  val solrServer = new HttpSolrServer(SOLR_SERVER_URL);
  
  def getMimeType(filepath: String): Option[String] = {
    var query = new SolrQuery("path:\"" + filepath + "\"")
    query.setFields("mime")
    query.setRows(1)
    val response = solrServer.query(query)
    val docs = response.getResults()
      
    if (docs.isEmpty()) {
      None
    } else {
      val doc = docs.get(0)
      Some(doc.get("mime").asInstanceOf[String])
    }
  }
}
