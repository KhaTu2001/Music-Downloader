package com.ddona.music_download_ms3_tunk.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlistMusic")
data class playlistMusic(
    @PrimaryKey(autoGenerate = true)
    val playList_ID: Int? = null,
    val playlistName: String,
)
