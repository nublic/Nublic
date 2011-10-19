package com.nublic.app.music.server

import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer
import org.apache.solr.client.solrj.SolrQuery
import org.apache.solr.client.solrj.SolrRequest

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
}