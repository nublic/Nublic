package com.nublic.app.downloads.server.model

case class JsonDownload(val id: Long, 
                        val source: String, val target: String,
                        val status: String, 
                        val downloadSpeed: Option[Long],
                        val uploadSpeed: Option[Long],
                        val totalSize: Option[Long],
                        val downloadedSize: Option[Long],
                        val uploadedSize: Option[Long])
