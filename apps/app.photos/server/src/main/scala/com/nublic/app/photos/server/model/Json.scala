package com.nublic.app.photos.server.model

case class JsonPhotosWithCount(val row_count: Long, val photos: List[JsonPhoto])
case class JsonPhoto(val id: Long, val title: String, val date: Long)
case class JsonAlbum(val id: Long, val name: String)