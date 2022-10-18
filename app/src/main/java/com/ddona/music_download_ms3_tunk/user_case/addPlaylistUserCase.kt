package com.ddona.music_download_ms3_tunk.user_case

import com.ddona.music_download_ms3_tunk.model.playlistMusic
import com.ddona.music_download_ms3_tunk.reponsitory.MusicRepository

class addPlaylistUserCase(private val repo:MusicRepository) {
    suspend operator fun invoke(playlistMusic: playlistMusic){
        repo.addPlaylist(playlistMusic)
    }

}