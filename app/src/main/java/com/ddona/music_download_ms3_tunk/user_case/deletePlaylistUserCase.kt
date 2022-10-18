package com.ddona.music_download_ms3_tunk.user_case

import com.ddona.music_download_ms3_tunk.reponsitory.MusicRepository

class deletePlaylistUserCase(private val repo:MusicRepository) {
     operator fun invoke(id:Int){
        repo.deletePlaylist(id)
    }

}