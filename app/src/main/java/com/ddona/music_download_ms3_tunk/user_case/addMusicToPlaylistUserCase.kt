package com.ddona.music_download_ms3_tunk.user_case

import com.ddona.music_download_ms3_tunk.model.Data
import com.ddona.music_download_ms3_tunk.reponsitory.MusicRepository

class addMusicToPlaylistUserCase(private val repo:MusicRepository) {
    suspend operator fun invoke(data: Data){
        repo.addMusicToPlaylist(data)
    }

}