package com.ddona.music_download_ms3_tunk.user_case

import com.ddona.music_download_ms3_tunk.reponsitory.MusicRepository

class updatePlaylistUserCase(private val repo:MusicRepository) {
     operator fun invoke(playlistMusic: String,id:Int){
        repo.updatePlayListName(playlistMusic,id)
    }

}