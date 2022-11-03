package com.ddona.music_download_ms3_tunk.user_case

import com.ddona.music_download_ms3_tunk.model.Data
import com.ddona.music_download_ms3_tunk.model.songDownloaded
import com.ddona.music_download_ms3_tunk.reponsitory.MusicRepository

class addMusicToDownloadlistUserCase(private val repo:MusicRepository) {
    suspend operator fun invoke(songDownloaded: songDownloaded){
        repo.addMusicToDownloadlist(songDownloaded)
    }

}