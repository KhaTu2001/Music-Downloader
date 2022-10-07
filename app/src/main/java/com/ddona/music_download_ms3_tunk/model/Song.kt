package com.ddona.music_download_ms3_tunk.model

data class Song(
    val currentOffset: Int,
    val `data`: List<Data>,
    val status: String
)