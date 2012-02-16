package com.nublic.app.music.server.model

case class JsonArtist(val id: Long, val name: String,
    val songs: Long, val discs: Long)
case class JsonArtistWithCount(val row_count: Long,
    val artists: List[JsonArtist])
    
case class JsonAlbum(val id: Long, val name: String, val songs: Long)
case class JsonAlbumsWithCount(val row_count: Long,
    val albums: List[JsonAlbum])

case class JsonSong(val id: Long, val title: String,
    val artist_id: Long, val album_id: Long, val length: Long,
    val disc_no: Option[Int], val track: Option[Int])
case class JsonSongsWithCount(val row_count: Long,
    val songs: List[JsonSong])

case class JsonCollection(val id: Long, val name: String)
case class JsonPlaylist(val id: Long, val name: String)