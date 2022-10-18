package com.ddona.music_download_ms3_tunk.user_case

import com.ddona.music_download_ms3_tunk.reponsitory.MusicRepository

class deleteMusicUserCase(private val repo:MusicRepository) {
     operator fun invoke(id:Int){
        repo.deleteMusicSong(id)
    }

}