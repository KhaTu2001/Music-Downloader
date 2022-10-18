package com.ddona.music_download_ms3_tunk.user_case

import com.ddona.music_download_ms3_tunk.reponsitory.MusicRepository

class deleteMusicFromPlaylistUserCase(private val repo:MusicRepository) {
     operator fun invoke(playlistID:Int,id:String){
        repo.deleteMusicSongFromPlaylist(id,playlistID)
    }

}